package com.example.temp.auth.oauth.impl.google;

import com.example.temp.auth.oauth.OAuthProvider;
import com.example.temp.auth.oauth.OAuthProviderType;
import com.example.temp.auth.oauth.OAuthResponse;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GoogleOAuthProvider implements OAuthProvider {

    private final GoogleOAuthProperties properties;

    @Override
    public boolean support(OAuthProviderType providerType) {
        return Objects.equals(OAuthProviderType.GOOGLE, providerType);
    }

    @Override
    public OAuthResponse fetch(String authCode) {
        return null;
    }
}
