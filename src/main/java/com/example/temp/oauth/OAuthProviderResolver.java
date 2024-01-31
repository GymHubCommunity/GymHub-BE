package com.example.temp.oauth;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OAuthProviderResolver {

    private final Set<OAuthProvider> providers;

    public OAuthResponse fetch(OAuthProviderType providerType, String authCode) {
        OAuthProvider oAuthProvider = providers.stream()
            .filter(provider -> provider.support(providerType))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 OAuth 타입입니다."));
        return oAuthProvider.fetch(authCode);
    }
}

