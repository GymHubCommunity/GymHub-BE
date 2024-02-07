package com.example.temp.member.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.temp.exception.ApiException;
import com.example.temp.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MemberTest {

    @Test
    @DisplayName("회원을 초기화한다")
    void init() throws Exception {
        // given
        String changedNickname = "변경할닉넴";
        String changedProfileUrl = "변경할프로필주소";
        Member member = Member.builder()
            .registered(false)
            .build();

        // when
        member.init(changedNickname, changedProfileUrl);

        // then
        assertThat(member.isRegistered()).isTrue();
        assertThat(member.getNickname()).isEqualTo(changedNickname);
        assertThat(member.getProfileUrl()).isEqualTo(changedProfileUrl);
    }

    @Test
    @DisplayName("초기화되어있던 회원은 초기화할 수 없다")
    void initFailAlreadyInit() throws Exception {
        // given
        String changedNickname = "변경할닉넴";
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

        // when
        Member member = Member.buildInitStatus().build();

        // then
        assertThat(member.isRegistered()).isFalse();
    }
}