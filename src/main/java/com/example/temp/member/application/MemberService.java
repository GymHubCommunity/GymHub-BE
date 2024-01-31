package com.example.temp.member.application;

import com.example.temp.member.domain.Member;
import com.example.temp.member.infrastructure.nickname.NicknameGenerator;
import com.example.temp.oauth.OAuthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final NicknameGenerator nicknameGenerator;

    @Transactional
    public Member register(OAuthResponse oAuthResponse) {
        String nickname = nicknameGenerator.generate();
        return null;
    }

}
