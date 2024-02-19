package com.example.temp.follow.dto.response;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.temp.follow.domain.Follow;
import com.example.temp.member.domain.Member;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

class FollowInfoResultTest {

    @Test
    @DisplayName("Following 목록 생성 테스트")
    void createFollowings() throws Exception {
        // given
        Member member1 = Member.builder().build();
        Member member2 = Member.builder().build();

        Follow follow1 = createFollow(member1, member2);
        Follow follow2 = createFollow(member2, member1);

        Slice<Follow> slice = new SliceImpl<>(List.of(follow1, follow2));

        // when
        FollowInfoResult result = FollowInfoResult.createFollowingsResult(slice);

        // then
        assertThat(result.follows()).hasSize(2);
        assertThat(result.hasNext()).isFalse();
    }

    @Test
    @DisplayName("Follower 목록 생성 테스트")
    void createFollowers() throws Exception {
        // given
        Member member1 = Member.builder().build();
        Member member2 = Member.builder().build();

        Follow follow1 = createFollow(member1, member2);
        Follow follow2 = createFollow(member2, member1);

        Slice<Follow> slice = new SliceImpl<>(List.of(follow1, follow2));

        // when
        FollowInfoResult result = FollowInfoResult.createFollowersResult(slice);

        // then
        assertThat(result.follows()).hasSize(2);
        assertThat(result.hasNext()).isFalse();
    }

    private static Follow createFollow(Member member1, Member member2) {
        return Follow.builder()
            .from(member1)
            .to(member2)
            .build();
    }

}