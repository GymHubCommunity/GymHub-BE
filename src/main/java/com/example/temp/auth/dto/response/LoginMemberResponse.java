package com.example.temp.auth.dto.response;

import com.example.temp.member.domain.Member;
import com.fasterxml.jackson.annotation.JsonIgnore;

public record LoginMemberResponse(
    Long id,
    String email,
    String profileUrl,
    String nickname,

    @JsonIgnore
    boolean init
) {

    public static LoginMemberResponse of(Member member) {
        return new LoginMemberResponse(member.getId(), member.getEmail(), member.getProfileUrl(),
            member.getNickname(), member.isInit());
    }
}
