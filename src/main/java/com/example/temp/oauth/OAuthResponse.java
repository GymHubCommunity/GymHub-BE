package com.example.temp.oauth;

import com.example.temp.member.domain.FollowStrategy;
import com.example.temp.member.domain.Member;

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

    public Member toMemberWithNickname(String nickname) {
        return Member.builder()
            .email(this.email())
            .profileUrl(this.profileUrl())
            .nickname(nickname)
            .followStrategy(FollowStrategy.EAGER)
            .publicAccount(true)
            .build();
    }
}
