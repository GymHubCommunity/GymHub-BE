package com.example.temp.oauth.application;

import com.example.temp.auth.dto.response.LoginInfoResponse;
import com.example.temp.member.application.MemberService;
import com.example.temp.member.application.MemberServiceFacade;
import com.example.temp.member.domain.Member;
import com.example.temp.oauth.OAuthProviderResolver;
import com.example.temp.oauth.OAuthProviderType;
import com.example.temp.oauth.OAuthResponse;
import com.example.temp.oauth.domain.OAuthMember;
import com.example.temp.oauth.domain.OAuthMemberRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OAuthService {

    private final OAuthProviderResolver oAuthProviderResolver;
    private final OAuthMemberRepository oAuthMemberRepository;
    private final MemberServiceFacade memberServiceFacade;

    @Transactional
    public LoginInfoResponse login(String provider, String authCode) {
        OAuthProviderType oAuthProviderType = OAuthProviderType.find(provider);
        OAuthResponse oAuthResponse = oAuthProviderResolver.fetch(oAuthProviderType, authCode);
        Member member = findOrSaveMember(oAuthProviderType, oAuthResponse);
        return LoginInfoResponse.of(member);
    }

    private Member findOrSaveMember(OAuthProviderType oAuthProviderType, OAuthResponse oAuthResponse) {
        Optional<OAuthMember> oAuthMemberOpt = oAuthMemberRepository.findByIdUsingResourceServerAndType(
            oAuthResponse.idUsingResourceServer(),
            oAuthProviderType);
        if (oAuthMemberOpt.isPresent()) {
            return oAuthMemberOpt.get().getMember();
        }
        Member savedMember = memberServiceFacade.register(oAuthResponse);
        OAuthMember oAuthMember = OAuthMember.of(oAuthResponse.idUsingResourceServer(), oAuthProviderType, savedMember);
        oAuthMemberRepository.save(oAuthMember);
        return savedMember;
    }

}
