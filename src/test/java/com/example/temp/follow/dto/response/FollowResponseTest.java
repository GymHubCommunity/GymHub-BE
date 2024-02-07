package com.example.temp.follow.dto.response;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.temp.follow.domain.FollowStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FollowResponseTest {

    @Test
    @DisplayName("FollowResponse를 생성한다")
    void create() throws Exception {
        // given
        long id = 1L;
        FollowStatus status = FollowStatus.PENDING;

        // when
        FollowResponse followResponse = new FollowResponse(id, status);

        // then
        assertThat(followResponse.id()).isEqualTo(id);
        assertThat(followResponse.status()).isEqualTo(status);
    }
}