package com.example.temp.follow.application;

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
        return followRepository.findAllByFromIdAndStatus(targetId, FollowStatus.SUCCESS).stream()
            .map(follow -> FollowInfo.of(follow.getFrom(), follow.getId()))
            .toList();
    }

    public List<FollowInfo> getFollowers(long executorId, long targetId) {

        return null;
    }

    @Transactional
    public FollowResponse follow(long fromId, Long toId) {
        if (fromId == toId) {
            throw new IllegalArgumentException("자기 자신을 팔로우할 수 없습니다.");
        }
        Member fromMember = memberRepository.findById(fromId)
            .orElseThrow(() -> new IllegalArgumentException("찾을 수 없는 사용자"));
        Member target = memberRepository.findById(toId)
            .orElseThrow(() -> new IllegalArgumentException("찾을 수 없는 사용자"));

        Follow savedFollow = followRepository.findByFromIdAndToId(fromId, toId)
            .map(follow -> follow.reactive(target.getStatusBasedOnStrategy()))
            .orElseGet(() -> saveFollow(fromMember, target));
        return FollowResponse.from(savedFollow);
    }

    @Transactional
    public void unfollow(long fromId, Long toId) {
        if (!memberRepository.existsById(toId)) {
            throw new IllegalArgumentException("찾을 수 없는 사용자");
        }
        Follow follow = followRepository.findByFromIdAndToId(fromId, toId)
            .orElseThrow(() -> new IllegalArgumentException("찾을 수 없는 관계"));
        follow.unfollow();
    }

    @Transactional
    public void acceptFollowRequest(long targetId, Long followId) {
        Follow follow = followRepository.findById(followId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 Follow"));
        Member target = follow.getTo();
        if (target.getId() != targetId) {
            throw new IllegalArgumentException("권한없음");
        }
        follow.accept();
    }

    @Transactional
    public void rejectFollowRequest(long executorId, long followId) {
        Follow follow = followRepository.findById(followId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 Follow"));
        Member target = follow.getTo();
        if (target.getId() != executorId) {
            throw new IllegalArgumentException("권한없음");
        }
        follow.reject();
    }

    private Follow saveFollow(Member fromMember, Member target) {
        Follow follow = Follow.builder()
            .from(fromMember)
            .to(target)
            .status(target.getStatusBasedOnStrategy())
            .build();
        return followRepository.save(follow);
    }

}

