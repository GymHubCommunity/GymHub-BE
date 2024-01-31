package com.example.temp.member.application;

import com.example.temp.member.domain.Member;
import com.example.temp.oauth.OAuthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberServiceFacade {

    public static final int LOOP_MAX_CNT = 5;

    private final MemberService memberService;

    @Transactional
    public Member register(OAuthResponse oAuthResponse) {
        return registerHelper(oAuthResponse, 1);
    }

    private Member registerHelper(OAuthResponse oAuthResponse, int loopCnt) {
        if (loopCnt > LOOP_MAX_CNT) {
            throw new IllegalStateException("서버에서 해당 요청을 처리할 수 없습니다.");
        }
        try {
            return memberService.register(oAuthResponse);
        } catch (DataIntegrityViolationException e) {
            registerHelper(oAuthResponse, loopCnt + 1);
        }
        return null;
    }

}
