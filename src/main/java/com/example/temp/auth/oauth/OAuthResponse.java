package com.example.temp.auth.oauth;

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
}
