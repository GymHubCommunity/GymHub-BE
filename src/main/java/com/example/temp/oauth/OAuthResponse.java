package com.example.temp.oauth;

import com.example.temp.member.domain.Member;
import com.example.temp.member.infrastructure.nickname.Nickname;

public record OAuthResponse(
    OAuthProviderType type,
    String email,
    String name,
    String idUsingResourceServer,
    String profileUrl
) {

    public static OAuthResponse of(OAuthProviderType type, OAuthUserInfo oAuthUserInfo) {
        return new OAuthResponse(type, oAuthUserInfo.getEmail(), oAuthUserInfo.getName(),
            oAuthUserInfo.getIdUsingResourceServer(), oAuthUserInfo.getProfileUrl());
    }

    public Member toInitStatusMemberWith(Nickname nickname) {
        return Member.createInitStatus(this.email(), this.profileUrl(), nickname);
    }
}
