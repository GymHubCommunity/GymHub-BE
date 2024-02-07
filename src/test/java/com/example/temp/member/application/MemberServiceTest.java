package com.example.temp.member.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.temp.auth.dto.response.MemberInfo;
import com.example.temp.common.entity.Email;
import com.example.temp.exception.ApiException;
import com.example.temp.exception.ErrorCode;
import com.example.temp.member.domain.FollowStrategy;
import com.example.temp.member.domain.Member;
import com.example.temp.member.dto.request.MemberRegisterRequest;
import com.example.temp.member.exception.NicknameDuplicatedException;
import com.example.temp.member.infrastructure.nickname.Nickname;
import com.example.temp.member.infrastructure.nickname.NicknameGenerator;
import com.example.temp.oauth.OAuthProviderType;
import com.example.temp.oauth.OAuthResponse;
import com.example.temp.oauth.OAuthUserInfo;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @MockBean
    NicknameGenerator nicknameGenerator;

    @Autowired
    EntityManager em;

    OAuthUserInfo oAuthUserInfo;

    OAuthResponse oAuthResponse;

    @BeforeEach
    void setUp() {
        oAuthUserInfo = createOAuthUserInfo();
        oAuthResponse = OAuthResponse.of(OAuthProviderType.GOOGLE, oAuthUserInfo);
    }


    @Test
    @DisplayName("임시 멤버를 생성한다")
    void registerTempSuccess() throws Exception {
        // given
        Nickname createdNickname = Nickname.create("중복되지않은_닉네임");
        when(nicknameGenerator.generate())
            .thenReturn(createdNickname);

        // when
        Member result = memberService.saveInitStatusMember(oAuthResponse);

        // then
        assertThat(result.getNickname()).isEqualTo(createdNickname);
        validateMemberIsSame(result, oAuthResponse);
    }

    @Test
    @DisplayName("중복된 닉네임으로는 임시 멤버를 생성할 수 없다.")
    void registerTempFailDuplicatedNickname() throws Exception {
        // given
        Nickname createdNickname = Nickname.create("중복되지않은_닉네임");
        saveMember(createdNickname);
        when(nicknameGenerator.generate())
            .thenReturn(createdNickname);

        // when & then
        assertThatThrownBy(() -> memberService.saveInitStatusMember(oAuthResponse))
            .isInstanceOf(NicknameDuplicatedException.class);
    }

    @Test
    @DisplayName("중복된 닉네임으로 임시 회원을 저장하려 할 때, 다섯 번까지 재시도한다.")
    void tryRegisterTempSeveralTimeIfDuplicatedNickname() throws Exception {
        // given
        Nickname createdNickname = Nickname.create("중복되지않은_닉네임");
        saveMember(createdNickname);
        when(nicknameGenerator.generate())
            .thenReturn(createdNickname);

        // when & then
        assertThatThrownBy(() -> memberService.saveInitStatusMember(oAuthResponse))
            .isInstanceOf(NicknameDuplicatedException.class);
        verify(nicknameGenerator, times(5))
            .generate();
    }

    @Test
    @DisplayName("닉네임 중복으로 임시 회원 저장을 실패한 뒤 다시 시도했을 때, 다섯 번 안에 중복되지 않은 닉네임이 만들어지면 임시 회원을 저장할 수 있다.")
    void tryRegisterTempSuccessRecovery() throws Exception {
        // given
        Nickname duplicatedNickname = Nickname.create("중복된_닉네임");
        Nickname createdNickname = Nickname.create("중복되지않은_닉네임");
        saveMember(duplicatedNickname);
        when(nicknameGenerator.generate())
            .thenReturn(duplicatedNickname, duplicatedNickname, duplicatedNickname,
                duplicatedNickname, createdNickname);

        // when
        Member result = memberService.saveInitStatusMember(oAuthResponse);

        // then
        assertThat(result.getNickname()).isEqualTo(createdNickname);
        validateMemberIsSame(result, oAuthResponse);
    }

    @Test
    @DisplayName("회원을 저장한다")
    void registerSuccess() throws Exception {
        // given
        Member member = saveMember(Nickname.create("닉넴"));
        String changedProfileUrl = "변경하는 프로필 주소";
        String changedNickname = "변경할 닉네임";

        // when
        MemberInfo result = memberService.register(member.getId(),
            new MemberRegisterRequest(changedProfileUrl, changedNickname));

        // then
        assertThat(result.registered()).isTrue();
        assertThat(result.id()).isEqualTo(member.getId());
        assertThat(result.profileUrl()).isEqualTo(changedProfileUrl);
        assertThat(result.nickname()).isEqualTo(changedNickname);
    }

    @Test
    @DisplayName("이미 회원가입된 사용자 계정으로 회원가입을 할 수 없다.")
    void registerFailAlreadyRegistered() throws Exception {
        // given
        Member member = saveRegisterMember(Nickname.create("닉넴"));
        String changedProfileUrl = "변경하는 프로필 주소";
        String changedNickname = "변경할 닉네임";

        // when & then
        assertThatThrownBy(() -> memberService.register(member.getId(),
            new MemberRegisterRequest(changedProfileUrl, changedNickname)))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.MEMBER_ALREADY_REGISTER.getMessage());
    }

    @Test
    @DisplayName("DB에 존재하지 않는 회원은 회원가입 요청이 불가능하다.")
    void registerFailNotAuthn() throws Exception {
        // given
        long notExistMemberId = 999_999_999L;

        // when & then
        assertThatThrownBy(() -> memberService.register(notExistMemberId,
            new MemberRegisterRequest("이미지url", "닉넴")))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.AUTHENTICATED_FAIL.getMessage());
    }

    private Member saveRegisterMember(Nickname nickname) {
        Member member = Member.builder()
            .nickname(nickname)
            .email(Email.create("이메일"))
            .profileUrl("프로필주소")
            .registered(true)
            .followStrategy(FollowStrategy.EAGER)
            .publicAccount(true)
            .build();
        em.persist(member);
        return member;
    }

    private Member saveMember(Nickname nickname) {
        Member member = Member.builder()
            .nickname(nickname)
            .email(Email.create("이메일"))
            .profileUrl("프로필주소")
            .registered(false)
            .followStrategy(FollowStrategy.EAGER)
            .publicAccount(true)
            .build();
        em.persist(member);
        return member;
    }

    private void validateMemberIsSame(Member result, OAuthResponse oAuthResponse) {
        assertThat(result.getId()).isNotNull();
        assertThat(result.getEmail()).isEqualTo(oAuthResponse.email());
        assertThat(result.getProfileUrl()).isEqualTo(oAuthResponse.profileUrl());
    }

    private OAuthUserInfo createOAuthUserInfo() {
        return new OAuthUserInfo() {
            @Override
            public String getProfileUrl() {
                return "프로필주소";
            }

            @Override
            public String getEmail() {
                return "이메일";
            }

            @Override
            public String getIdUsingResourceServer() {
                return "id";
            }

            @Override
            public String getName() {
                return "이름";
            }
        };
    }

}