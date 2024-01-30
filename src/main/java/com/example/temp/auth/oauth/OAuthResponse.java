package com.example.temp.auth.oauth;

public record OAuthResponse(
    String email,
    String nickname,
    Long idUsingResourceServer,
    String profileUrl
) {
}
