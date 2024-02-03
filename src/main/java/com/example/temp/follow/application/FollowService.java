package com.example.temp.follow.application;

import com.example.temp.follow.domain.Follow;
import com.example.temp.follow.domain.FollowRepository;
import com.example.temp.follow.response.FollowResponse;
import com.example.temp.member.domain.Member;
import com.example.temp.member.domain.MemberRepository;
import java.util.Optional;
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

        Optional<Follow> followOpt = followRepository.findByFromIdAndToId(fromId, toId);
        if (followOpt.isEmpty()) {
            // 새롭게 가입
            Follow follow = Follow.builder()
                .from(fromMember)
                .to(target)
                .status(target.getStatusBasedOnStrategy())
                .build();
            Follow savedFollow = followRepository.save(follow);
            return FollowResponse.from(savedFollow);
        } else {
            Follow follow = followOpt.get();
            if (follow.isValid()) {
                throw new IllegalArgumentException("이미 둘 사이에 관계가 존재합니다.");
            }
            follow.setStatus(target.getStatusBasedOnStrategy());
            return FollowResponse.from(follow);
        }
    }
}
