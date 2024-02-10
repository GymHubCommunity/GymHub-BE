package com.example.temp.image.application;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.temp.common.exception.ApiException;
import com.example.temp.common.exception.ErrorCode;
import com.example.temp.common.properties.S3Properties;
import com.example.temp.image.dto.request.PresignedUrlRequest;
import java.net.URL;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ImageServiceTest {

    @Autowired
    ImageService imageService;

    @Autowired
    S3Properties s3Properties;

    @Test
    @DisplayName("서버에서 지정한 사이즈보다 큰 이미지에 대해서는 presigned url을 생성하지 않는다")
    void createFailImageTooBig() throws Exception {
        // given
        long contentLength = s3Properties.imgMaxContentLength() + 1;
        String extValue = "JPEG";
        PresignedUrlRequest request = new PresignedUrlRequest(contentLength, extValue);

        // when & then
        assertThatThrownBy(() -> imageService.createPresignedUrl(request))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.IMAGE_TOO_BIG.getMessage());
    }

    @Test
    @DisplayName("이미지가 아닌 확장자에 대해서는 presigned url을 생성하지 않는다.")
    void createFailInvalidExt() throws Exception {
        // given
        long contentLength = s3Properties.imgMaxContentLength();
        String extValue = "TXT";
        PresignedUrlRequest request = new PresignedUrlRequest(contentLength, extValue);

        // when & then
        assertThatThrownBy(() -> imageService.createPresignedUrl(request))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.EXTENSION_NOT_SUPPORTED.getMessage());
    }

    @Test
    @DisplayName("존재하지 않는 확장자에 대해서는 presigned url을 생성하지 않는다.")
    void createFailNotExistsExt() throws Exception {
        // given
        long contentLength = s3Properties.imgMaxContentLength();
        String extValue = "NOT_FOUND";
        PresignedUrlRequest request = new PresignedUrlRequest(contentLength, extValue);

        // when & then
        assertThatThrownBy(() -> imageService.createPresignedUrl(request))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.EXTENSION_NOT_SUPPORTED.getMessage());
    }

}