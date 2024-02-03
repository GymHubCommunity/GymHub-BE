package com.example.temp.follow.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.temp.follow.domain.Follow;
import com.example.temp.follow.domain.FollowStatus;
import com.example.temp.follow.response.FollowResponse;
import com.example.temp.member.domain.Member;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class FollowServiceTest {

    @Autowired
    FollowService followService;

    @Autowired
    EntityManager em;

    long notExistMemberId = 999_999_999L;

    @Test
    @DisplayName("from 사용자가 to 사용자를 팔로우한다.")
    void followSuccess() throws Exception {
        // given
        Member fromMember = saveMember();
        Member toMember = saveMember();

        // when
        FollowResponse response = followService.follow(fromMember.getId(), toMember.getId());

        // then
        assertThat(response.status()).isEqualTo(FollowStatus.SUCCESS);
        validateFollowResponse(response, fromMember, toMember);
    }

    @Test
    @DisplayName("이미 팔로우가 되어있는 사용자에게 팔로우 요청을 보낼 수 없다")
    void followFailAlreadyFollowSuccess() throws Exception {
        // given
        Member fromMember = saveMember();
        Member toMember = saveMember();
        saveFollow(fromMember, toMember, FollowStatus.SUCCESS);

        // when & then
        assertThatThrownBy(() -> followService.follow(fromMember.getId(), toMember.getId()))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("이미 둘 사이에 관계가 존재합니다.");
    }

    @Test
    @DisplayName("이미 팔로우 요청을 보내 둔 사용자에게 팔로우 요청을 보낼 수 없다")
    void followFailAlreadyFollowPending() throws Exception {
        // given
        Member fromMember = saveMember();
        Member toMember = saveMember();
        saveFollow(fromMember, toMember, FollowStatus.PENDING);

        // when
        assertThatThrownBy(() -> followService.follow(fromMember.getId(), toMember.getId()))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("이미 둘 사이에 관계가 존재합니다.");
    }

    @Test
    @DisplayName("기존에 fromMember가 toMember를 팔로우 취소한 상태에서, 새롭게 팔로우를 한다")
    void followSuccessThatAlreadyUnfollow() throws Exception {
        // given
        Member fromMember = saveMember();
        Member toMember = saveMember();
        saveFollow(fromMember, toMember, FollowStatus.CANCELED);

        // when
        FollowResponse response = followService.follow(fromMember.getId(), toMember.getId());

        // then
        assertThat(response.status()).isEqualTo(FollowStatus.SUCCESS);
        validateFollowResponse(response, fromMember, toMember);
    }

    @Test
    @DisplayName("기존에 toMember가 fromMember의 팔로우를 거절한 상태에서, fromMember는 다시 팔로우를 한다")
    void followSuccessThatAlreadyRejected() throws Exception {
        // given
        Member fromMember = saveMember();
        Member toMember = saveMember();
        saveFollow(fromMember, toMember, FollowStatus.REJECTED);

        // when
        FollowResponse response = followService.follow(fromMember.getId(), toMember.getId());

        // then
        assertThat(response.status()).isEqualTo(FollowStatus.SUCCESS);
        validateFollowResponse(response, fromMember, toMember);
    }

    @Test
    @DisplayName("존재하지 않는 toMember에게 팔로우 요청을 할 수 없다")
    void followFailBecauseOfNotExistMember() throws Exception {
        // given
        Member fromMember = saveMember();

        // when & then
        assertThatThrownBy(() -> followService.follow(fromMember.getId(), notExistMemberId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("찾을 수 없는 사용자");
    }


    @Test
    @DisplayName("존재하지 않는 fromMember로는 팔로우 요청을 보낼 수 없다")
    void followFailBecauseExecutorNotFound() throws Exception {
        // given
        Member toMember = saveMember();

        // when & then
        assertThatThrownBy(() -> followService.follow(notExistMemberId, toMember.getId()))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("찾을 수 없는 사용자");
    }

    private void validateFollowResponse(FollowResponse response, Member fromMember, Member toMember) {
        Follow result = em.find(Follow.class, response.id());
        assertThat(result.getId()).isNotNull();
        assertThat(result.getFrom()).isEqualTo(fromMember);
        assertThat(result.getTo()).isEqualTo(toMember);
    }

    private Follow saveFollow(Member fromMember, Member toMember, FollowStatus status) {
        Follow follow = Follow.builder()
            .from(fromMember)
            .to(toMember)
            .status(status)
            .build();
        em.persist(follow);
        return follow;
    }

    private Member saveMember() {
        Member member = Member.builder().build();
        em.persist(member);
        return member;
    }

}