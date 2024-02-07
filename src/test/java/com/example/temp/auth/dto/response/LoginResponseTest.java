package com.example.temp.auth.dto.response;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.temp.member.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LoginResponseTest {

    @Test
    @DisplayName("of 메서드를 통해 LoginResponse를 생성한다.")
    void ofSuccess() throws Exception {
        // given
        String accessToken = "엑세스토큰";
        boolean registered = true;
        TokenInfo tokenInfo = TokenInfo.builder()
            .accessToken(accessToken)
            .refreshToken("리프레쉬")
            .build();
        Member member = Member.builder()
            .registered(registered)
            .build();
        MemberInfo memberInfo = MemberInfo.of(member);

        // when
        LoginResponse result = LoginResponse.of(tokenInfo, memberInfo);

        // then
        assertThat(result.accessToken()).isEqualTo(accessToken);
        assertThat(result.requiredAdditionalInfo()).isEqualTo(!registered);
        assertThat(result.userInfo()).isEqualTo(memberInfo);
    }
}