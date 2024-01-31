package com.example.temp.auth.oauth;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.temp.auth.oauth.domain.OAuthMember;
import com.example.temp.member.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OAuthMemberTest {

    @Test
    @DisplayName("OAuthResponse 인스턴스와 Member 인스턴스를 사용해 OAuthMember를 생성한다")
    void createOAuthMemberUsingOAuthResponse() throws Exception {
        // given
        String idUsingResourceServer = "1234";
        Member member = Member.builder().build();
        OAuthProviderType type = OAuthProviderType.GOOGLE;

        // when
        OAuthMember oAuthMember = OAuthMember.of(idUsingResourceServer, type, member);

        // then
        assertThat(oAuthMember).isNotNull();
        assertThat(oAuthMember.getIdUsingResourceServer()).isEqualTo(idUsingResourceServer);
        assertThat(oAuthMember.getType()).isEqualTo(type);
        assertThat(oAuthMember.getMember()).isEqualTo(member);
    }
}