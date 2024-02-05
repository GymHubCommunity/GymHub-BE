package com.example.temp.oauth.impl.kakao;

import com.example.temp.oauth.OAuthProvider;
import com.example.temp.oauth.OAuthProviderType;
import com.example.temp.oauth.OAuthResponse;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
@RequiredArgsConstructor
public class KakaoOAuthProvider implements OAuthProvider {

    private final KakaoOAuthClient kakaoOAuthClient;
    private final KakaoOAuthProperties properties;

    @Override
    public boolean support(OAuthProviderType providerType) {
        return Objects.equals(OAuthProviderType.KAKAO, providerType);
    }

    @Override
    public OAuthResponse fetch(String authCode) {
        KakaoToken kakaoToken = fetchToken(authCode);
        KakaoUserInfo kakaoUserInfo = fetchUserInfo(kakaoToken);
        return OAuthResponse.of(OAuthProviderType.KAKAO, kakaoUserInfo);
    }

    private KakaoUserInfo fetchUserInfo(KakaoToken kakaoToken) {
        try {
            return kakaoOAuthClient.fetchUserInfo(kakaoToken.getValueUsingAuthorizationHeader());
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().is5xxServerError()) {
                throw new IllegalArgumentException("Kakao 서버에서 문제가 발생했습니다.");
            }
            throw e;
        }
    }

    private KakaoToken fetchToken(String authCode) {
        try {
            return kakaoOAuthClient.fetchToken(getFetchTokenParams(authCode));
        } catch (WebClientResponseException.BadRequest e) {
            throw new IllegalArgumentException("적절하지 않은 Auth Code 입니다.");
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().is5xxServerError()) {
                throw new IllegalArgumentException("Kakao 서버에서 문제가 발생했습니다.");
            }
            throw e;
        }
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
