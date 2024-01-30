package com.example.temp.auth.oauth;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OAuthProviderResolver {

    private final Set<OAuthProvider> providers;

    public OAuthResponse fetch(OAuthProviderType providerType, String authCode) {
        return null;
    }
}
