package com.example.temp.member.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.temp.auth.dto.response.MemberInfo;
import com.example.temp.common.dto.UserContext;
import com.example.temp.common.entity.Email;
import com.example.temp.common.exception.ApiException;
import com.example.temp.common.exception.ErrorCode;
import com.example.temp.member.domain.FollowStrategy;
import com.example.temp.member.domain.Member;
import com.example.temp.member.domain.PrivacyPolicy;
import com.example.temp.member.dto.request.MemberRegisterRequest;
import com.example.temp.member.exception.NicknameDuplicatedException;
import com.example.temp.member.domain.nickname.Nickname;
import com.example.temp.member.domain.nickname.NicknameGenerator;
import com.example.temp.oauth.OAuthProviderType;
import com.example.temp.oauth.OAuthResponse;
import com.example.temp.oauth.OAuthUserInfo;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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
        oAuthUserInfo = mockOAuthClientResponse();
        oAuthResponse = OAuthResponse.of(OAuthProviderType.GOOGLE, oAuthUserInfo);
    }

    private OAuthUserInfo mockOAuthClientResponse() {
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

    @Test
    @DisplayName("임시 멤버를 생성한다")
    void registerTempSuccess() throws Exception {
        // given
        Nickname createdNickname = Nickname.create("중복되지않은닉네임");
        when(nicknameGenerator.generate())
            .thenReturn(createdNickname);

        // when
        Member result = memberService.saveInitStatusMember(oAuthResponse);

        // then
        assertThat(result.getPrivacyPolicy()).isEqualTo(PrivacyPolicy.PRIVATE);
        assertThat(result.getFollowStrategy()).isEqualTo(FollowStrategy.LAZY);
        assertThat(result.getNickname()).isEqualTo(createdNickname);
        validateMemberIsSame(result, oAuthResponse);
    }

    @Test
    @DisplayName("중복된 닉네임으로는 임시 멤버를 생성할 수 없다.")
    void registerTempFailDuplicatedNickname() throws Exception {
        // given
        Nickname createdNickname = Nickname.create("중복닉네임");
        saveNotInitializedMember(createdNickname);
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
        Nickname createdNickname = Nickname.create("중복닉네임");
        saveNotInitializedMember(createdNickname);
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
        Nickname duplicatedNickname = Nickname.create("중복된닉네임");
        Nickname createdNickname = Nickname.create("중복되지않은닉네임");
        saveNotInitializedMember(duplicatedNickname);
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
        Member member = saveNotInitializedMember(Nickname.create("닉넴"));
        String changedProfileUrl = "변경할프로필";
        String changedNickname = "변경할닉네임";

        // when
        MemberInfo result = memberService.register(UserContext.from(member),
            new MemberRegisterRequest(changedProfileUrl, changedNickname));

        // then
        assertThat(member.getPrivacyPolicy()).isEqualTo(PrivacyPolicy.PUBLIC);
        assertThat(member.getFollowStrategy()).isEqualTo(FollowStrategy.EAGER);
        assertThat(result.registered()).isTrue();
        assertThat(result.id()).isEqualTo(member.getId());
        assertThat(result.profileUrl()).isEqualTo(changedProfileUrl);
        assertThat(result.nickname()).isEqualTo(changedNickname);
    }

    @Test
    @DisplayName("이미 회원가입된 사용자 계정으로 회원가입을 할 수 없다.")
    void registerFailAlreadyRegistered() throws Exception {
        // given
        Member member = saveRegisteredMember(Nickname.create("닉넴"));
        String changedProfileUrl = "변경하는프로필주소";
        String changedNickname = "변경할닉네임";

        // when & then
        assertThatThrownBy(() -> memberService.register(UserContext.from(member),
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
        assertThatThrownBy(() -> memberService.register(new UserContext(notExistMemberId),
            new MemberRegisterRequest("이미지url", "닉넴")))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.AUTHENTICATED_FAIL.getMessage());
    }

    @Test
    @DisplayName("존재하지 않는 회원은 계정 Privacy 상태를 바꿀 수 없다.")
    void changeStatusFail() throws Exception {
        // given
        long notExistMemberId = 999_999_999L;

        // when & then
        assertThatThrownBy(() -> memberService.changePrivacy(new UserContext(notExistMemberId), PrivacyPolicy.PRIVATE))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.AUTHENTICATED_FAIL.getMessage());
    }

    @ParameterizedTest
    @DisplayName("계정 Privacy 상태를 변경한다.")
    @ValueSource(strings = {"PRIVATE", "PUBLIC"})
    void changeStatus(String privacyStr) throws Exception {
        // given
        PrivacyPolicy targetPolicy = PrivacyPolicy.valueOf(privacyStr);
        Member member = saveRegisteredMember(Nickname.create("nick"));

        // when
        memberService.changePrivacy(UserContext.from(member), targetPolicy);

        // then
        assertThat(member.getPrivacyPolicy()).isEqualTo(targetPolicy);
    }

    private Member saveRegisteredMember(Nickname nickname) {
        return saveMember(nickname, true, PrivacyPolicy.PRIVATE);
    }

    private Member saveNotInitializedMember(Nickname nickname) {
        return saveMember(nickname, false, PrivacyPolicy.PRIVATE);
    }

    private Member saveMember(Nickname nickname, boolean registered, PrivacyPolicy privacyPolicy) {
        Member member = Member.builder()
            .nickname(nickname)
            .email(Email.create("이메일"))
            .profileUrl("프로필주소")
            .registered(registered)
            .privacyPolicy(privacyPolicy)
            .followStrategy(FollowStrategy.LAZY)
            .build();
        em.persist(member);
        return member;
    }

    private void validateMemberIsSame(Member result, OAuthResponse oAuthResponse) {
        assertThat(result.getId()).isNotNull();
        assertThat(result.getEmail()).isEqualTo(oAuthResponse.email());
        assertThat(result.getProfileUrl()).isEqualTo(oAuthResponse.profileUrl());
    }

}