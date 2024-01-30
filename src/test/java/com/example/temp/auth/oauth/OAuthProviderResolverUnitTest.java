package com.example.temp.auth.oauth;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OAuthProviderResolverUnitTest {

    OAuthProviderResolver resolver;

    @Mock
    OAuthProvider oAuthProvider1;

    @Mock
    OAuthProvider oAuthProvider2;

    @BeforeEach
    void setUp() {
        Set<OAuthProvider> providers = new HashSet<>(List.of(oAuthProvider1, oAuthProvider2));
        resolver = new OAuthProviderResolver(providers);
    }

    @Test
    @DisplayName("입력된 OAuthProviderType에 해당되는 Provider의 fetch를 실행한다")
    void fetchSuccess() throws Exception {
        // given
        when(oAuthProvider1.support(any(OAuthProviderType.class)))
            .thenReturn(true);

        // when
        resolver.fetch(OAuthProviderType.KAKAO, "123");

        // then
        verify(oAuthProvider1, times(1)).fetch(anyString());
        verify(oAuthProvider2, never()).fetch(anyString());
    }

    @Test
    @DisplayName("지원하지 않는 OAuthProviderType을 입력받으면 예외를 반환한다")
    void fetchFailNotSupportOAuthType() throws Exception {
        // given
        when(oAuthProvider1.support(any(OAuthProviderType.class)))
            .thenReturn(false);
        when(oAuthProvider2.support(any(OAuthProviderType.class)))
            .thenReturn(false);

        // when & then
        assertThatThrownBy(() -> resolver.fetch(OAuthProviderType.KAKAO, "123"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("지원하지 않는 OAuth 타입입니다.");
        verify(oAuthProvider1, never()).fetch(anyString());
        verify(oAuthProvider2, never()).fetch(anyString());
    }
}