package com.example.temp.auth.oauth;

import java.util.Arrays;
import java.util.Objects;

public enum OAuthProviderType {
    GOOGLE("google"),
    KAKAO("kakao");

    private final String text;

    OAuthProviderType(String text) {
        this.text = text;
    }

    public static OAuthProviderType find(String providerName) {
        return Arrays.stream(OAuthProviderType.values())
            .filter(each -> Objects.equals(each.text, providerName))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 OAuth 타입입니다."));
    }
}
