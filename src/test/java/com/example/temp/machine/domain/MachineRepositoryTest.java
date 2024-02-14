package com.example.temp.machine.domain;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class MachineRepositoryTest {

    @Autowired
    MachineRepository machineRepository;

    @Autowired
    EntityManager em;

    @Test
    @DisplayName("신체부위에 해당되는 모든 운동기구를 가져온다.")
    void findAllByBodyPart() throws Exception {

        // given
        Machine target1 = saveMachine("머신1", BodyPart.BACK);
        Machine another = saveMachine("머신2", BodyPart.CARDIO);
        Machine target2 = saveMachine("머신3", BodyPart.BACK);
        em.flush();
        em.clear();

        // when
        List<Machine> results = machineRepository.findAllByBodyPart(BodyPart.BACK);

        // then
        assertThat(results).hasSize(2)
            .extracting("name")
            .contains(target1.getName(), target2.getName());
    }

    @Test
    @DisplayName("신체부위에 해당되는 운동기구가 없으면 비어있는 리스트를 가져온다.")
    void findAllThatResultIsEmpty() throws Exception {

        // given
        saveMachine("머신1", BodyPart.BACK);
        saveMachine("머신2", BodyPart.CARDIO);
        saveMachine("머신3", BodyPart.BACK);
        em.flush();
        em.clear();

        // when
        List<Machine> results = machineRepository.findAllByBodyPart(BodyPart.SHOULDER);

        // then
        assertThat(results).isEmpty();
    }
    private Machine saveMachine(String name, BodyPart bodyPart) {
        MachineBodyPart machineBodyPart = MachineBodyPart.create(bodyPart);
        Machine machine = Machine.builder()
            .name(name)
            .machineBodyParts(List.of(machineBodyPart))
            .build();
        machineBodyPart.setMachine(machine);

        machineRepository.save(machine);
        return machine;
    }
}