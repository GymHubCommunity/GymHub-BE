package com.example.temp.auth.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.temp.auth.dto.response.LoginMemberResponse;
import com.example.temp.member.application.MemberService;
import com.example.temp.member.domain.Member;
import com.example.temp.oauth.OAuthProviderResolver;
import com.example.temp.oauth.OAuthProviderType;
import com.example.temp.oauth.OAuthResponse;
import com.example.temp.oauth.application.OAuthService;
import com.example.temp.oauth.domain.OAuthInfo;
import com.example.temp.oauth.domain.OAuthInfoRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OAuthServiceUnitTest {

    OAuthService oAuthService;

    @Mock
    OAuthProviderResolver oAuthProviderResolver;

    @Mock
    OAuthInfoRepository oAuthInfoRepository;

    @Mock
    MemberService memberService;

    OAuthResponse oAuthResponse;

    OAuthInfo oAuthInfo;

    Member member;

    @BeforeEach
    void setUp() {
        oAuthService = new OAuthService(oAuthProviderResolver, oAuthInfoRepository, memberService);
        oAuthResponse = new OAuthResponse(OAuthProviderType.GOOGLE, "이메일", "닉네임", "123", "프로필주소");
        member = Member.builder().build();
        oAuthInfo = OAuthInfo.builder()
            .member(member)
            .build();
    }

    @Test
    @DisplayName("OAuth 로그인을 처음 한 사용자는 로그인에 성공한다")
    void successFirstLogin() throws Exception {
        // given
        when(oAuthProviderResolver.fetch(any(OAuthProviderType.class), anyString()))
            .thenReturn(oAuthResponse);
        when(oAuthInfoRepository.findByIdUsingResourceServerAndType(anyString(), any(OAuthProviderType.class)))
            .thenReturn(Optional.empty());
        when(memberService.register(any(OAuthResponse.class)))
            .thenReturn(member);

        // when
        LoginMemberResponse response = oAuthService.login("google", "1234");

        // then
        assertThat(response.id()).isEqualTo(member.getId());
        assertThat(response.email()).isEqualTo(member.getEmail());
        assertThat(response.profileUrl()).isEqualTo(member.getProfileUrl());
    }

    @Test
    @DisplayName("기존 OAuth 로그인을 했던 사용자는 로그인에 성공한다")
    void successLoginThatUserAlreadyLogin() throws Exception {
        // given
        when(oAuthProviderResolver.fetch(any(OAuthProviderType.class), anyString()))
            .thenReturn(oAuthResponse);
        when(oAuthInfoRepository.findByIdUsingResourceServerAndType(anyString(), any(OAuthProviderType.class)))
            .thenReturn(Optional.of(oAuthInfo));

        // when
        LoginMemberResponse response = oAuthService.login("google", "1234");

        // then
        assertThat(response.id()).isEqualTo(member.getId());
        assertThat(response.email()).isEqualTo(member.getEmail());
        assertThat(response.profileUrl()).isEqualTo(member.getProfileUrl());
    }

    @Test
    @DisplayName("OAuth 로그인을 처음 한 사용자는 DB에 저장된다")
    void saveDatabaseIfFirstLogin() throws Exception {
        // given
        when(oAuthProviderResolver.fetch(any(OAuthProviderType.class), anyString()))
            .thenReturn(oAuthResponse);
        when(oAuthInfoRepository.findByIdUsingResourceServerAndType(anyString(), any(OAuthProviderType.class)))
            .thenReturn(Optional.empty());
        when(memberService.register(any(OAuthResponse.class)))
            .thenReturn(member);

        // when
        oAuthService.login("google", "1234");

        // then
        verify(memberService, times(1))
            .register(any(OAuthResponse.class));
        verify(oAuthInfoRepository, times(1))
            .save(any(OAuthInfo.class));
    }

    @Test
    @DisplayName("기존에 OAuth 로그인을 한 사용자는 DB에 저장되지 않는다")
    void notSaveDatabaseIfAlreadyLogin() throws Exception {
        // given
        when(oAuthProviderResolver.fetch(any(OAuthProviderType.class), anyString()))
            .thenReturn(oAuthResponse);
        when(oAuthInfoRepository.findByIdUsingResourceServerAndType(anyString(), any(OAuthProviderType.class)))
            .thenReturn(Optional.of(oAuthInfo));

        // when
        oAuthService.login("google", "1234");

        // then
        verify(memberService, never())
            .register(any(OAuthResponse.class));
    }

}