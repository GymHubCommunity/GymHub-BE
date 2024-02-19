package com.example.temp.image.application;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.temp.auth.domain.Role;
import com.example.temp.common.dto.UserContext;
import com.example.temp.common.exception.ApiException;
import com.example.temp.common.exception.ErrorCode;
import com.example.temp.common.properties.S3Properties;
import com.example.temp.common.utils.random.RandomGenerator;
import com.example.temp.image.domain.ImageRepository;
import com.example.temp.image.dto.request.PresignedUrlRequest;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
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

    @Autowired
    RandomGenerator randomGenerator;

    @Autowired
    ImageRepository imageRepository;

    UserContext userContext;

    @BeforeEach
    void setUp() {
        s3Presigner = S3Presigner.builder()
            .region(Region.of(s3Properties.region()))
            .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create("accessKey", "secretKey")))
            .build();
        imageService = new ImageService(s3Presigner, s3Properties, randomGenerator, imageRepository);
        userContext = new UserContext(1234L, Role.NORMAL);
    }

    @Test
    @DisplayName("presigned url을 생성한다")
    void createPresignedUrl() throws MalformedURLException {
        long contentLength = s3Properties.imgMaxContentLength();
        String extValue = "JPEG";
        PresignedUrlRequest request = new PresignedUrlRequest(contentLength, extValue);

        // when
        URL presignedUrl = imageService.createPresignedUrl(userContext, request);

        // then
        validateUrl(presignedUrl);
        String imageUrl = extractImageUrl(presignedUrl);

        assertThat(imageRepository.existsByUrl(imageUrl)).isTrue();
    }

    /**
     * host가 .amazonaws.com이어야 하고, 버킷명이 지정한 값으로 나와야 합니다. 또한, path는 [id.uuid] 형태로 이뤄졌는지 검사합니다.
     */
    private void validateUrl(URL presignedUrl) {
        assertThat(presignedUrl.getHost()).endsWith(".amazonaws.com");
        String bucketName = presignedUrl.getHost().split("\\.")[0];
        assertThat(bucketName).isEqualTo(s3Properties.bucket());

        String fileName = presignedUrl.getPath();
        String[] idAndUuid = fileName.split("\\.");
        assertThat(idAndUuid).hasSize(2)
            .doesNotContainNull();
    }

    @Test
    @DisplayName("서버에서 지정한 사이즈보다 큰 이미지에 대해서는 presigned url을 생성하지 않는다")
    void createFailImageTooBig() throws Exception {
        // given
        long contentLength = s3Properties.imgMaxContentLength() + 1;
        String extValue = "JPEG";
        PresignedUrlRequest request = new PresignedUrlRequest(contentLength, extValue);

        // when & then
        assertThatThrownBy(() -> imageService.createPresignedUrl(userContext, request))
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
        assertThatThrownBy(() -> imageService.createPresignedUrl(userContext, request))
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
        assertThatThrownBy(() -> imageService.createPresignedUrl(userContext, request))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.EXTENSION_NOT_SUPPORTED.getMessage());
    }

    private String extractImageUrl(URL url) {
        return Optional.of(url.toString().indexOf("?"))
            .filter(index -> index != -1)
            .map(index -> url.toString().substring(0, index))
            .orElse(url.toString());
    }
}