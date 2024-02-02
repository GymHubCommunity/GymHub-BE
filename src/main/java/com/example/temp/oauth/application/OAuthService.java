package com.example.temp.oauth.application;

import com.example.temp.auth.dto.response.LoginInfoResponse;
import com.example.temp.member.domain.Member;
import com.example.temp.member.domain.MemberRepository;
import com.example.temp.oauth.OAuthProviderResolver;
import com.example.temp.oauth.OAuthProviderType;
import com.example.temp.oauth.OAuthResponse;
import com.example.temp.oauth.domain.OAuthInfo;
import com.example.temp.oauth.domain.OAuthInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OAuthService {

    private final OAuthProviderResolver oAuthProviderResolver;
    private final OAuthInfoRepository oAuthInfoRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public LoginInfoResponse login(String provider, String authCode) {
        OAuthProviderType oAuthProviderType = OAuthProviderType.find(provider);
        OAuthResponse oAuthResponse = oAuthProviderResolver.fetch(oAuthProviderType, authCode);
        Member member = findMemberOrElseCreate(oAuthResponse);
        return LoginInfoResponse.of(member);
    }

    private Member findMemberOrElseCreate(OAuthResponse oAuthResponse) {
        return oAuthInfoRepository
            .findByIdUsingResourceServerAndType(oAuthResponse.idUsingResourceServer(), oAuthResponse.type())
            .map(OAuthInfo::getMember)
            .orElseGet(() -> saveMemberAndOAuthInfo(oAuthResponse.type(), oAuthResponse));
    }

    private Member saveMemberAndOAuthInfo(OAuthProviderType oAuthProviderType, OAuthResponse oAuthResponse) {
        Member savedMember = memberRepository.save(Member.of(oAuthResponse));
        OAuthInfo oAuthInfo = OAuthInfo.of(oAuthResponse.idUsingResourceServer(), oAuthProviderType, savedMember);
        oAuthInfoRepository.save(oAuthInfo);
        return savedMember;
    }

}
