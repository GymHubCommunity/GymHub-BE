package com.example.temp.auth.application;

import com.example.temp.auth.dto.response.LoginInfoResponse;
import com.example.temp.auth.oauth.OAuthMember;
import com.example.temp.auth.oauth.OAuthProviderResolver;
import com.example.temp.auth.oauth.OAuthProviderType;
import com.example.temp.auth.oauth.OAuthResponse;
import com.example.temp.auth.oauth.domain.OAuthMemberRepository;
import com.example.temp.member.domain.Member;
import com.example.temp.member.domain.MemberRepository;
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
    private final MemberRepository memberRepository;

    @Transactional
    public LoginInfoResponse login(String provider, String authCode) {
        OAuthResponse oAuthResponse = oAuthProviderResolver.fetch(provider, authCode);
        Member member = findOrSaveMember(provider, oAuthResponse);
        return LoginInfoResponse.of(member);
    }

    private Member findOrSaveMember(String provider, OAuthResponse oAuthResponse) {
        Optional<OAuthMember> oAuthMemberOpt = oAuthMemberRepository.findByIdUsingResourceServerAndType(
            oAuthResponse.idUsingResourceServer(),
            OAuthProviderType.find(provider));
        if (oAuthMemberOpt.isPresent()) {
            return oAuthMemberOpt.get().getMember();
        }
        Member savedMember = memberRepository.save(Member.of(oAuthResponse));
        OAuthMember oAuthMember = OAuthMember.from(oAuthResponse, savedMember);
        oAuthMemberRepository.save(oAuthMember);
        return savedMember;
    }

}
