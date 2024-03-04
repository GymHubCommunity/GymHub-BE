package com.example.temp.member.domain;

import static com.example.temp.member.domain.FollowStrategy.EAGER;
import static com.example.temp.member.domain.PrivacyPolicy.PRIVATE;
import static com.example.temp.member.domain.PrivacyPolicy.PUBLIC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.temp.common.entity.Email;
import com.example.temp.common.exception.ApiException;
import com.example.temp.common.exception.ErrorCode;
import com.example.temp.member.domain.nickname.Nickname;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class MemberTest {

    @Test
    @DisplayName("회원을 초기화한다")
    void init() throws Exception {
        // given
        Nickname changedNickname = Nickname.create("변경할닉넴");
        String changedProfileUrl = "변경할프로필주소";
        Member member = Member.builder()
            .registered(false)
            .build();

        // when
        member.init(changedNickname, changedProfileUrl);

        // then
        assertThat(member.isRegistered()).isTrue();
        assertThat(member.getPrivacyPolicy()).isEqualTo(PUBLIC);
        assertThat(member.getFollowStrategy()).isEqualTo(EAGER);
        assertThat(member.getNickname()).isEqualTo(changedNickname);
        assertThat(member.getProfileUrl()).isEqualTo(changedProfileUrl);
    }

    @Test
    @DisplayName("회원을 초기화할 때, 프로필 이미지를 입력하지 않으면 디폴트 이미지를 가진 회원이 생성된다.")
    void initNotProfileUrl() throws Exception {
        // given
        Nickname changedNickname = Nickname.create("변경할닉넴");
        String changedProfileUrl = null;
        Member member = Member.builder()
            .registered(false)
            .build();

        // when
        member.init(changedNickname, changedProfileUrl);

        // then
        assertThat(member.getProfileUrl()).isEqualTo(Member.DEFAULT_PROFILE);
        assertThat(member.isRegistered()).isTrue();
        assertThat(member.getPrivacyPolicy()).isEqualTo(PUBLIC);
        assertThat(member.getFollowStrategy()).isEqualTo(EAGER);
        assertThat(member.getNickname()).isEqualTo(changedNickname);
    }

    @Test
    @DisplayName("초기화되어있던 회원은 초기화할 수 없다")
    void initFailAlreadyInit() throws Exception {
        // given
        Nickname changedNickname = Nickname.create("변경할닉넴");
        String changedProfileUrl = "변경할프로필주소";
        Member member = Member.builder()
            .registered(true)
            .build();

        // when & then
        assertThatThrownBy(() -> member.init(changedNickname, changedProfileUrl))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.MEMBER_ALREADY_REGISTER.getMessage());
    }

    @Test
    @DisplayName("초기화가 되지 않은 멤버를 생성한다.")
    void buildInitStatus() throws Exception {
        // given
        Email email = Email.create("email");
        String profileUrl = "profileUrl";
        Nickname nickname = Nickname.create("nickname");

        // when
        Member member = Member.createInitStatus(email, profileUrl, nickname);

        // then
        assertThat(member.isRegistered()).isFalse();
        assertThat(member.isPublicAccount()).isFalse();
        assertThat(member.getFollowStrategy()).isEqualTo(FollowStrategy.LAZY);
        assertThat(member.getPrivacyPolicy()).isEqualTo(PRIVATE);
        assertThat(member.getEmail()).isEqualTo(email);
        assertThat(member.getProfileUrl()).isEqualTo(profileUrl);
        assertThat(member.getNickname()).isEqualTo(nickname);
    }

    @ParameterizedTest
    @DisplayName("계정을 공개 계정으로 만든다.")
    @ValueSource(strings = {"PUBLIC", "PRIVATE"})
    void changePublicAccount(String privacyStr) throws Exception {
        // given
        Member member = Member.builder()
            .privacyPolicy(PrivacyPolicy.valueOf(privacyStr))
            .build();

        // when
        member.changePrivacy(PUBLIC);

        // then
        assertThat(member.getPrivacyPolicy()).isEqualTo(PUBLIC);
        assertThat(member.getFollowStrategy()).isEqualTo(PUBLIC.getDefaultFollowStrategy());
    }

    @ParameterizedTest
    @DisplayName("계정을 비공개 계정으로 만든다.")
    @ValueSource(strings = {"PUBLIC", "PRIVATE"})
    void changePrivateAccount(String privacyStr) throws Exception {
        // given
        Member member = Member.builder()
            .privacyPolicy(PrivacyPolicy.valueOf(privacyStr))
            .build();

        // when
        member.changePrivacy(PRIVATE);

        // then
        assertThat(member.getPrivacyPolicy()).isEqualTo(PRIVATE);
        assertThat(member.getFollowStrategy()).isEqualTo(PRIVATE.getDefaultFollowStrategy());
    }
}