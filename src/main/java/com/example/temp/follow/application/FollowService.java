package com.example.temp.follow.application;

import com.example.temp.follow.domain.Follow;
import com.example.temp.follow.domain.FollowRepository;
import com.example.temp.follow.response.FollowResponse;
import com.example.temp.member.domain.Member;
import com.example.temp.member.domain.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public FollowResponse follow(long fromId, Long toId) {
        Member fromMember = memberRepository.findById(fromId)
            .orElseThrow(() -> new IllegalArgumentException("찾을 수 없는 사용자"));
        Member target = memberRepository.findById(toId)
            .orElseThrow(() -> new IllegalArgumentException("찾을 수 없는 사용자"));

        Follow savedFollow = followRepository.findByFromIdAndToId(fromId, toId)
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
}

