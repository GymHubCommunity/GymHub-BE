package com.example.temp.record.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.temp.common.exception.ApiException;
import com.example.temp.common.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SetTest {

    @Test
    @DisplayName("세트를 생성한다.")
    void create() throws Exception {
        // given
        int order = 1;
        int weight = 1;
        int repeatCnt = 1;

        // when
        Set set = createSet(order, weight, repeatCnt);

        // then
        assertThat(set.getOrder()).isEqualTo(order);
        assertThat(set.getWeight()).isEqualTo(weight);
        assertThat(set.getRepeatCnt()).isEqualTo(repeatCnt);
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

    private Set createSet(int order, int weight, int repeatCnt) {
        return Set.builder()
            .order(order)
            .weight(weight)
            .repeatCnt(repeatCnt)
            .build();
    }
}