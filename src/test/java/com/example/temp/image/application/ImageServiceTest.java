package com.example.temp.image.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.temp.common.exception.ApiException;
import com.example.temp.common.exception.ErrorCode;
import com.example.temp.common.properties.S3Properties;
import com.example.temp.image.dto.request.PresignedUrlRequest;
import java.net.MalformedURLException;
import java.net.URL;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@SpringBootTest
class ImageServiceTest {

    ImageService imageService;

    S3Presigner s3Presigner;

    @Autowired
    S3Properties s3Properties;

    @BeforeEach
    void setUp() {
        s3Presigner = S3Presigner.builder()
            .region(Region.of(s3Properties.region()))
            .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create("accessKey", "secretKey")))
            .build();
        imageService = new ImageService(s3Presigner, s3Properties);
    }

    @Test
    @DisplayName("presigned url을 생성한다")
    void createPresignedUrl() throws MalformedURLException {
        long contentLength = s3Properties.imgMaxContentLength();
        String extValue = "JPEG";
        PresignedUrlRequest request = new PresignedUrlRequest(contentLength, extValue);

        // when
        URL presignedUrl = imageService.createPresignedUrl(request);

        // then
        validateUrl(presignedUrl);
    }

    private void validateUrl(URL presignedUrl) {
        assertThat(presignedUrl.getHost()).endsWith(".amazonaws.com");
        String bucketName = presignedUrl.getHost().split("\\.")[0];
        assertThat(bucketName).isEqualTo(s3Properties.bucket());
    }

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