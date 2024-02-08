package com.example.temp.oauth;

import static com.example.temp.member.domain.PrivacyPolicy.PRIVATE;
import static org.assertj.core.api.Assertions.assertThat;

import com.example.temp.common.entity.Email;
import com.example.temp.member.domain.FollowStrategy;
import com.example.temp.member.domain.Member;
import com.example.temp.member.infrastructure.nickname.Nickname;
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
        assertThat(result.email()).isEqualTo(Email.create(oAuthUserInfo.getEmail()));
        assertThat(result.name()).isEqualTo(oAuthUserInfo.getName());
        assertThat(result.idUsingResourceServer()).isEqualTo(oAuthUserInfo.getIdUsingResourceServer());
        assertThat(result.profileUrl()).isEqualTo(oAuthUserInfo.getProfileUrl());
    }

    @Test
    @DisplayName("OAuthResponse와 nickname을 사용해 초기화되지 않은 멤버를 만든다.")
    void createMemberUsingOAuthResponse() throws Exception {
        // given
        Nickname nickname = Nickname.create("닉네임");
        OAuthResponse oAuthResponse = OAuthResponse.of(OAuthProviderType.GOOGLE, oAuthUserInfo);

        // when
        Member result = oAuthResponse.toInitStatusMemberWith(nickname);

        // then
        assertThat(result.isRegistered()).isFalse();
        assertThat(result.isPublicAccount()).isFalse();
        assertThat(result.getFollowStrategy()).isEqualTo(FollowStrategy.LAZY);
        assertThat(result.getPrivacyPolicy()).isEqualTo(PRIVATE);
        assertThat(result.getNickname()).isEqualTo(nickname);
        assertThat(result.getEmail()).isEqualTo(oAuthResponse.email());
        assertThat(result.getProfileUrl()).isEqualTo(oAuthResponse.profileUrl());
    }
}