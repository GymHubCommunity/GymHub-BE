package com.example.temp.image.application;

import com.example.temp.common.dto.UserContext;
import com.example.temp.common.entity.Extension;
import com.example.temp.common.exception.ApiException;
import com.example.temp.common.exception.ErrorCode;
import com.example.temp.common.properties.S3Properties;
import com.example.temp.common.utils.random.RandomGenerator;
import com.example.temp.image.dto.request.PresignedUrlRequest;
import java.net.URL;
import java.time.Duration;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest.Builder;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

@Service
@RequiredArgsConstructor
public class ImageService {

    public static final int RANDOM_SIZE_ABOUT_ID = 10;
    public static final String DELIMITER = ".";

    private final S3Presigner s3Presigner;
    private final S3Properties s3Properties;
    private final RandomGenerator randomGenerator;

    public URL createPresignedUrl(UserContext userContext, PresignedUrlRequest request) {
        Long memberId = userContext.id();
        long contentLength = request.contentLength();
        if (contentLength > s3Properties.imgMaxContentLength()) {
            throw new ApiException(ErrorCode.IMAGE_TOO_BIG);
        }
        validateExtension(request.extension());

        String fileName = generateFileName(memberId);
        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(r ->
            r.signatureDuration(Duration.ofSeconds(s3Properties.presignedExpires()))
                .putObjectRequest(createPutObjectRequest(contentLength, fileName)));

        return presignedRequest.url();
    }

    private String generateFileName(Long memberId) {
        String randomId = randomGenerator.generateWithSeed(String.valueOf(memberId), RANDOM_SIZE_ABOUT_ID);
        String uuid = randomGenerator.generate();
        return randomId + DELIMITER + uuid;
    }

    private void validateExtension(String extValue) {
        try {
            Extension ext = Extension.valueOf(extValue.toUpperCase());
            if (!ext.isImageType()) {
                throw new ApiException(ErrorCode.EXTENSION_NOT_SUPPORTED);
            }
        } catch (IllegalArgumentException e) {
            throw new ApiException(ErrorCode.EXTENSION_NOT_SUPPORTED);
        }
    }

    private Consumer<Builder> createPutObjectRequest(long contentLength, String fileName) {
        return objectRequest -> objectRequest.bucket(s3Properties.bucket())
            .contentLength(contentLength)
            .key(fileName);
    }
}
