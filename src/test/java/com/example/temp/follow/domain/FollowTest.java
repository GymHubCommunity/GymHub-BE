package com.example.temp.follow.domain;

import static com.example.temp.exception.ErrorCode.FOLLOW_ALREADY_RELATED;
import static com.example.temp.exception.ErrorCode.FOLLOW_INACTIVE;
import static com.example.temp.exception.ErrorCode.FOLLOW_NOT_PENDING;
import static com.example.temp.exception.ErrorCode.FOLLOW_STATUS_CHANGE_NOT_ALLOWED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.temp.exception.ApiException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class FollowTest {

    @DisplayName("해당 Follow 엔티티가 유효한 상태라면 true를 반환한다")
    @ParameterizedTest
    @ValueSource(strings = {"APPROVED", "PENDING"})
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
        "REJECTED, APPROVED",
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
        "APPROVED",
        "PENDING"
    })
    void reactiveFailAlreadyActivate(String prevStatusStr) throws Exception {
        // given
        Follow follow = Follow.builder().status(FollowStatus.valueOf(prevStatusStr)).build();

        // when & then
        assertThatThrownBy(() -> follow.reactive(FollowStatus.APPROVED))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(FOLLOW_ALREADY_RELATED.getMessage());
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
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(FOLLOW_STATUS_CHANGE_NOT_ALLOWED.getMessage());
    }

    @ParameterizedTest
    @DisplayName("언팔로우한다.")
    @CsvSource({
        "APPROVED",
        "PENDING"
    })
    void unfollowSuccess(String statusStr) throws Exception {
        // given
        Follow follow = Follow.builder()
            .status(FollowStatus.valueOf(statusStr))
            .build();

        // when
        follow.unfollow();
        // then
        assertThat(follow.getStatus()).isEqualTo(FollowStatus.CANCELED);
    }

    @ParameterizedTest
    @DisplayName("이미 비활성화된 팔로우에 대해서는 언팔로우를 할 수 없다.")
    @CsvSource({
        "CANCELED",
        "REJECTED"
    })
    void unfollowFailAlreadyInactivate(String statusStr) throws Exception {
        // given
        Follow follow = Follow.builder()
            .status(FollowStatus.valueOf(statusStr))
            .build();

        // when & then
        assertThatThrownBy(() -> follow.unfollow())
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(FOLLOW_INACTIVE.getMessage());
    }

    @ParameterizedTest
    @DisplayName("팔로우를 거절한다.")
    @CsvSource({
        "APPROVED",
        "PENDING"
    })
    void rejectSuccess(String statusStr) throws Exception {
        // given
        Follow follow = Follow.builder()
            .status(FollowStatus.valueOf(statusStr))
            .build();

        // when
        follow.reject();
        // then
        assertThat(follow.getStatus()).isEqualTo(FollowStatus.REJECTED);
    }

    @ParameterizedTest
    @DisplayName("이미 비활성화된 팔로우에 대해서는 거절을 할 수 없다.")
    @CsvSource({
        "CANCELED",
        "REJECTED"
    })
    void rejectFailAlreadyInactivate(String statusStr) throws Exception {
        // given
        Follow follow = Follow.builder()
            .status(FollowStatus.valueOf(statusStr))
            .build();

        // when & then
        assertThatThrownBy(() -> follow.reject())
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(FOLLOW_INACTIVE.getMessage());
    }

    @Test
    @DisplayName("pending 상태의 follow를 수락한다")
    void acceptSuccess() throws Exception {
        // given
        Follow follow = Follow.builder()
            .status(FollowStatus.PENDING)
            .build();

        // when
        follow.accept();

        // then
        assertThat(follow.getStatus()).isEqualTo(FollowStatus.APPROVED);
    }

    @ParameterizedTest
    @DisplayName("pending 상태의 follow에 대해서만 요청을 수락할 수 있다.")
    @ValueSource(strings = {"APPROVED", "REJECTED", "CANCELED"})
    void acceptFailInvalidFollowType(String statusStr) throws Exception {
        // given
        Follow follow = Follow.builder()
            .status(FollowStatus.valueOf(statusStr))
            .build();

        // when & then
        assertThatThrownBy(() -> follow.accept())
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(FOLLOW_NOT_PENDING.getMessage());
    }
}