package com.example.temp.oauth.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.temp.member.domain.Member;
import com.example.temp.oauth.OAuthProviderType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OAuthInfoTest {

    @Test
    @DisplayName("OAuthResponse 인스턴스와 Member 인스턴스를 사용해 OAuthInfo를 생성한다")
    void createOAuthInfoUsingOAuthResponse() throws Exception {
        // given
        String idUsingResourceServer = "1234";
        Member member = Member.builder().build();
        OAuthProviderType type = OAuthProviderType.GOOGLE;

        // when
        OAuthInfo oAuthInfo = OAuthInfo.of(idUsingResourceServer, type, member);

        // then
        assertThat(oAuthInfo).isNotNull();
        assertThat(oAuthInfo.getIdUsingResourceServer()).isEqualTo(idUsingResourceServer);
        assertThat(oAuthInfo.getType()).isEqualTo(type);
        assertThat(oAuthInfo.getMember()).isEqualTo(member);
    }
}