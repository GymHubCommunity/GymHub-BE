package com.example.temp.machine.dto.request;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.temp.machine.domain.BodyPart;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MachineCreateRequestTest {

    @Test
    @DisplayName("생성자 순서가 정확한지 테스트한다.")
    void createSuccess() throws Exception {
        // given
        String name = "name";
        List<BodyPart> bodyParts = List.of(BodyPart.CARDIO, BodyPart.LEG);

        // when
        MachineCreateRequest result = new MachineCreateRequest(name, bodyParts);

        // then
        assertThat(result.name()).isEqualTo(name);
        assertThat(result.bodyParts()).hasSize(2)
            .contains(BodyPart.CARDIO, BodyPart.LEG);
    }

}