package com.example.temp.auth.oauth;

public interface OAuthProvider {

    boolean support(OAuthProviderType providerType);

    OAuthResponse fetch(String authCode);
}
