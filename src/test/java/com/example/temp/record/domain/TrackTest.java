package com.example.temp.record.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.temp.common.exception.ApiException;
import com.example.temp.common.exception.ErrorCode;
import com.example.temp.machine.domain.BodyPart;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class TrackTest {

    @Test
    @DisplayName("트랙을 생성한다")
    void create() throws Exception {
        // given
        String machineName = "스쿼트 머신";
        List<SetInTrack> setInTracks = List.of(createSet(1));

        // when
        Track track = Track.createWithoutRecord(machineName, BodyPart.HIP, setInTracks);

        // then
        assertThat(track.getMachineName()).isEqualTo(machineName);
        assertThat(track.getSetsInTrack()).hasSize(1);
    }

    @ParameterizedTest
    @DisplayName("트랙을 생성할 때, 운동 기구의 이름을 공백으로 할 수는 없다.")
    @ValueSource(strings = {" ", "", "  "})
    void createFailMachineNameIsBlank(String machineName) throws Exception {
        // given
        List<SetInTrack> setInTracks = List.of(createSet(1));

        // when & then
        assertThatThrownBy(() -> Track.createWithoutRecord(machineName, BodyPart.HIP, setInTracks))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.TRACK_MACHINE_NAME_INVALID.getMessage());
    }

    @Test
    @DisplayName("트랙은 최소 한 개의 세트를 가져야 한다.")
    void createFailEmptySet() throws Exception {
        // given
        String machineName = "스쿼트 머신";
        List<SetInTrack> setInTracks = Collections.emptyList();

        // when & then
        assertThatThrownBy(() -> Track.createWithoutRecord(machineName, BodyPart.HIP, setInTracks))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.SET_CANT_EMPTY.getMessage());
    }

    @ParameterizedTest
    @DisplayName("트랙 내 세트는 1부터 N까지 구성되어야 한다.")
    @ValueSource(strings = {"1,3", "2", "1,2,4"})
    void createFailSetOrderInvalid(String numsBeforeSplit) throws Exception {
        // given
        String machineName = "스쿼트 머신";
        List<SetInTrack> setInTracks = Arrays.stream(numsBeforeSplit.split(","))
            .map(Integer::parseInt)
            .map(this::createSet)
            .toList();

        // when & then
        assertThatThrownBy(() -> Track.createWithoutRecord(machineName, BodyPart.HIP, setInTracks))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("트랙 내 세트들의 순서는 1부터 순차적으로 올라가야 합니다.");
    }

    SetInTrack createSet(int order) {
        return SetInTrack.builder()
            .repeatCnt(1)
            .order(order)
            .weight(1)
            .build();
    }
}