package com.example.temp.auth.dto.response;

import com.example.temp.member.domain.Member;
import com.fasterxml.jackson.annotation.JsonIgnore;

public record MemberInfo(
    Long id,
    String email,
    String profileUrl,
    String nickname,

    @JsonIgnore
    boolean registered
) {

    public static MemberInfo of(Member member) {
        return new MemberInfo(member.getId(), member.getEmailValue(), member.getProfileUrl(),
            member.getNicknameValue(), member.isRegistered());
    }
}
