package com.example.temp.machine.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
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

    @Test
    @DisplayName("BodyPart에 속한 모든 머신을 가져온다.")
    void findAllBelongTo() throws Exception {
        // given
        saveMachine("레그 프레스", BodyPart.LEG);
        saveMachine("레그 컬", List.of(BodyPart.LEG, BodyPart.SHOULDER));
        saveMachine("벤치 프레스", BodyPart.SHOULDER);
        saveMachine("시티드 체스트 프레스 머신", BodyPart.CHEST);
        em.flush();
        em.clear();

        // when
        List<Machine> machines = machineRepository.findAllBelongTo(List.of(BodyPart.LEG, BodyPart.SHOULDER));
        // then
        assertThat(machines).hasSize(3)
            .extracting(
                Machine::getName,
                extractBodyParts())
            .containsExactlyInAnyOrder(
                tuple("레그 프레스", Set.of(BodyPart.LEG)),
                tuple("레그 컬", Set.of(BodyPart.LEG, BodyPart.SHOULDER)),
                tuple("벤치 프레스", Set.of(BodyPart.SHOULDER))
            );
    }

    private static Function<Machine, Set<BodyPart>> extractBodyParts() {
        return machine -> machine.getMachineBodyParts().stream()
            .map(MachineBodyPart::getBodyPart)
            .collect(Collectors.toSet());
    }

    @Test
    @DisplayName("BodyPart에 속한 머신이 없으면 비어있는 리스트를 반환한다.")
    void findAllBelongToThatResultIsEmpty() throws Exception {
        // given
        saveMachine("레그 프레스", BodyPart.LEG);
        em.flush();
        em.clear();

        // when
        List<Machine> machines = machineRepository.findAllBelongTo(List.of(BodyPart.SHOULDER));
        // then
        assertThat(machines).isEmpty();
    }

    private Machine saveMachine(String name, BodyPart bodyPart) {
        return saveMachine(name, List.of(bodyPart));
    }

    private Machine saveMachine(String name, List<BodyPart> bodyParts) {
        List<MachineBodyPart> machineBodyParts = bodyParts.stream()
            .map(MachineBodyPart::create)
            .toList();
        Machine machine = Machine.builder()
            .name(name)
            .machineBodyParts(machineBodyParts)
            .build();
        for (MachineBodyPart machineBodyPart : machineBodyParts) {
            machineBodyPart.relate(machine);
        }
        machineRepository.save(machine);
        return machine;
    }
}