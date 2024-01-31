package com.example.temp.auth.oauth;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OAuthResponseTest {

    @Test
    @DisplayName("OAuthProviderType과 OAuthUserInfo를 사용해 OAuthResponse를 생성한다")
    void create() throws Exception {
        // given
        OAuthProviderType type = OAuthProviderType.GOOGLE;
        OAuthUserInfo info = new OAuthUserInfo() {
            @Override
            public String getProfileUrl() {
                return "profile";
            }

            @Override
            public String getEmail() {
                return "email";
            }

            @Override
            public String getIdUsingResourceServer() {
                return "id";
            }

            @Override
            public String getName() {
                return "name";
            }
        };

        // when
        OAuthResponse result = OAuthResponse.of(type, info);

        // then
        assertThat(result.type()).isEqualTo(type);
        assertThat(result.email()).isEqualTo(info.getEmail());
        assertThat(result.name()).isEqualTo(info.getName());
        assertThat(result.idUsingResourceServer()).isEqualTo(info.getIdUsingResourceServer());
        assertThat(result.profileUrl()).isEqualTo(info.getProfileUrl());
    }

}