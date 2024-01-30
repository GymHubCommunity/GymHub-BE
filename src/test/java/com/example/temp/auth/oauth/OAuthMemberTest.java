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
        OAuthResponse oAuthResponse = new OAuthResponse(OAuthProviderType.GOOGLE, "이메일", "닉네임", "123", "프로필주소");
        Member member = Member.builder().build();

        // when
        OAuthMember oAuthMember = OAuthMember.of(oAuthResponse, member, OAuthProviderType.GOOGLE);

        // then
        assertThat(oAuthMember).isNotNull();
        assertThat(oAuthMember.getEmail()).isEqualTo(oAuthResponse.email());
        assertThat(oAuthMember.getNickname()).isEqualTo(oAuthResponse.name());
        assertThat(oAuthMember.getIdUsingResourceServer()).isEqualTo(oAuthResponse.idUsingResourceServer());
        assertThat(oAuthMember.getProfileUrl()).isEqualTo(oAuthResponse.profileUrl());
        assertThat(oAuthMember.getMember()).isEqualTo(member);
    }
}