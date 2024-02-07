package com.example.temp.follow.application;

import static com.example.temp.exception.ErrorCode.AUTHENTICATED_FAIL;
import static com.example.temp.exception.ErrorCode.AUTHORIZED_FAIL;
import static com.example.temp.exception.ErrorCode.FOLLOW_ALREADY_RELATED;
import static com.example.temp.exception.ErrorCode.FOLLOW_NOT_FOUND;
import static com.example.temp.exception.ErrorCode.FOLLOW_NOT_PENDING;
import static com.example.temp.exception.ErrorCode.FOLLOW_SELF_FAIL;
import static com.example.temp.exception.ErrorCode.MEMBER_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.temp.exception.ApiException;
import com.example.temp.follow.domain.Follow;
import com.example.temp.follow.domain.FollowStatus;
import com.example.temp.follow.dto.response.FollowInfo;
import com.example.temp.follow.response.FollowResponse;
import com.example.temp.member.domain.FollowStrategy;
import com.example.temp.member.domain.Member;
import com.example.temp.member.infrastructure.nickname.Nickname;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
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


    /**
     * 닉네임을 만들 때 중복을 제거하기 위해 사용합니다. ex. 닉네임0, 닉네임1 .... 과 같은 방식으로 닉네임을 순차적으로 생성하도록 돕습니다.
     */
    int globalIdx = 0;

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
    @DisplayName("자기 자신은 팔로우할 수 없다.")
    void followFailBecauseOfFollowersAndFollowingsIsSame() throws Exception {
        // given
        Member sameMember = saveMember();

        // when
        assertThatThrownBy(() -> followService.follow(sameMember.getId(), sameMember.getId()))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(FOLLOW_SELF_FAIL.getMessage());
    }

    @Test
    @DisplayName("팔로우를 할 때, target의 전략이 EAGER면 APPROVED 상태의 follow가 생성된다.")
    void validateCreateSuccessFollowThatTargetStrategyIsEager() throws Exception {
        // given
        Member fromMember = saveMember();
        Member toMember = saveMemberWithFollowStrategy(FollowStrategy.EAGER);

        // when
        FollowResponse response = followService.follow(fromMember.getId(), toMember.getId());

        // then
        assertThat(response.status()).isEqualTo(FollowStatus.APPROVED);
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
        saveFollow(fromMember, toMember, FollowStatus.APPROVED);

        // when & then
        assertThatThrownBy(() -> followService.follow(fromMember.getId(), toMember.getId()))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(FOLLOW_ALREADY_RELATED.getMessage());
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
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(FOLLOW_ALREADY_RELATED.getMessage());
    }

    @ParameterizedTest
    @DisplayName("이전에 팔로우가 비활성화(CANCELED, REJECTED)된 상태에서, 새롭게 팔로우를 한다")
    @ValueSource(strings = {"CANCELED", "REJECTED"})
    void followSuccessThatAlreadyUnfollow(String statusStr) throws Exception {
        // given
        Member fromMember = saveMember();
        Member toMember = saveMember();
        saveFollow(fromMember, toMember, FollowStatus.valueOf(statusStr));

        // when
        FollowResponse response = followService.follow(fromMember.getId(), toMember.getId());

        // then
        assertThat(response.status()).isEqualTo(FollowStatus.APPROVED);
        validateFollowResponse(response, fromMember, toMember);
    }

    @Test
    @DisplayName("존재하지 않는 대상에게 팔로우 요청을 할 수 없다")
    void followFailBecauseOfNotExistMember() throws Exception {
        // given
        Member fromMember = saveMember();

        // when & then
        assertThatThrownBy(() -> followService.follow(fromMember.getId(), notExistMemberId))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(MEMBER_NOT_FOUND.getMessage());
    }


    @Test
    @DisplayName("로그인이 제대로 되지 않은 회원은 팔로우 요청을 보낼 수 없다")
    void followFailBecauseExecutorNotFound() throws Exception {
        // given
        Member toMember = saveMember();

        // when & then
        assertThatThrownBy(() -> followService.follow(notExistMemberId, toMember.getId()))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(AUTHENTICATED_FAIL.getMessage());
    }

    @Test
    @DisplayName("언팔로우한다.")
    void unfollowSuccess() throws Exception {
        // given
        Member fromMember = saveMember();
        Member target = saveMember();
        Follow follow = saveFollow(fromMember, target, FollowStatus.APPROVED);

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
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(FOLLOW_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("존재하지 않는 대상에게 언팔로우를 할 수 없다")
    void unfollowFailTargetNotFound() throws Exception {
        // given
        Member fromMember = saveMember();

        // when & then
        assertThatThrownBy(() -> followService.unfollow(fromMember.getId(), notExistMemberId))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(MEMBER_NOT_FOUND.getMessage());
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
        assertThat(follow.getStatus()).isEqualTo(FollowStatus.APPROVED);
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
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(AUTHORIZED_FAIL.getMessage());
    }

    @ParameterizedTest
    @DisplayName("pending 상태의 follow에 대해서만 요청을 수락할 수 있다.")
    @ValueSource(strings = {"APPROVED", "REJECTED", "CANCELED"})
    void acceptFollowRequestFailInvalidFollowTarget(String statusStr) throws Exception {
        // given
        Member fromMember = saveMember();
        Member target = saveMember();
        Follow follow = saveFollow(fromMember, target, FollowStatus.valueOf(statusStr));

        // when & then
        assertThatThrownBy(() -> followService.acceptFollowRequest(target.getId(), follow.getId()))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(FOLLOW_NOT_PENDING.getMessage());
    }

    @ParameterizedTest
    @DisplayName("상대의 팔로우를 거절한다")
    @ValueSource(strings = {"APPROVED", "PENDING"})
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
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(AUTHORIZED_FAIL.getMessage());
    }

    @Test
    @DisplayName("특정 사용자가 팔로잉한 사람들을 전부 보여준다.")
    void getFollowingsSuccess() throws Exception {
        // given
        Member target = saveMember();
        int followingCnt = 10;
        List<Member> members = saveMembers(followingCnt);
        List<Follow> targetFollows = saveTargetFollowings(FollowStatus.APPROVED, target, members, 0, followingCnt);

        List<FollowInfo> targetFollowInfos = targetFollows.stream()
            .map(follow -> FollowInfo.of(follow.getTo(), follow.getId()))
            .toList();

        // when
        List<FollowInfo> infos = followService.getFollowings(target.getId(), target.getId());

        // then
        assertThat(infos).hasSize(followingCnt)
            .containsAnyElementsOf(targetFollowInfos);
        assertThat(infos.get(0).memberId()).isNotEqualTo(target.getId());
    }

    @Test
    @DisplayName("특정 사용자의 팔로잉 목록을 가져올 때, APPROVED 상태인 것만 가져온다.")
    void getFollowingsThatStatusIsSuccess() throws Exception {
        // given
        Member target = saveMember();
        int pendingCnt = 1;
        int rejectCnt = 1;
        int canceledCnt = 1;
        int approvedCnt = 10;

        List<Member> members = saveMembers(pendingCnt + rejectCnt + canceledCnt + approvedCnt);

        int idx = 0;
        saveTargetFollowings(FollowStatus.PENDING, target, members, idx, pendingCnt);
        idx += pendingCnt;
        saveTargetFollowings(FollowStatus.REJECTED, target, members, idx, rejectCnt);
        idx += rejectCnt;
        saveTargetFollowings(FollowStatus.CANCELED, target, members, idx, canceledCnt);
        idx += canceledCnt;
        saveTargetFollowings(FollowStatus.APPROVED, target, members, idx, approvedCnt);

        // when
        List<FollowInfo> infos = followService.getFollowings(target.getId(), target.getId());

        // then
        assertThat(infos).hasSize(approvedCnt);
    }

    @Test
    @DisplayName("공개 계정은 누구나 팔로잉 목록을 볼 수 있다.")
    void canSeeFollowingsEveryoneOnPublicAccount() throws Exception {
        // given
        Member publicAccountMember = saveMemberWithAccountPolicy(true);
        em.persist(publicAccountMember);

        Member anotherMember = saveMember();

        // when & then
        assertThatCode(() -> followService.getFollowings(anotherMember.getId(), publicAccountMember.getId()))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("비공개 계정은 자신을 팔로우한 사람들만 팔로잉 목록을 볼 수 있다")
    void canSeeFollowingsThatSpecifyMemberOnPrivateAccount() throws Exception {
        // given
        Member privateMember = saveMemberWithAccountPolicy(true);
        em.persist(privateMember);

        Member anotherMember = saveMember();
        saveFollow(anotherMember, privateMember, FollowStatus.APPROVED);

        // when & then
        assertThatCode(() -> followService.getFollowings(anotherMember.getId(), privateMember.getId()))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("비공개 계정은 자신을 팔로우하지 않은 사용자에게 팔로잉 목록을 보여주지 않는다")
    void cantSeeFollowingsThatDoesNotFollowOnPrivateAccount() throws Exception {
        // given
        Member privateMember = saveMemberWithAccountPolicy(false);
        em.persist(privateMember);

        Member anotherMember = saveMember();
        saveFollow(anotherMember, privateMember, FollowStatus.PENDING);

        // when & then
        assertThatThrownBy(() -> followService.getFollowings(anotherMember.getId(), privateMember.getId()))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(AUTHORIZED_FAIL.getMessage());
    }


    @Test
    @DisplayName("특정 사용자가 팔로우한 사람들을 전부 보여준다.")
    void getFollowersSuccess() throws Exception {
        // given
        Member target = saveMember();
        int approvedCnt = 10;
        List<Member> members = saveMembers(approvedCnt);
        List<Follow> targetFollows = saveTargetFollowers(FollowStatus.APPROVED, target, members, 0, approvedCnt);

        List<FollowInfo> targetFollowInfos = targetFollows.stream()
            .map(follow -> FollowInfo.of(follow.getFrom(), follow.getId()))
            .toList();
        em.flush();
        em.clear();

        // when
        List<FollowInfo> infos = followService.getFollowers(target.getId(), target.getId());

        // then
        assertThat(infos).hasSize(approvedCnt)
            .containsAnyElementsOf(targetFollowInfos);
        assertThat(infos.get(0).memberId()).isNotEqualTo(target.getId());
    }

    @Test
    @DisplayName("특정 사용자의 팔로워 목록을 가져올 때, APPROVED 상태인 것만 가져온다.")
    void getFollowersThatStatusIsSuccess() throws Exception {
        // given
        Member target = saveMember();
        int pendingCnt = 1;
        int rejectCnt = 1;
        int canceledCnt = 1;
        int approvedCnt = 10;

        List<Member> members = saveMembers(pendingCnt + rejectCnt + canceledCnt + approvedCnt);

        int idx = 0;
        saveTargetFollowers(FollowStatus.PENDING, target, members, idx, pendingCnt);
        idx += pendingCnt;
        saveTargetFollowers(FollowStatus.REJECTED, target, members, idx, rejectCnt);
        idx += rejectCnt;
        saveTargetFollowers(FollowStatus.CANCELED, target, members, idx, canceledCnt);
        idx += canceledCnt;
        saveTargetFollowers(FollowStatus.APPROVED, target, members, idx, approvedCnt);

        // when
        List<FollowInfo> infos = followService.getFollowers(target.getId(), target.getId());

        // then
        assertThat(infos).hasSize(approvedCnt);
    }

    @Test
    @DisplayName("공개 계정은 누구나 팔로워 목록을 볼 수 있다.")
    void canSeeFollowersEveryoneOnPublicAccount() throws Exception {
        // given
        Member publicAccountMember = saveMemberWithAccountPolicy(true);
        em.persist(publicAccountMember);

        Member anotherMember = saveMember();

        // when & then
        assertThatCode(() -> followService.getFollowers(anotherMember.getId(), publicAccountMember.getId()))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("비공개 계정은 자신을 팔로우한 사람들만 팔로워 목록을 볼 수 있다")
    void canSeeFollowersThatSpecifyMemberOnPrivateAccount() throws Exception {
        // given
        Member privateMember = saveMemberWithAccountPolicy(false);
        em.persist(privateMember);

        Member anotherMember = saveMember();
        saveFollow(anotherMember, privateMember, FollowStatus.APPROVED);

        // when & then
        assertThatCode(() -> followService.getFollowers(anotherMember.getId(), privateMember.getId()))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("비공개 계정은 자신을 팔로우하지 않은 사용자에게 팔로워 목록을 보여주지 않는다")
    void cantSeeFollowersThatDoesNotFollowOnPrivateAccount() throws Exception {
        // given
        Member privateMember = saveMemberWithAccountPolicy(false);
        em.persist(privateMember);

        Member anotherMember = saveMember();
        saveFollow(privateMember, anotherMember, FollowStatus.PENDING);

        // when & then
        assertThatThrownBy(() -> followService.getFollowers(anotherMember.getId(), privateMember.getId()))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(AUTHORIZED_FAIL.getMessage());
    }

    private List<Follow> saveTargetFollowings(FollowStatus followStatus, Member target, List<Member> members, int start,
        int repeatCnt) {
        List<Follow> follows = new ArrayList<>();
        for (int i = start; i < start + repeatCnt; i++) {
            follows.add(saveFollow(target, members.get(i), followStatus));
        }
        return follows;
    }

    private List<Follow> saveTargetFollowers(FollowStatus followStatus, Member target, List<Member> members, int start,
        int repeatCnt) {
        List<Follow> follows = new ArrayList<>();
        for (int i = start; i < start + repeatCnt; i++) {
            follows.add(saveFollow(members.get(i), target, followStatus));
        }
        return follows;
    }

    private List<Member> saveMembers(int createdCnt) {
        List<Member> members = new ArrayList<>();
        for (int i = 0; i < createdCnt; i++) {
            Member member = saveMember();
            members.add(member);
        }
        return members;
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

    private Member saveMemberWithAccountPolicy(boolean isPublic) {
        Member member = Member.builder()
            .email("이메일")
            .profileUrl("프로필")
            .nickname(Nickname.create("nick" + (globalIdx++)))
            .followStrategy(FollowStrategy.EAGER)
            .publicAccount(isPublic)
            .build();
        em.persist(member);
        return member;
    }

    private Member saveMemberWithFollowStrategy(FollowStrategy followStrategy) {
        Member member = Member.builder()
            .email("이메일")
            .profileUrl("프로필")
            .nickname(Nickname.create("nick" + (globalIdx++)))
            .followStrategy(followStrategy)
            .publicAccount(true)
            .build();
        em.persist(member);
        return member;
    }
}
