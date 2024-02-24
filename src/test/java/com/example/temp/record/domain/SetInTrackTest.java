package com.example.temp.record.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.temp.common.exception.ApiException;
import com.example.temp.common.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SetInTrackTest {

    @Test
    @DisplayName("세트를 생성한다.")
    void create() throws Exception {
        // given
        int order = 1;
        int weight = 1;
        int repeatCnt = 1;

        // when
        SetInTrack setInTrack = createSet(order, weight, repeatCnt);

        // then
        assertThat(setInTrack.getOrder()).isEqualTo(order);
        assertThat(setInTrack.getWeight()).isEqualTo(weight);
        assertThat(setInTrack.getRepeatCnt()).isEqualTo(repeatCnt);
    }

    @Test
    @DisplayName("세트는 1이상의 순서를 가져야 한다.")
    void createFailOrderInvalid() throws Exception {
        // given
        int order = 0;
        int weight = 1;
        int repeatCnt = 1;

        // when & then
        assertThatThrownBy(() -> createSet(order, weight, repeatCnt))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Track 내 Set의 순서는 0보다 커야 합니다.");
    }

    @Test
    @DisplayName("세트는 0 이상의 무게를 가져야 한다.")
    void createFailWeightInvalid() throws Exception {
        // given
        int order = 1;
        int weight = -1;
        int repeatCnt = 1;

        // when & then
        assertThatThrownBy(() -> createSet(order, weight, repeatCnt))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.SET_WEIGHT_INVALID.getMessage());
    }

    @Test
    @DisplayName("세트는 0 이상의 반복 횟수를 가져야 한다.")
    void createFailRepeatCntInvalid() throws Exception {
        // given
        int order = 1;
        int weight = 1;
        int repeatCnt = -1;

        // when & then
        assertThatThrownBy(() -> createSet(order, weight, repeatCnt))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.SET_REPEAT_CNT_INVALID.getMessage());
    }

    @Test
    @DisplayName("세트를 복사한다.")
    void copy() throws Exception {
        // given
        int order = 1;
        int weight = 1;
        int repeatCnt = 1;
        SetInTrack original = createSet(order, weight, repeatCnt);

        // when
        SetInTrack copy = original.copy();

        // then
        assertThat(copy.getOrder()).isEqualTo(original.getOrder());
        assertThat(copy.getWeight()).isEqualTo(original.getWeight());
        assertThat(copy.getRepeatCnt()).isEqualTo(original.getRepeatCnt());
    }

    private SetInTrack createSet(int order, int weight, int repeatCnt) {
        return SetInTrack.builder()
            .order(order)
            .weight(weight)
            .repeatCnt(repeatCnt)
            .build();
    }
}