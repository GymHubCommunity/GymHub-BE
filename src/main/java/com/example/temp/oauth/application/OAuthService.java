package com.example.temp.oauth.application;

import com.example.temp.auth.dto.response.MemberInfo;
import com.example.temp.member.application.MemberService;
import com.example.temp.member.domain.Member;
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
    private final MemberService memberService;

    @Transactional
    public MemberInfo login(String provider, String authCode) {
        OAuthProviderType oAuthProviderType = OAuthProviderType.find(provider);
        OAuthResponse oAuthResponse = oAuthProviderResolver.fetch(oAuthProviderType, authCode);
        Member member = findMemberOrElseCreate(oAuthResponse);
        return MemberInfo.of(member);
    }

    private Member findMemberOrElseCreate(OAuthResponse oAuthResponse) {
        return oAuthInfoRepository
            .findByIdUsingResourceServerAndType(oAuthResponse.idUsingResourceServer(), oAuthResponse.type())
            .map(OAuthInfo::getMember)
            .orElseGet(() -> saveMemberAndOAuthInfo(oAuthResponse.type(), oAuthResponse));
    }

    private Member saveMemberAndOAuthInfo(OAuthProviderType oAuthProviderType, OAuthResponse oAuthResponse) {
        Member savedMember = memberService.saveInitStatusMember(oAuthResponse);
        OAuthInfo oAuthInfo = OAuthInfo.of(oAuthResponse.idUsingResourceServer(), oAuthProviderType, savedMember);
        oAuthInfoRepository.save(oAuthInfo);
        return savedMember;
    }

    public String getAuthorizedUrl(String provider) {
        OAuthProviderType oAuthProviderType = OAuthProviderType.find(provider);
        return oAuthProviderResolver.getAuthorizedUrl(oAuthProviderType);
    }
}
