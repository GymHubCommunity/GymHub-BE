package com.example.temp.auth.oauth.impl.google;

import com.example.temp.auth.oauth.OAuthProvider;
import com.example.temp.auth.oauth.OAuthProviderType;
import com.example.temp.auth.oauth.OAuthResponse;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Component
@RequiredArgsConstructor
public class GoogleOAuthProvider implements OAuthProvider {

    private final GoogleOAuthClient googleOAuthClient;
    private final GoogleOAuthProperties properties;

    @Override
    public boolean support(OAuthProviderType providerType) {
        return Objects.equals(OAuthProviderType.GOOGLE, providerType);
    }

    @Override
    public OAuthResponse fetch(String authCode) {
        GoogleToken googleToken = googleOAuthClient.fetchToken(getFetchTokenParams(authCode));
        GoogleUserInfo googleUserInfo = googleOAuthClient.fetchUserInfo(googleToken.getAuthorizationValue());
        return null;
    }

    private MultiValueMap<String, String> getFetchTokenParams(String authCode) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", properties.clientId());
        params.add("client_secret", properties.clientSecret());
        params.add("code", authCode);
        params.add("redirect_uri", properties.redirectUri());
        params.add("grant_type", "authorization_code");
        return params;
    }
}
