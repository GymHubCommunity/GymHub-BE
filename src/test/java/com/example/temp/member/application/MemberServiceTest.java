package com.example.temp.member.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.temp.member.domain.FollowStrategy;
import com.example.temp.member.domain.Member;
import com.example.temp.member.exception.NicknameDuplicatedException;
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
        String createdNickname = "중복되지않은_닉네임";
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
        String createdNickname = "중복된_닉네임";
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
        String createdNickname = "중복된_닉네임";
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
        String duplicatedNickname = "중복된_닉네임";
        String createdNickname = "중복되지_않은_닉네임";
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

    private Member saveMember(String nickname) {
        Member member = Member.builder()
            .nickname(nickname)
            .email("이메일")
            .profileUrl("프로필주소")
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