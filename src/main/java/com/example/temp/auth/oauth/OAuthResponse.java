package com.example.temp.auth.oauth;

public record OAuthResponse(
    OAuthProviderType type,
    String email,
    String nickname,
    Long idUsingResourceServer,
    String profileUrl
) {
}
