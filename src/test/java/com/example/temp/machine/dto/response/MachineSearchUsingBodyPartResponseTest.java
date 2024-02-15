package com.example.temp.machine.dto.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MachineSearchUsingBodyPartResponseTest {

    @Test
    @DisplayName("생성자의 순서를 확인한다")
    void create() {
        // given
        List<MachineInfo> machineInfos = List.of();

        // when
        MachineSearchUsingBodyPartResponse result = new MachineSearchUsingBodyPartResponse(machineInfos);

        // then
        assertThat(result.machines()).isEqualTo(machineInfos);
    }

}