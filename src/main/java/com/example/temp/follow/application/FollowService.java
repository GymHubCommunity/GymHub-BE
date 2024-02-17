package com.example.temp.follow.application;

import static com.example.temp.common.exception.ErrorCode.AUTHENTICATED_FAIL;
import static com.example.temp.common.exception.ErrorCode.AUTHORIZED_FAIL;
import static com.example.temp.common.exception.ErrorCode.FOLLOW_NOT_FOUND;
import static com.example.temp.common.exception.ErrorCode.FOLLOW_SELF_FAIL;
import static com.example.temp.common.exception.ErrorCode.MEMBER_NOT_FOUND;

import com.example.temp.common.dto.UserContext;
import com.example.temp.common.exception.ApiException;
import com.example.temp.follow.domain.Follow;
import com.example.temp.follow.domain.FollowRepository;
import com.example.temp.follow.domain.FollowStatus;
import com.example.temp.follow.dto.response.FollowInfoResult;
import com.example.temp.follow.dto.response.FollowResponse;
import com.example.temp.member.domain.Member;
import com.example.temp.member.domain.MemberRepository;
import com.example.temp.member.event.MemberDeletedEvent;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final MemberRepository memberRepository;

    /**
     * targetId를 팔로우하고 있는 사람들의 목록과 마지막 데이터가 포함되었는지 여부를 보여줍니다. Target이 비공개 계정일 때는 자기 자신과 Target을 팔로우하고 있는 사람들만이 실행할 수
     * 있습니다.
     *
     * @param userContext 로그인한 사용자의 정보
     * @param targetId    팔로잉 목록을 보려고 하는 대상의 ID
     * @param lastId      이전에 조회했던 마지막 팔로우 엔티티의 아이디
     * @param pageable
     * @return FollowInfo 객체 리스트를 반환합니다. 각 FollowInfo 객체는 팔로잉 대상의 정보와 팔로우 ID를 포함하고 있습니다.
     * @throws ApiException AUTHORIZED_FAIL: 팔로잉 목록을 볼 권한이 없을 때 발생합니다.
     */
    public FollowInfoResult getFollowings(UserContext userContext, Long targetId, long lastId, Pageable pageable) {
        Member target = memberRepository.findById(targetId)
            .orElseThrow(() -> new ApiException(MEMBER_NOT_FOUND));
        if (!target.isPublicAccount()) {
            validateViewAuthorization(targetId, userContext.id());
        }
        Slice<Follow> follows = followRepository.findAllByFromIdAndStatus(targetId, FollowStatus.APPROVED,
            lastId, pageable);
        return FollowInfoResult.createFollowingsResult(follows);
    }

    /**
     * Target이 팔로우하고 있는 사람들의 목록과 마지막 데이터가 포함되었는지 여부를 보여줍니다. Target이 비공개 계정일 때는 자기 자신과 Target을 팔로우하고 있는 사람들만이 실행할 수
     * 있습니다.
     *
     * @param userContext 로그인한 사용자의 정보
     * @param targetId    팔로워 목록을 보려고 하는 대상의 ID
     * @param lastId      이전에 조회했던 마지막 팔로우 엔티티의 아이디
     * @param pageable
     * @return FollowInfo 객체 리스트를 반환합니다. 각 FollowInfo 객체는 팔로워 대상의 정보와 팔로우 ID를 포함하고 있습니다.
     * @throws ApiException AUTHORIZED_FAIL: 팔로워 목록을 볼 권한이 없을 때 발생합니다.
     */
    public FollowInfoResult getFollowers(UserContext userContext, long targetId, long lastId, Pageable pageable) {
        Member target = memberRepository.findById(targetId)
            .orElseThrow(() -> new ApiException(MEMBER_NOT_FOUND));
        if (!target.isPublicAccount()) {
            validateViewAuthorization(targetId, userContext.id());
        }
        Slice<Follow> follows = followRepository.findAllByToIdAndStatus(targetId,
            FollowStatus.APPROVED, lastId, pageable);
        return FollowInfoResult.createFollowersResult(follows);
    }

    private void validateViewAuthorization(long targetId, long executorId) {
        if (isMyAccount(targetId, executorId)) {
            return;
        }
        if (!isTargetsFollower(targetId, executorId)) {
            throw new ApiException(AUTHORIZED_FAIL);
        }
    }

    private boolean isMyAccount(long targetId, long executorId) {
        return targetId == executorId;
    }

    private boolean isTargetsFollower(long targetId, long executorId) {
        return followRepository.checkExecutorFollowsTarget(executorId, targetId);
    }

    /**
     * Target을 팔로우합니다. Target의 팔로우 전략에 따라 팔로우가 성공하거나, 또는 팔로우가 대기 상태가 될 수 있습니다.
     *
     * @param userContext 로그인한 사용자의 정보
     * @param targetId    팔로우를 신청하는 대상의 ID
     * @return 팔로우 요청의 결과를 보여주는 FollowResponse 객체를 반환합니다. FollowResponse 객체는 팔로우의 상태(성공, 대기 등)를 포함하고 있습니다.
     * @throws ApiException FOLLOW_SELF_FAIL: 자기 자신을 팔로우하려고 할 때 발생합니다.
     * @throws ApiException MEMBER_NOT_FOUND: 팔로우를 신청하는 대상을 찾을 수 없을 때 발생합니다.
     * @throws ApiException FOLLOW_ALREADY_RELATED: 로그인한 사용자가 이미 Target을 팔로우하고 있을 때 발생합니다.
     */
    @Transactional
    public FollowResponse follow(UserContext userContext, Long targetId) {
        if (isMyAccount(userContext.id(), targetId)) {
            throw new ApiException(FOLLOW_SELF_FAIL);
        }
        Member fromMember = memberRepository.findById(userContext.id())
            .orElseThrow(() -> new ApiException(AUTHENTICATED_FAIL));
        Member target = memberRepository.findById(targetId)
            .orElseThrow(() -> new ApiException(MEMBER_NOT_FOUND));

        Follow savedFollow = followRepository.findByFromIdAndToId(userContext.id(), targetId)
            .map(follow -> follow.reactive(target.getStatusBasedOnStrategy()))
            .orElseGet(() -> saveFollow(fromMember, target));
        return FollowResponse.from(savedFollow);
    }

    private Follow saveFollow(Member fromMember, Member target) {
        Follow follow = Follow.builder()
            .from(fromMember)
            .to(target)
            .status(target.getStatusBasedOnStrategy())
            .build();
        return followRepository.save(follow);
    }

    /**
     * Executor는 Target과의 관계를 끊습니다. Follow 레코드의 상태는 CANCELED 상태가 됩니다.
     *
     * @param userContext 로그인한 사용자의 정보
     * @param targetId    언팔로우 하려는 대상의 ID
     * @throws ApiException MEMBER_NOT_FOUND: 언팔로우를 하려는 대상을 찾을 수 없을 때 발생합니다.
     * @throws ApiException FOLLOW_INACTIVE: 팔로우가 이미 비활성화되어있을 때 발생합니다.
     */
    @Transactional
    public void unfollow(UserContext userContext, Long targetId) {
        if (!memberRepository.existsById(targetId)) {
            throw new ApiException(MEMBER_NOT_FOUND);
        }
        Follow follow = followRepository.findByFromIdAndToId(userContext.id(), targetId)
            .orElseThrow(() -> new ApiException(FOLLOW_NOT_FOUND));
        follow.unfollow();
    }

    /**
     * 대기 상태의 Follow를 수락합니다.
     *
     * @param userContext 로그인한 사용자의 정보
     * @param followId    팔로우 레코드의 ID
     * @throws ApiException FOLLOW_NOT_FOUND: 팔로우 관계를 찾을 수 없을 때 발생합니다.
     * @throws ApiException FOLLOW_NOT_PENDING: 변경하려 하는 팔로우가 PENDING 상태가 아닐 때 발생합니다.
     * @throws ApiException FOLLOW_INACTIVE: 팔로우가 이미 비활성화되어있을 때 발생합니다.
     */
    @Transactional
    public void acceptFollowRequest(UserContext userContext, Long followId) {
        Follow follow = followRepository.findById(followId)
            .orElseThrow(() -> new ApiException(FOLLOW_NOT_FOUND));
        Member toMember = follow.getTo();
        if (!Objects.equals(toMember.getId(), userContext.id())) {
            throw new ApiException(AUTHORIZED_FAIL);
        }
        follow.accept();
    }

    /**
     * Executor는 자신을 팔로우하고 있는 관계 중 하나를 끊습니다. Follow 레코드의 상태는 REJECTED 상태가 됩니다.
     *
     * @param userContext 로그인한 사용자의 정보
     * @param followId    끊으려는 Follow 레코드의 ID
     * @throws ApiException FOLLOW_NOT_FOUND: 팔로우 관계를 찾을 수 없을 때 발생합니다.
     * @throws ApiException AUTHORIZED_FAIL: 팔로우가 로그인한 사용자를 가리키고 있지 않을 때 발생합니다.
     * @throws ApiException FOLLOW_INACTIVE: 팔로우가 이미 비활성화되어있을 때 발생합니다.
     */
    @Transactional
    public void rejectFollowRequest(UserContext userContext, long followId) {
        Follow follow = followRepository.findById(followId)
            .orElseThrow(() -> new ApiException(FOLLOW_NOT_FOUND));
        Member target = follow.getTo();
        if (!isMyAccount(target.getId(), userContext.id())) {
            throw new ApiException(AUTHORIZED_FAIL);
        }
        follow.reject();
    }

    /**
     * 회원이 삭제되었을 때, 해당 회원이 팔로우 중이거나, 해당 회원을 팔로우하고 있는 모든 팔로우를 삭제합니다.
     */
    @EventListener
    public void handleMemberDeletedEvent(MemberDeletedEvent event) {
        List<Follow> follows = followRepository.findAllRelatedByMemberId(event.getMemberId());
        followRepository.deleteAllInBatch(follows);
    }

}
