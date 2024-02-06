package com.example.temp.follow.dto.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FollowInfosTest {

    @Test
    @DisplayName("FollowInfos를 생성한다")
    void create() throws Exception {
        // given
        FollowInfo followInfo1 = new FollowInfo(1L, 1L, "url");
        FollowInfo followInfo2 = new FollowInfo(2L, 2L, "url");
        // when
        FollowInfos result = FollowInfos.from(List.of(followInfo1, followInfo2));

        // then
        assertThat(result.follows()).hasSize(2)
            .contains(followInfo1, followInfo2);
    }

}