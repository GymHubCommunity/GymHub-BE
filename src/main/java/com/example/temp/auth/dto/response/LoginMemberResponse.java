package com.example.temp.auth.dto.response;

import com.example.temp.member.domain.Member;

public record LoginMemberResponse(
    Long id,
    String email,
    String profileUrl,
    String nickname
) {

    public static LoginMemberResponse of(Member member) {
        return new LoginMemberResponse(member.getId(), member.getEmail(), member.getProfileUrl(), member.getNickname());
    }
}
