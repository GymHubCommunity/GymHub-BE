package com.example.temp.oauth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.temp.oauth.OAuthProviderType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OAuthProviderTypeTest {

    @Test
    @DisplayName("google을 입력받아 타입을 찾는다")
    void findTypeGoogle() throws Exception {
        // when
        OAuthProviderType result = OAuthProviderType.find("google");

        // then
        assertThat(result).isEqualTo(OAuthProviderType.GOOGLE);
    }

    @Test
    @DisplayName("kakao를 입력받아 타입을 찾는다")
    void findTypeKakao() throws Exception {
        // when
        OAuthProviderType result = OAuthProviderType.find("kakao");

        // then
        assertThat(result).isEqualTo(OAuthProviderType.KAKAO);
    }

    @Test
    @DisplayName("존재하지 않는 타입을 찾으려 하면 예외를 반환한다")
    void findNotExistType() throws Exception {
        // when & then
        assertThatThrownBy(() -> OAuthProviderType.find("notExist"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("지원하지 않는 OAuth 타입입니다.");
    }
}