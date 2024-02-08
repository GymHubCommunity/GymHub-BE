package com.example.temp.auth.dto.response;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.temp.common.entity.Email;
import com.example.temp.member.domain.Member;
import com.example.temp.member.infrastructure.nickname.Nickname;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LoginResponseTest {

    @Test
    @DisplayName("생성자의 순서가 올바른지 테스트한다")
    void create() throws Exception {
        // given
        String accessToken = "엑세스토큰";
        boolean requiredAdditionalInfo = true;
        Member member = saveMember();
        MemberInfo memberInfo = MemberInfo.of(member);

        // when
        LoginResponse result = new LoginResponse(accessToken, requiredAdditionalInfo, memberInfo);

        // then
        assertThat(result.accessToken()).isEqualTo(accessToken);
        assertThat(result.requiredAdditionalInfo()).isEqualTo(requiredAdditionalInfo);
        assertThat(result.userInfo()).isEqualTo(memberInfo);
    }

    @Test
    @DisplayName("of 메서드를 통해 LoginResponse를 생성한다.")
    void ofSuccess() throws Exception {
        // given
        String accessToken = "엑세스토큰";
        TokenInfo tokenInfo = TokenInfo.builder()
            .accessToken(accessToken)
            .refreshToken("리프레쉬")
            .build();
        Member member = saveMember();
        MemberInfo memberInfo = MemberInfo.of(member);

        // when
        LoginResponse result = LoginResponse.of(tokenInfo, memberInfo);

        // then
        assertThat(result.accessToken()).isEqualTo(accessToken);
        assertThat(result.requiredAdditionalInfo()).isEqualTo(!memberInfo.registered());
        assertThat(result.userInfo()).isEqualTo(memberInfo);
    }

    private Member saveMember() {
        return Member.builder()
            .nickname(Nickname.create("nickname"))
            .email(Email.create("email@naver.com"))
            .registered(true)
            .build();
    }

}