package com.example.temp.oauth;

public interface OAuthProvider {

    boolean support(OAuthProviderType providerType);

    OAuthResponse fetch(String authCode);

    String getAuthorizedUrl();
}
