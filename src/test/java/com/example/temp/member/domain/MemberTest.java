package com.example.temp.member.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.temp.auth.oauth.OAuthProviderType;
import com.example.temp.auth.oauth.OAuthResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MemberTest {

    @Test
    @DisplayName("OAuthResponse 객체를 사용해 Member를 생성한다")
    void createMemberUsingOAuthResponse() throws Exception {
        // given
        OAuthResponse oAuthResponse = new OAuthResponse(OAuthProviderType.GOOGLE, "이메일", "닉네임", 1L, "프로필주소");

        // when
        Member member = Member.of(oAuthResponse);

        // then
        assertThat(member).isNotNull();
        assertThat(member.getEmail()).isEqualTo(oAuthResponse.email());
        assertThat(member.getProfileUrl()).isEqualTo(oAuthResponse.profileUrl());
    }

}