package com.example.temp.machine.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.temp.machine.domain.BodyPart.BodyCategory;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MachineBodyPartTest {

    @Test
    @DisplayName("MachineBodyPart가 잘 생성되는지 테스트한다.")
    void create() throws Exception {
        // given
        BodyPart bodyPart = BodyPart.BACK;
        // when
        MachineBodyPart machineBodyPart = MachineBodyPart.create(bodyPart);

        // then
        assertThat(machineBodyPart.getBodyPart()).isEqualTo(bodyPart);
        assertThat(machineBodyPart.getMachine()).isNull();
    }

    @Test
    @DisplayName("MachineBodyPart를 처음으로 Machine에 연관짓는다.")
    void relateFirst() throws Exception {
        // given
        BodyPart bodyPart = BodyPart.BACK;
        Machine machine = createMachine();

        MachineBodyPart machineBodyPart = createNotRelatedMachineBodyPart(bodyPart);

        // when
        machineBodyPart.relate(machine);

        // then
        assertThat(machineBodyPart.getBodyPart()).isEqualTo(bodyPart);
        assertThat(machineBodyPart.getMachine()).isEqualTo(machine);
        assertThat(machine.getMachineBodyParts()).hasSize(1)
            .contains(machineBodyPart);
    }

    @Test
    @DisplayName("연관된 Machine을 변경한다.")
    void relateAlready() throws Exception {
        // given
        BodyPart bodyPart = BodyPart.BACK;
        Machine past = createMachine();
        Machine target = createMachine();

        MachineBodyPart machineBodyPart = createRelatedMachineBodyPart(bodyPart, past);

        // when
        machineBodyPart.relate(target);

        // then
        assertThat(machineBodyPart.getBodyPart()).isEqualTo(bodyPart);
        assertThat(machineBodyPart.getMachine()).isEqualTo(target);
        assertThat(target.getMachineBodyParts()).hasSize(1)
            .contains(machineBodyPart);
        assertThat(past.getMachineBodyParts()).isEmpty();
    }
    
    @Test
    @DisplayName("특정 신체 카테고리에 속한 신체부위들을 모두 가져온다.")
    void findAllBelongTo() throws Exception {
        // given
        BodyCategory category = BodyCategory.WHOLE;

        // when
        List<BodyPart> bodyParts = BodyPart.findAllBelongTo(category);

        // then
        for (BodyPart bodyPart : bodyParts) {
            assertThat(bodyPart.getCategory()).isEqualTo(category);
        }
        validateSize(category, bodyParts);
    }

    private static void validateSize(BodyCategory category, List<BodyPart> bodyParts) {
        int cnt = 0;
        for(BodyPart bodyPart : BodyPart.values()) {
            if (bodyPart.getCategory() == category) {
                cnt++;
            }
        }
        assertThat(bodyParts).hasSize(cnt);
    }

    private static MachineBodyPart createRelatedMachineBodyPart(BodyPart bodyPart, Machine past) {
        return MachineBodyPart.builder()
            .bodyPart(bodyPart)
            .machine(past)
            .build();
    }


    private static MachineBodyPart createNotRelatedMachineBodyPart(BodyPart bodyPart) {
        return MachineBodyPart.builder()
            .bodyPart(bodyPart)
            .build();
    }

    private static Machine createMachine() {
        return Machine.builder()
            .machineBodyParts(Collections.emptyList())
            .build();
    }

}