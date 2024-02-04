package com.example.temp.follow.application;

import static com.example.temp.exception.ErrorCode.AUTHENTICATED_FAIL;
import static com.example.temp.exception.ErrorCode.AUTHORIZED_FAIL;
import static com.example.temp.exception.ErrorCode.FOLLOW_NOT_FOUND;
import static com.example.temp.exception.ErrorCode.FOLLOW_SELF_FAIL;
import static com.example.temp.exception.ErrorCode.MEMBER_NOT_FOUND;

import com.example.temp.exception.ApiException;
import com.example.temp.follow.domain.Follow;
import com.example.temp.follow.domain.FollowRepository;
import com.example.temp.follow.domain.FollowStatus;
import com.example.temp.follow.dto.response.FollowInfo;
import com.example.temp.follow.response.FollowResponse;
import com.example.temp.member.domain.Member;
import com.example.temp.member.domain.MemberRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final MemberRepository memberRepository;


    public List<FollowInfo> getFollowings(long executorId, long targetId) {
        Member target = memberRepository.findById(targetId)
            .orElseThrow(() -> new ApiException(MEMBER_NOT_FOUND));
        if (!target.isPublicAccount()) {
            validateViewAuthorization(targetId, executorId);
        }
        return followRepository.findAllByFromIdAndStatus(targetId, FollowStatus.SUCCESS).stream()
            .map(follow -> FollowInfo.of(follow.getTo(), follow.getId()))
            .toList();
    }

    public List<FollowInfo> getFollowers(long executorId, long targetId) {
        Member target = memberRepository.findById(targetId)
            .orElseThrow(() -> new ApiException(MEMBER_NOT_FOUND));
        if (!target.isPublicAccount()) {
            validateViewAuthorization(targetId, executorId);
        }
        return followRepository.findAllByToIdAndStatus(targetId, FollowStatus.SUCCESS).stream()
            .map(follow -> FollowInfo.of(follow.getFrom(), follow.getId()))
            .toList();
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

    @Transactional
    public FollowResponse follow(long executorId, Long targetId) {
        if (isMyAccount(executorId, targetId)) {
            throw new ApiException(FOLLOW_SELF_FAIL);
        }
        Member fromMember = memberRepository.findById(executorId)
            .orElseThrow(() -> new ApiException(AUTHENTICATED_FAIL));
        Member target = memberRepository.findById(targetId)
            .orElseThrow(() -> new ApiException(MEMBER_NOT_FOUND));

        Follow savedFollow = followRepository.findByFromIdAndToId(executorId, targetId)
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

    @Transactional
    public void unfollow(long executorId, Long targetId) {
        if (!memberRepository.existsById(targetId)) {
            throw new ApiException(MEMBER_NOT_FOUND);
        }
        Follow follow = followRepository.findByFromIdAndToId(executorId, targetId)
            .orElseThrow(() -> new ApiException(FOLLOW_NOT_FOUND));
        follow.unfollow();
    }

    @Transactional
    public void acceptFollowRequest(long targetId, Long followId) {
        Follow follow = followRepository.findById(followId)
            .orElseThrow(() -> new ApiException(FOLLOW_NOT_FOUND));
        Member target = follow.getTo();
        if (target.getId() != targetId) {
            throw new ApiException(AUTHORIZED_FAIL);
        }
        follow.accept();
    }

    @Transactional
    public void rejectFollowRequest(long executorId, long followId) {
        Follow follow = followRepository.findById(followId)
            .orElseThrow(() -> new ApiException(FOLLOW_NOT_FOUND));
        Member target = follow.getTo();
        if (!isMyAccount(target.getId(), executorId)) {
            throw new ApiException(AUTHORIZED_FAIL);
        }
        follow.reject();
    }

}

