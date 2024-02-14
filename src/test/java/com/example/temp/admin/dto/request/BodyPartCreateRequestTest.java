package com.example.temp.admin.dto.request;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.temp.machine.dto.request.BodyPartCreateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BodyPartCreateRequestTest {

    @Test
    @DisplayName("생성자 순서가 정확한지 테스트한다.")
    void createSuccess() throws Exception {
        // given
        String name = "name";

        // when
        BodyPartCreateRequest result = new BodyPartCreateRequest(name);

        // then
        assertThat(result.name()).isEqualTo(name);
    }

}