package com.example.temp.machine.dto.request;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.temp.machine.domain.BodyPart;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MachineSearchUsingBodyPartRequestTest {

    @Test
    @DisplayName("생성자의 순서를 확인한다")
    void create() {
        BodyPart bodyPart = BodyPart.BACK;
        MachineSearchUsingBodyPartRequest result = new MachineSearchUsingBodyPartRequest(bodyPart);

        assertThat(result.bodyPart()).isEqualTo(bodyPart);
    }
}