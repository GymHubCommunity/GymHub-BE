package com.example.temp.oauth.impl.google;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.temp.common.entity.Email;
import com.example.temp.oauth.OAuthProviderType;
import com.example.temp.oauth.OAuthResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

@ExtendWith(MockitoExtension.class)
class GoogleOAuthProviderTest {

    GoogleOAuthProvider provider;

    @Mock
    GoogleOAuthClient client;

    @Mock
    GoogleOAuthProperties properties;

    GoogleToken googleToken;

    GoogleUserInfo googleUserInfo;

    @BeforeEach
    void setUp() {
        provider = new GoogleOAuthProvider(client, properties);
        googleToken = new GoogleToken("access", "Bearer");
        googleUserInfo = new GoogleUserInfo();
    }

    @Test
    @DisplayName("GoogleOAuthProvider 객체는 Google 타입을 지원한다")
    void supportTest() throws Exception {
        assertThat(provider.support(OAuthProviderType.GOOGLE)).isTrue();
    }

    @Test
    @DisplayName("GoogleOAuthProvider 객체는 Google 이외의 타입은 지원하지 않는다")
    void supportTestFailInvalidType1() throws Exception {
        assertThat(provider.support(OAuthProviderType.KAKAO)).isFalse();
    }

    @Test
    @DisplayName("GoogleOAuthProvider 객체는 null 타입에 대해 지원하지 않는다")
    void supportTestFailInvalidType2() throws Exception {
        assertThat(provider.support(null)).isFalse();
    }

    @Test
    @DisplayName("Google의 authCode를 사용해서 OAuthResponse를 만든다")
    void fetch() throws Exception {
        // given
        when(client.fetchToken(any(MultiValueMap.class)))
            .thenReturn(googleToken);
        when(client.fetchUserInfo(anyString()))
            .thenReturn(googleUserInfo);

        // when
        OAuthResponse result = provider.fetch("authCode");

        // then
        assertThat(result.type()).isEqualTo(OAuthProviderType.GOOGLE);
        assertThat(result.email()).isEqualTo(Email.create(googleUserInfo.getEmail()));
        assertThat(result.name()).isEqualTo(googleUserInfo.getName());
        assertThat(result.idUsingResourceServer()).isEqualTo(googleUserInfo.getIdUsingResourceServer());
        assertThat(result.profileUrl()).isEqualTo(googleUserInfo.getProfileUrl());
    }

    @Test
    @DisplayName("Google의 authCode가 적절하지 않으면 예외를 반환한다")
    void fetchFailInvalidAuthCode() throws Exception {
        // given
        when(client.fetchToken(any(MultiValueMap.class)))
            .thenThrow(WebClientResponseException.create(400, "에러메세지", null, null, null));

        // when & then
        assertThatThrownBy(() -> provider.fetch("authCode"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("적절하지 않은 Auth Code 입니다.");
    }

    @Test
    @DisplayName("fetchToken 요청을 보낼 때, Google 서버에 문제가 발생하면 예외를 반환한다")
    void fetchFailInvalidGoogleServer1() throws Exception {
        // given
        when(client.fetchToken(any(MultiValueMap.class)))
            .thenReturn(googleToken);
        when(client.fetchUserInfo(anyString()))
            .thenThrow(WebClientResponseException.create(500, "에러메세지", null, null, null));

        // when & then
        assertThatThrownBy(() -> provider.fetch("authCode"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Google 서버에서 문제가 발생했습니다.");
    }

    @Test
    @DisplayName("fetchUserInfo 요청을 보낼 때, Google 서버에 문제가 발생하면 예외를 반환한다")
    void fetchFailInvalidGoogleServer2() throws Exception {
        // given
        when(client.fetchToken(any(MultiValueMap.class)))
            .thenThrow(WebClientResponseException.create(500, "에러메세지", null, null, null));

        // when & then
        assertThatThrownBy(() -> provider.fetch("authCode"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Google 서버에서 문제가 발생했습니다.");
    }

    @Test
    @DisplayName("인증 URL을 받아온다.")
    void getAuthorizedUrl() throws Exception {
        // given
        String fromUri = "fromUri";
        String clientId = "clientId";
        String redirectUri = "redirectUri";
        String[] scope = new String[]{"first", "second"};
        when(properties.fromUri()).thenReturn(fromUri);
        when(properties.clientId()).thenReturn(clientId);
        when(properties.redirectUri()).thenReturn(redirectUri);
        when(properties.scope()).thenReturn(scope);
        String expectedUrl = createUrl(fromUri, clientId, redirectUri, scope);

        // when
        String authorizedUrl = provider.getAuthorizedUrl();

        // then
        assertThat(authorizedUrl).isEqualTo(expectedUrl);
        verify(properties, never()).clientSecret();
    }

    private static String createUrl(String fromUri, String clientId, String redirectUri, String[] scope) {
        String googleScopeDelimiter = " ";
        return UriComponentsBuilder
            .fromUriString(fromUri)
            .queryParam("client_id", clientId)
            .queryParam("redirect_uri", redirectUri)
            .queryParam("response_type", "code")
            .queryParam("scope", String.join(googleScopeDelimiter, scope))
            .toUriString();
    }

}