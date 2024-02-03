package com.example.temp.follow.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class FollowStatusTest {

    @DisplayName("해당 FollowStatus가 활성화 상태라면 true를 반환한다")
    @ParameterizedTest
    @ValueSource(strings = {"SUCCESS", "PENDING"})
    void isValidSuccess(String statusStr) throws Exception {
        // given
        FollowStatus status = FollowStatus.valueOf(statusStr);

        // when & then
        Assertions.assertThat(status.isActive()).isTrue();
    }

    @DisplayName("해당 FollowStatus가 활성화 상태가 아니라면 false를 반환한다")
    @ParameterizedTest
    @ValueSource(strings = {"REJECTED", "CANCELED"})
    void isValidFail(String statusStr) throws Exception {
        // given
        FollowStatus status = FollowStatus.valueOf(statusStr);

        // when & then
        Assertions.assertThat(status.isActive()).isFalse();
    }

}