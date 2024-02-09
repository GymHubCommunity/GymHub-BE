package com.example.temp.oauth;

import com.example.temp.common.entity.Email;
import com.example.temp.member.domain.Member;
import com.example.temp.member.domain.nickname.Nickname;

public record OAuthResponse(
    OAuthProviderType type,
    Email email,
    String name,
    String idUsingResourceServer,
    String profileUrl
) {

    public static OAuthResponse of(OAuthProviderType type, OAuthUserInfo oAuthUserInfo) {
        return new OAuthResponse(type, Email.create(oAuthUserInfo.getEmail()), oAuthUserInfo.getName(),
            oAuthUserInfo.getIdUsingResourceServer(), oAuthUserInfo.getProfileUrl());
    }

    public Member toInitStatusMemberWith(Nickname nickname) {
        return Member.createInitStatus(this.email(), this.profileUrl(), nickname);
    }
}
