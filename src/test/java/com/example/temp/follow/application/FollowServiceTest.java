package com.example.temp.follow.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.temp.follow.domain.Follow;
import com.example.temp.follow.domain.FollowStatus;
import com.example.temp.follow.response.FollowResponse;
import com.example.temp.member.domain.FollowStrategy;
import com.example.temp.member.domain.Member;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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
    @DisplayName("fromMember가 toMember를 팔로우한다.")
    void followSuccess() throws Exception {
        // given
        Member fromMember = saveMember();
        Member toMember = saveMemberWithFollowStrategy(FollowStrategy.EAGER);

        // when
        FollowResponse response = followService.follow(fromMember.getId(), toMember.getId());

        // then
        assertThat(response.status()).isEqualTo(toMember.getFollowStrategy().getFollowStatus());
        validateFollowResponse(response, fromMember, toMember);
    }

    @Test
    @DisplayName("팔로우를 할 때, target의 전략이 EAGER면 SUCCESS 상태의 follow가 생성된다.")
    void validateCreateSuccessFollowThatTargetStrategyIsEager() throws Exception {
        // given
        Member fromMember = saveMember();
        Member toMember = saveMemberWithFollowStrategy(FollowStrategy.EAGER);

        // when
        FollowResponse response = followService.follow(fromMember.getId(), toMember.getId());

        // then
        assertThat(response.status()).isEqualTo(FollowStatus.SUCCESS);
    }

    @Test
    @DisplayName("팔로우를 할 때, target의 전략이 LAZY면 PENDING 상태의 follow가 생성된다.")
    void validateCreatePendingFollowThatTargetStrategyIsLazy() throws Exception {
        // given
        Member fromMember = saveMember();
        Member toMember = saveMemberWithFollowStrategy(FollowStrategy.LAZY);

        // when
        FollowResponse response = followService.follow(fromMember.getId(), toMember.getId());

        // then
        assertThat(response.status()).isEqualTo(FollowStatus.PENDING);
    }

    @Test
    @DisplayName("이미 팔로우가 되어있는 상태에서 또 팔로우 요청을 보낼 수는 없다")
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
    @DisplayName("이전에 팔로우 취소한 상태에서, 새롭게 팔로우를 한다")
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
    @DisplayName("이전에 팔로우를 거절한 상태에서, fromMember는 다시 팔로우를 한다")
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
    @DisplayName("존재하지 않는 대상에게 팔로우 요청을 할 수 없다")
    void followFailBecauseOfNotExistMember() throws Exception {
        // given
        Member fromMember = saveMember();

        // when & then
        assertThatThrownBy(() -> followService.follow(fromMember.getId(), notExistMemberId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("찾을 수 없는 사용자");
    }


    @Test
    @DisplayName("존재하지 않는 회원은 팔로우 요청을 보낼 수 없다")
    void followFailBecauseExecutorNotFound() throws Exception {
        // given
        Member toMember = saveMember();

        // when & then
        assertThatThrownBy(() -> followService.follow(notExistMemberId, toMember.getId()))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("찾을 수 없는 사용자");
    }

    @Test
    @DisplayName("언팔로우한다.")
    void unfollowSuccess() throws Exception {
        // given
        Member fromMember = saveMember();
        Member target = saveMember();
        Follow follow = saveFollow(fromMember, target, FollowStatus.SUCCESS);

        // when
        followService.unfollow(fromMember.getId(), target.getId());

        // then
        assertThat(follow.getStatus()).isEqualTo(FollowStatus.CANCELED);
    }

    @Test
    @DisplayName("기존에 팔로우 관계가 존재하지 않으면 언팔로우를 할 수 없다.")
    void unfollowFailFollowNotFound() throws Exception {
        // given
        Member fromMember = saveMember();
        Member target = saveMember();

        // when & then
        assertThatThrownBy(() -> followService.unfollow(fromMember.getId(), target.getId()))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("찾을 수 없는 관계");
    }

    @Test
    @DisplayName("존재하지 않는 대상에게 언팔로우를 할 수 없다")
    void unfollowFailTargetNotFound() throws Exception {
        // given
        Member fromMember = saveMember();

        // when & then
        assertThatThrownBy(() -> followService.unfollow(fromMember.getId(), notExistMemberId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("찾을 수 없는 사용자");
    }

    @Test
    @DisplayName("pending 상태의 팔로우 요청을 수락한다")
    void acceptFollowRequestSuccess() throws Exception {
        // given
        Member fromMember = saveMember();
        Member target = saveMember();
        Follow follow = saveFollow(fromMember, target, FollowStatus.PENDING);

        // when
        followService.acceptFollowRequest(target.getId(), follow.getId());

        // then
        assertThat(follow.getStatus()).isEqualTo(FollowStatus.SUCCESS);
    }

    @Test
    @DisplayName("팔로우를 받은 사용자만 팔로우 요청을 수락할 수 있다.")
    void acceptFollowRequestFailInvalidFollowTarget() throws Exception {
        // given
        Member fromMember = saveMember();
        Member target = saveMember();
        Follow follow = saveFollow(fromMember, target, FollowStatus.PENDING);
        Member anotherMember = saveMember();

        // when & then
        assertThatThrownBy(() -> followService.acceptFollowRequest(anotherMember.getId(), follow.getId()))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("권한없음");
    }

    @ParameterizedTest
    @DisplayName("pending 상태의 follow에 대해서만 요청을 수락할 수 있다.")
    @ValueSource(strings = {"SUCCESS", "REJECTED", "CANCELED"})
    void acceptFollowRequestFailInvalidFollowTarget(String statusStr) throws Exception {
        // given
        Member fromMember = saveMember();
        Member target = saveMember();
        Follow follow = saveFollow(fromMember, target, FollowStatus.valueOf(statusStr));

        // when & then
        assertThatThrownBy(() -> followService.acceptFollowRequest(target.getId(), follow.getId()))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("잘못된 상태입니다.");
    }

    @ParameterizedTest
    @DisplayName("상대의 팔로우를 거절한다")
    @ValueSource(strings = {"SUCCESS", "PENDING"})
    void rejectFollowRequest(String prevStatus) throws Exception {
        // given
        Member fromMember = saveMember();
        Member target = saveMember();
        Follow follow = saveFollow(fromMember, target, FollowStatus.valueOf(prevStatus));

        // when
        followService.rejectFollowRequest(target.getId(), follow.getId());

        // then
        assertThat(follow.getStatus()).isEqualTo(FollowStatus.REJECTED);
    }

    @Test
    @DisplayName("팔로우를 받은 사용자만 상대의 팔로우를 거절할 수 있다")
    void rejectFollowRequestFailNoAuthz() throws Exception {
        // given
        Member fromMember = saveMember();
        Member target = saveMember();
        Follow follow = saveFollow(fromMember, target, FollowStatus.PENDING);
        Member anotherMember = saveMember();

        // when & then
        assertThatThrownBy(() -> followService.rejectFollowRequest(anotherMember.getId(), follow.getId()))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("권한없음");
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
        return saveMemberWithFollowStrategy(FollowStrategy.EAGER);
    }

    private Member saveMemberWithFollowStrategy(FollowStrategy followStrategy) {
        Member member = Member.builder()
            .followStrategy(followStrategy)
            .build();
        em.persist(member);
        return member;
    }

}