package com.example.temp.machine.dto.response;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.temp.machine.domain.BodyPart;
import com.example.temp.machine.domain.Machine;
import com.example.temp.machine.domain.MachineBodyPart;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MachineInfoTest {

    @Test
    @DisplayName("머신을 입력받아 객체를 생성한다.")
    void createFromMachineSuccess() throws Exception {
        // given
        BodyPart bodyPart1 = BodyPart.BACK;
        BodyPart bodyPart2 = BodyPart.CARDIO;
        MachineBodyPart machineBodyPart1 = MachineBodyPart.create(bodyPart1);
        MachineBodyPart machineBodyPart2 = MachineBodyPart.create(bodyPart2);

        Machine machine = Machine.builder()
            .name("name")
            .machineBodyParts(List.of(machineBodyPart1, machineBodyPart2))
            .build();

        // when
        MachineInfo result = MachineInfo.from(machine);

        // then
        assertThat(result.name()).isEqualTo(machine.getName());
        assertThat(result.bodyParts()).hasSize(2)
            .contains(bodyPart1, bodyPart2);
    }
}