package com.example.temp.auth.oauth.impl.google;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.temp.auth.oauth.OAuthProviderType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GoogleOAuthProviderTest {

    GoogleOAuthProvider provider;

    @Mock
    GoogleOAuthProperties properties;

    @BeforeEach
    void setUp() {
        provider = new GoogleOAuthProvider(properties);
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
}