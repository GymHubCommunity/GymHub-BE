package com.example.temp.machine.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MachineTest {

    @Test
    @DisplayName("머신을 생성한다.")
    void create() throws Exception {
        // given
        String name = "머신이름";
        BodyPart bodyPart1 = BodyPart.BACK;
        BodyPart bodyPart2 = BodyPart.LEG;
        List<BodyPart> bodyParts = List.of(bodyPart1, bodyPart2);

        // when
        Machine machine = Machine.create(name, bodyParts);

        // then
        List<BodyPart> relatedBodyParts = machine.getMachineBodyParts().stream()
            .map(MachineBodyPart::getBodyPart)
            .toList();
        assertThat(machine.getName()).isEqualTo(name);
        assertThat(relatedBodyParts).hasSize(2)
            .containsExactly(bodyPart1, bodyPart2);
    }

}