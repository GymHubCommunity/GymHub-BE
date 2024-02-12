package com.example.temp.image.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ImageTest {

    @Test
    @DisplayName("presigned 요청 시 서버에 이미지를 생성한다.")
    void create() throws Exception {
        // given
        String fileName = "randomFileName";

        // when
        Image image = Image.create(fileName);

        // then
        assertThat(image.getUrl()).isEqualTo(fileName);
        assertThat(image.isUsed()).isFalse();
    }

}