package com.example.temp.image.dto.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URL;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PresignedUrlResponseTest {

    @Test
    @DisplayName("응답을 생성한다.")
    void create() throws Exception {
        // given
        URL presignedUrl = new URL("http://test.com");

        // when
        PresignedUrlResponse result = PresignedUrlResponse.create(presignedUrl);

        // then
        assertThat(result.getPresignedUrl()).isEqualTo(presignedUrl.toString());
    }

}