package com.example.temp.member.dto.request;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MemberRegisterRequestTest {

    @Test
    @DisplayName("생성자의 순서가 올바른지 테스트한다")
    void create() throws Exception {
        // given
        String profileUrl = "profileUrl";
        String nickname = "nickname";

        // when
        MemberRegisterRequest result = new MemberRegisterRequest(profileUrl, nickname);

        // then
        assertThat(result.profileUrl()).isEqualTo(profileUrl);
        assertThat(result.nickname()).isEqualTo(nickname);
    }

}