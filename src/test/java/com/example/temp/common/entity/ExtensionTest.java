package com.example.temp.common.entity;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ExtensionTest {

    @ParameterizedTest
    @DisplayName("확장자의 타입이 이미지인지 검사한다.")
    @ValueSource(strings = {"JPEG", "JPG", "PNG"})
    void checkImageType(String typeValue) throws Exception {
        // when
        Extension extension = Extension.valueOf(typeValue);

        // then
        Assertions.assertThat(extension.isImageType()).isTrue();
    }

    @DisplayName("확장자의 타입이 이미지가 아닌지 검사한다.")
    void checkNotImageType() throws Exception {
        // when
        Extension extension = Extension.valueOf("TXT");

        // then
        Assertions.assertThat(extension.isImageType()).isFalse();
    }

}