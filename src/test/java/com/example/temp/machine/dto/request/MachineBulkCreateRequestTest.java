package com.example.temp.machine.dto.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MachineBulkCreateRequestTest {

    @Test
    @DisplayName("생성자의 순서를 테스트한다.")
    void create() throws Exception {
        // given
        MachineCreateRequest req1 = new MachineCreateRequest("이름", Collections.emptyList());

        // when
        MachineBulkCreateRequest result = new MachineBulkCreateRequest(List.of(req1));

        // then
        assertThat(result.machines()).hasSize(1)
            .contains(req1);
    }
}