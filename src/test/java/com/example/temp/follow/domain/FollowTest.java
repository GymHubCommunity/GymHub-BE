package com.example.temp.follow.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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
        assertThat(follow.isActive()).isTrue();
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
        assertThat(follow.isActive()).isFalse();
    }

    @ParameterizedTest
    @DisplayName("비활성 상태의 Follow를 활성화한다.")
    @CsvSource({
        "REJECTED, SUCCESS",
        "CANCELED, PENDING"
    })
    void reactiveSuccess(String prevStatusStr, String changedStatusStr) throws Exception {
        // given
        FollowStatus changed = FollowStatus.valueOf(changedStatusStr);
        Follow follow = Follow.builder().status(FollowStatus.valueOf(prevStatusStr)).build();

        // when
        Follow reactiveFollow = follow.reactive(changed);

        // then
        assertThat(reactiveFollow).isEqualTo(follow);
        assertThat(reactiveFollow.getStatus()).isEqualTo(changed);
    }

    @ParameterizedTest
    @DisplayName("활성 상태의 Follow는 활성화시킬 수 없다.")
    @CsvSource({
        "SUCCESS",
        "PENDING"
    })
    void reactiveFailAlreadyActivate(String prevStatusStr) throws Exception {
        // given
        Follow follow = Follow.builder().status(FollowStatus.valueOf(prevStatusStr)).build();

        // when & then
        assertThatThrownBy(() -> follow.reactive(FollowStatus.SUCCESS))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("이미 둘 사이에 관계가 존재합니다.");
    }

    @ParameterizedTest
    @DisplayName("비활성 상태를 입력해 Follow를 활성화시키는 건 불가능하다")
    @CsvSource({
        "CANCELED",
        "REJECTED"
    })
    void reactiveFailInputIsInactivate(String changed) throws Exception {
        // given
        Follow follow = Follow.builder().status(FollowStatus.CANCELED).build();

        // when & then
        assertThatThrownBy(() -> follow.reactive(FollowStatus.valueOf(changed)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("해당 상태로는 변경할 수 없습니다.");
    }
}