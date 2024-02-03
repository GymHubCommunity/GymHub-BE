package com.example.temp.follow.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class FollowTest {

    @DisplayName("해당 Follow 엔티티가 유효한 상태라면 true를 반환한다")
    @ParameterizedTest
    @ValueSource(strings = {"SUCCESS", "PENDING"})
    void isValidSuccess(String statusStr) throws Exception {
        // given
        Follow follow = Follow.builder()
            .status(FollowStatus.valueOf(statusStr))
            .build();

        // when & then
        Assertions.assertThat(follow.isValid()).isTrue();
    }

    @DisplayName("해당 Follow 엔티티가 유효한 상태가 아니라면 false를 반환한다")
    @ParameterizedTest
    @ValueSource(strings = {"REJECTED", "CANCELED"})
    void isValidFail(String statusStr) throws Exception {
        // given
        Follow follow = Follow.builder()
            .status(FollowStatus.valueOf(statusStr))
            .build();

        // when & then
        Assertions.assertThat(follow.isValid()).isFalse();
    }
}