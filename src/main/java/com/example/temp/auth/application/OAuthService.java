package com.example.temp.auth.application;

import com.example.temp.auth.dto.response.LoginInfoResponse;
import com.example.temp.auth.oauth.OAuthMember;
import com.example.temp.auth.oauth.OAuthProviderResolver;
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
        Optional<OAuthMember> oAuthMemberOpt = oAuthMemberRepository.findByS(oAuthResponse.idUsingResourceServer(),
            provider);
        if (oAuthMemberOpt.isEmpty()) {
            Member savedMember = memberRepository.save(Member.of(oAuthResponse));
            OAuthMember oAuthMember = OAuthMember.from(oAuthResponse, savedMember);
            oAuthMemberRepository.save(oAuthMember);
            return LoginInfoResponse.of(savedMember);
        } else {
            OAuthMember oAuthMember = oAuthMemberOpt.get();
            return LoginInfoResponse.of(oAuthMember.getMember());
        }
    }

}
