package com.example.temp.member.application;

import com.example.temp.auth.dto.response.MemberInfo;
import com.example.temp.exception.ApiException;
import com.example.temp.exception.ErrorCode;
import com.example.temp.member.domain.Member;
import com.example.temp.member.domain.MemberRepository;
import com.example.temp.member.dto.request.MemberRegisterRequest;
import com.example.temp.member.exception.NicknameDuplicatedException;
import com.example.temp.member.infrastructure.nickname.NicknameGenerator;
import com.example.temp.oauth.OAuthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.retry.annotation.Backoff;
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
     * OAuthResponse과 nicknameGenerator에서 생성한 닉네임을 사용해 Member를 저장합니다. NicknameDuplicatedException 발생 시 최대 다섯 번 재시도를 하며
     * 복구를 시도합니다.
     *
     * @param oAuthResponse
     * @return DB에 저장한 Member 객체를 반환합니다.
     * @throws NicknameDuplicatedException 중복된 닉네임으로 Member를 저장하려 할 때 발생합니다.
     */
    @Transactional
    @Retryable(retryFor = NicknameDuplicatedException.class, maxAttempts = LOOP_MAX_CNT, backoff = @Backoff(delay = 0))
    public Member saveInitStatusMember(OAuthResponse oAuthResponse) {
        try {
            String nickname = nicknameGenerator.generate();
            if (memberRepository.existsByNickname(nickname)) {
                throw new NicknameDuplicatedException();
            }
            Member member = oAuthResponse.toInitStatusMemberWith(nickname);
            memberRepository.save(member);
            return member;
        } catch (DataIntegrityViolationException e) {
            throw new NicknameDuplicatedException(e);
        }
    }

    /**
     * 초기화가 되어 있지 않던 Member를 사용 가능한 상태로 변경합니다.
     *
     * @param executorId 로그인한 사용자의 ID
     * @param request
     * @return 회원가입이 완료된 Member 객체의 정보를 반환합니다.
     */
    @Transactional
    public MemberInfo register(long executorId, MemberRegisterRequest request) {
        Member member = memberRepository.findById(executorId)
            .orElseThrow(() -> new ApiException(ErrorCode.AUTHENTICATED_FAIL));
        member.init(request.nickname(), request.profileUrl());
        return MemberInfo.of(member);
    }
}
