package com.example.temp.member.application;

import com.example.temp.auth.dto.response.MemberInfo;
import com.example.temp.common.dto.UserContext;
import com.example.temp.common.exception.ApiException;
import com.example.temp.common.exception.ErrorCode;
import com.example.temp.image.domain.ImageRepository;
import com.example.temp.member.domain.Member;
import com.example.temp.member.domain.MemberRepository;
import com.example.temp.member.domain.PrivacyPolicy;
import com.example.temp.member.domain.nickname.Nickname;
import com.example.temp.member.domain.nickname.NicknameGenerator;
import com.example.temp.member.dto.request.MemberRegisterRequest;
import com.example.temp.member.dto.request.MemberUpdateRequest;
import com.example.temp.member.event.MemberDeletedEvent;
import com.example.temp.member.exception.NicknameDuplicatedException;
import com.example.temp.oauth.OAuthResponse;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
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
    private final ApplicationEventPublisher eventPublisher;
    private final ImageRepository imageRepository;

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
            Nickname nickname = nicknameGenerator.generate();
            if (memberRepository.existsByNickname(nickname.getValue())) {
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
     * 가입 처리가 완료되지 않은 회원을 가입시킵니다. profile url을 입력하지 않은 회원은 디폴트 이미지를 사용해 저장합니다.
     *
     * @param userContext 로그인한 사용자의 정보
     * @param request     회원 가입에 필요한 정보
     * @return 회원가입이 완료된 Member 객체의 정보를 반환합니다.
     * @throws ApiException NICKNAME_DUPLICATED: 닉네임이 중복되었을 때 발생합니다.
     * @throws ApiException MEMBER_ALREADY_REGISTER: 이미 가입이 완료된 멤버가 해당 요청을 호출할 때 발생합니다.
     */
    @Transactional
    public MemberInfo register(UserContext userContext, MemberRegisterRequest request) {
        Member member = memberRepository.findMemberIncludingUnregisteredById(userContext.id())
            .orElseThrow(() -> new ApiException(ErrorCode.AUTHENTICATED_FAIL));
        Nickname nickname = Nickname.create(request.nickname());
        if (memberRepository.existsByNickname(nickname.getValue())) {
            throw new ApiException(ErrorCode.NICKNAME_DUPLICATED);
        }
        if (request.profileUrl() != null && !imageRepository.existsByUrl(request.profileUrl())) {
            throw new ApiException(ErrorCode.IMAGE_NOT_FOUND);
        }
        member.init(nickname, request.profileUrl());
        return MemberInfo.of(member);
    }

    /**
     * 회원을 탈퇴시킵니다. 회원이 삭제되면 연관된 엔티티(팔로우)들이 모두 삭제됩니다.
     *
     * @param userContext 로그인한 사용자의 정보
     * @param targetId    탈퇴시킬 대상의 ID
     * @throws ApiException AUTHORIZED_FAIL: 로그인한 사용자와 탈퇴를 원하는 대상의 ID가 일치하지 않을 때 발생합니다.
     */
    @Transactional
    public void withdraw(UserContext userContext, long targetId) {
        if (userContext.id() != targetId) {
            throw new ApiException(ErrorCode.AUTHORIZED_FAIL);
        }
        Member member = memberRepository.findById(userContext.id())
            .orElseThrow(() -> new ApiException(ErrorCode.AUTHENTICATED_FAIL));
        member.delete();
        eventPublisher.publishEvent(MemberDeletedEvent.create(member.getId()));
    }

    @Transactional
    public void changePrivacy(UserContext userContext, PrivacyPolicy privacyPolicy) {
        Member member = memberRepository.findById(userContext.id())
            .orElseThrow(() -> new ApiException(ErrorCode.AUTHENTICATED_FAIL));
        member.changePrivacy(privacyPolicy);
    }

    @Transactional
    public void updateMemberInfo(UserContext userContext, MemberUpdateRequest request) {
        Member member = memberRepository.findById(userContext.id())
            .orElseThrow(() -> new ApiException(ErrorCode.AUTHENTICATED_FAIL));
        if (!isMemberOriginalNickname(member, request.nickname()) &&
            memberRepository.existsByNickname(request.nickname())) {
            throw new ApiException(ErrorCode.NICKNAME_DUPLICATED);
        }
        if (!imageRepository.existsByUrl(request.profileUrl())) {
            throw new ApiException(ErrorCode.IMAGE_NOT_FOUND);
        }
        member.setProfileUrl(request.profileUrl());
        member.setNickname(Nickname.create(request.nickname()));
    }

    private boolean isMemberOriginalNickname(Member member, String nickname) {
        Objects.requireNonNull(member);
        Objects.requireNonNull(nickname);
        return Objects.equals(member.getNicknameValue(), nickname);
    }

    public MemberInfo retrieveMemberInfo(long targetId) {
        Member member = memberRepository.findById(targetId)
            .orElseThrow(() -> new ApiException(ErrorCode.MEMBER_NOT_FOUND));
        return MemberInfo.of(member);
    }
}
