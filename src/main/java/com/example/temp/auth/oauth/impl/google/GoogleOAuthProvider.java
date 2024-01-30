package com.example.temp.auth.oauth.impl.google;

import com.example.temp.auth.oauth.OAuthProvider;
import com.example.temp.auth.oauth.OAuthProviderType;
import com.example.temp.auth.oauth.OAuthResponse;
import java.util.Objects;

public class GoogleOAuthProvider implements OAuthProvider {

    @Override
    public boolean support(OAuthProviderType providerType) {
        return Objects.equals(OAuthProviderType.GOOGLE, providerType);
    }

    @Override
    public OAuthResponse fetch(String authCode) {
        return null;
    }
}
