package com.example.temp.admin.dto.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MachineCreateRequestTest {

    @Test
    @DisplayName("생성자 순서가 정확한지 테스트한다.")
    void createSuccess() throws Exception {
        // given
        String name = "name";
        List<String> bodyParts = List.of("등", "배");

        // when
        MachineCreateRequest result = new MachineCreateRequest(name, bodyParts);

        // then
        assertThat(result.name()).isEqualTo(name);
        assertThat(result.bodyParts()).hasSize(2)
            .contains("등", "배");
    }

}