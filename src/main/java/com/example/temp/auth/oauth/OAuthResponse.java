package com.example.temp.auth.oauth;

import com.example.temp.auth.oauth.impl.google.GoogleUserInfo;

public record OAuthResponse(
    OAuthProviderType type,
    String email,
    String name,
    String idUsingResourceServer,
    String profileUrl
) {

    public static OAuthResponse of(OAuthProviderType type, GoogleUserInfo googleUserInfo) {
        return new OAuthResponse(type, googleUserInfo.email(), googleUserInfo.name(),
            googleUserInfo.idUsingResourceServer(), googleUserInfo.profileUrl());
    }
}
