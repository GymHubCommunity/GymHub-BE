package com.example.temp.image.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ImageTest {

    @Test
    @DisplayName("이미지를 생성한다.")
    void create() throws Exception {
        // given
        String fileName = "randomFileName";

        // when
        Image image = Image.create(fileName);

        // then
        assertThat(image.getFileName()).isEqualTo(fileName);
    }

}