package com.example.temp.follow.dto.response;

import com.example.temp.member.domain.Member;

public record FollowInfo(
    Long id,
    Long memberId,
    String nickname,
    String profileUrl
) {

    public static FollowInfo of(Member member, Long id) {
        return new FollowInfo(id, member.getId(), member.getNicknameValue(), member.getProfileUrl());
    }
}
