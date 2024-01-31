package com.example.temp.member.application;

import com.example.temp.member.domain.Member;
import com.example.temp.member.domain.MemberRepository;
import com.example.temp.member.infrastructure.nickname.NicknameGenerator;
import com.example.temp.oauth.OAuthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    public static final int LOOP_MAX_CNT = 5;

    private final MemberRepository memberRepository;
    private final NicknameGenerator nicknameGenerator;

    /**
     * OAuthResponse과 nicknameGenerator에서 생성한 닉네임을 사용해 Member를 저장합니다.
     *
     * @param oAuthResponse
     * @return 저장된 Member 객체를 반환합니다.
     * @throws DataIntegrityViolationException 중복된 닉네임으로 Member를 저장하려 할 때 발생합니다.
     */
    @Transactional
    @Retryable(retryFor = DataIntegrityViolationException.class, maxAttempts = LOOP_MAX_CNT)
    public Member register(OAuthResponse oAuthResponse) {
        String nickname = nicknameGenerator.generate();

        Member member = Member.builder()
            .email(oAuthResponse.email())
            .profileUrl(oAuthResponse.profileUrl())
            .nickname(nickname)
            .build();
        memberRepository.save(member);
        return member;
    }

}
