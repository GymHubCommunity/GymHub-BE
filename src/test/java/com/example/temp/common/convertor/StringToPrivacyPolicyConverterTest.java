package com.example.temp.common.convertor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.temp.member.domain.PrivacyPolicy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StringToPrivacyPolicyConverterTest {

    StringToPrivacyPolicyConverter converter = new StringToPrivacyPolicyConverter();

    @Test
    @DisplayName("존재하지 않는 Policy에 대해 예외를 반환한다.")
    void policyNotFound() throws Exception {
        // given
        String input = "NOTFOUND";

        // when & then
        assertThatThrownBy(() -> converter.convert(input))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("입력된 값을 PrivacyPolicy로 변환한다.")
    void convert() throws Exception {
        // given
        String input = "PRIVATE";
        PrivacyPolicy privacyPolicy = PrivacyPolicy.valueOf(input);

        // when
        PrivacyPolicy result = converter.convert(input);

        // then
        assertThat(result).isEqualTo(privacyPolicy);
    }

    @Test
    @DisplayName("입력된 값의 좌우 공백을 제거한다")
    void trim() throws Exception {
        // given
        String input = "    PRIVATE \n";

        // when & then
        Assertions.assertDoesNotThrow(() -> converter.convert(input));
    }


    @Test
    @DisplayName("입력된 값의 대소문자를 신경쓰지 않는다")
    void ignoreAlphabetType() throws Exception {
        // given
        String input = "pRivaTE";

        // when & then
        Assertions.assertDoesNotThrow(() -> converter.convert(input));
    }
}