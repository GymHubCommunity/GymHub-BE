package com.example.temp.image.dto.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PresignedUrlResponseTest {

    @Test
    @DisplayName("응답을 생성한다.")
    void create() throws Exception {
        // given
        String presignedUrl = "http://test.com";

        // when
        PresignedUrlResponse result = PresignedUrlResponse.builder()
            .presignedUrl(presignedUrl)
            .build();

        // then
        assertThat(result.getPresignedUrl()).isEqualTo(presignedUrl);
    }

}