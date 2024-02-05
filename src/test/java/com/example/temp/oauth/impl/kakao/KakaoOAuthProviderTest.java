package com.example.temp.oauth.impl.kakao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.temp.oauth.OAuthProviderType;
import com.example.temp.oauth.OAuthResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

@ExtendWith(MockitoExtension.class)
class KakaoOAuthProviderTest {

    KakaoOAuthProvider provider;

    @Mock
    KakaoOAuthClient client;

    @Mock
    KakaoOAuthProperties properties;

    KakaoToken kakaoToken;

    KakaoUserInfo kakaoUserInfo;

    @BeforeEach
    void setUp() {
        provider = new KakaoOAuthProvider(client, properties);
        kakaoToken = new KakaoToken("test_access_token", "Bearer");
        kakaoUserInfo = generateKakaoUserInfo();
    }

    @DisplayName("KakaoOAuthProvider 객체는 Kakao 타입을 지원한다.")
    @Test
    void supportTest() {
        assertThat(provider.support(OAuthProviderType.KAKAO)).isTrue();
    }

    @DisplayName("KakaoOAuthProvider 객체는 Kakao 이외의 타입을 지원하지 않는다.")
    @Test
    void notSupportTest() {
        assertThat(provider.support(OAuthProviderType.GOOGLE)).isFalse();
    }

    @DisplayName("KakaoOAuthProvider 객체는 null값을 허용하지 않는다.")
    @Test
    void notSupportNull() {
        assertThat(provider.support(null)).isFalse();
    }

    @Test
    @DisplayName("Kakao에서 발급한 AuthCode를 사용하여 OauthResponse를 받아 올 수 있다.")
    void fetch() throws Exception {
        // given
        when(client.fetchToken(any(MultiValueMap.class)))
            .thenReturn(kakaoToken);
        when(client.fetchUserInfo(anyString()))
            .thenReturn(kakaoUserInfo);

        // when
        OAuthResponse result = provider.fetch("authCode");

        // then
        assertThat(result.type()).isEqualTo(OAuthProviderType.KAKAO);
        assertThat(result.email()).isEqualTo(kakaoUserInfo.getEmail());
        assertThat(result.name()).isEqualTo(kakaoUserInfo.getName());
        assertThat(result.idUsingResourceServer()).isEqualTo(kakaoUserInfo.getIdUsingResourceServer());
        assertThat(result.profileUrl()).isEqualTo(kakaoUserInfo.getProfileUrl());
    }

    @DisplayName("잘못된 AuthCode가 들어오면 '적절하지 않은 Auth Code 입니다.' 예외 메시지를 던진다.")
    @Test
    void fetchWithInvalidAuthCodeTest() {
        //given
        String invalidAuthCode = "invalidAuthCode";
        String errorMessage = "적절하지 않은 Auth Code 입니다.";
        WebClientResponseException exception = WebClientResponseException.create(
            HttpStatus.BAD_REQUEST.value(), errorMessage, HttpHeaders.EMPTY, null, null);

        when(client.fetchToken(any(MultiValueMap.class)))
            .thenThrow(exception);

        //when & then
        assertThatThrownBy(() -> {
            provider.fetch(invalidAuthCode);
        }).isInstanceOf(IllegalArgumentException.class)
            .hasMessage(errorMessage);
    }

    @DisplayName("fetchToken 메소드 호출 시 Kakao 서버에서 문제 발생 시 'Kakao 서버에서 문제가 발생했습니다.' 메시지와 함께 예외를 발생시킨다.")
    @Test
    void fetchTokenWithServerErrorTest() {
        // given
        String authCode = "authCode";
        String errorMessage = "Kakao 서버에서 문제가 발생했습니다.";
        WebClientResponseException exception = WebClientResponseException.create(
            HttpStatus.INTERNAL_SERVER_ERROR.value(), errorMessage, HttpHeaders.EMPTY, null, null);

        when(client.fetchToken(any(MultiValueMap.class))).thenThrow(exception);

        // when & then
        assertThatThrownBy(() -> {
            provider.fetch(authCode);
        }).isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining(errorMessage);
    }

    @Test
    @DisplayName("fetchUserInfo 메소드 호출 시 Kakao 서버에서 문제 발생 시 'Kakao 서버에서 문제가 발생했습니다.' 메시지와 함께 예외를 발생시킨다.")
    void fetchUserInfoWithServerErrorTest() {
        // given
        String authCode = "authCode";
        String errorMessage = "Kakao 서버에서 문제가 발생했습니다.";

        WebClientResponseException exception = WebClientResponseException.create(
            HttpStatus.INTERNAL_SERVER_ERROR.value(), errorMessage, HttpHeaders.EMPTY, null, null);

        when(client.fetchToken(any(MultiValueMap.class))).thenReturn(kakaoToken);
        when(client.fetchUserInfo(anyString())).thenThrow(exception);

        // when & then
        assertThatThrownBy(() -> {
            provider.fetch(authCode);
        }).isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining(errorMessage);
    }

    private KakaoUserInfo generateKakaoUserInfo() {
        KakaoUserInfo kakaoUserInfo = new KakaoUserInfo();
        ReflectionTestUtils.setField(kakaoUserInfo, "idUsingResourceServer", "testId");

        KakaoUserInfo.Properties properties = new KakaoUserInfo.Properties();
        ReflectionTestUtils.setField(properties, "name", "testName");

        KakaoUserInfo.KakaoAccount kakaoAccount = new KakaoUserInfo.KakaoAccount();
        ReflectionTestUtils.setField(kakaoAccount, "email", "testEmail");

        KakaoUserInfo.KakaoAccount.Profile profile = new KakaoUserInfo.KakaoAccount.Profile();
        ReflectionTestUtils.setField(profile, "profileUrl", "testProfileUrl");
        ReflectionTestUtils.setField(kakaoAccount, "profile", profile);
        ReflectionTestUtils.setField(kakaoUserInfo, "kakaoAccount", kakaoAccount);
        ReflectionTestUtils.setField(kakaoUserInfo, "properties", properties);

        return kakaoUserInfo;
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
        String kakaoScopeDelimiter = ",";
        return UriComponentsBuilder
            .fromUriString(fromUri)
            .queryParam("client_id", clientId)
            .queryParam("redirect_uri", redirectUri)
            .queryParam("response_type", "code")
            .queryParam("scope", String.join(kakaoScopeDelimiter, scope))
            .toUriString();
    }
}