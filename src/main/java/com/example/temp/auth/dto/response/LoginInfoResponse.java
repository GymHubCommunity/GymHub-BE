package com.example.temp.auth.dto.response;

import com.example.temp.member.domain.Member;

public record LoginInfoResponse(
    Long id,
    String email,
    String profileUrl,
    String nickname
) {

    public static LoginInfoResponse of(Member member) {
        return new LoginInfoResponse(member.getId(), member.getEmail(), member.getProfileUrl(), member.getNickname());
    }
}
