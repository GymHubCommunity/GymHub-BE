package com.example.temp.oauth;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.temp.member.domain.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OAuthResponseTest {

    OAuthUserInfo oAuthUserInfo;

    @BeforeEach
    void setUp() {
        oAuthUserInfo = new OAuthUserInfo() {
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
    }

    @Test
    @DisplayName("OAuthProviderType과 OAuthUserInfo를 사용해 OAuthResponse를 생성한다")
    void create() throws Exception {
        // given
        OAuthProviderType type = OAuthProviderType.GOOGLE;

        // when
        OAuthResponse result = OAuthResponse.of(type, oAuthUserInfo);

        // then
        assertThat(result.type()).isEqualTo(type);
        assertThat(result.email()).isEqualTo(oAuthUserInfo.getEmail());
        assertThat(result.name()).isEqualTo(oAuthUserInfo.getName());
        assertThat(result.idUsingResourceServer()).isEqualTo(oAuthUserInfo.getIdUsingResourceServer());
        assertThat(result.profileUrl()).isEqualTo(oAuthUserInfo.getProfileUrl());
    }

    @Test
    @DisplayName("OAuthResponse와 nickname을 사용해 Member 객체를 만든다.")
    void createMemberUsingOAuthResponse() throws Exception {
        // given
        String nickname = "닉네임";
        OAuthResponse oAuthResponse = OAuthResponse.of(OAuthProviderType.GOOGLE, oAuthUserInfo);

        // when
        Member result = oAuthResponse.toMemberWithNickname(nickname);

        // then
        assertThat(result.getNickname()).isEqualTo(nickname);
        assertThat(result.getEmail()).isEqualTo(oAuthResponse.email());
        assertThat(result.getProfileUrl()).isEqualTo(oAuthResponse.profileUrl());
    }
}