package com.example.temp.image.application;

import com.example.temp.common.dto.UserContext;
import com.example.temp.common.entity.Extension;
import com.example.temp.common.exception.ApiException;
import com.example.temp.common.exception.ErrorCode;
import com.example.temp.common.properties.S3Properties;
import com.example.temp.common.utils.random.RandomGenerator;
import com.example.temp.image.domain.Image;
import com.example.temp.image.domain.ImageRepository;
import com.example.temp.image.dto.request.PresignedUrlRequest;
import java.net.URL;
import java.time.Duration;
import java.util.Optional;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.s3.model.PutObjectRequest.Builder;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ImageService {

    public static final int RANDOM_SIZE_ABOUT_ID = 10;
    public static final String DELIMITER = ".";

    private final S3Presigner s3Presigner;
    private final S3Properties s3Properties;
    private final RandomGenerator randomGenerator;
    private final ImageRepository imageRepository;

    @Transactional
    public URL createPresignedUrl(UserContext userContext, PresignedUrlRequest request) {
        try {
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
            URL url = presignedRequest.url();
            saveImageUrl(url);
            return url;
        } catch (DataIntegrityViolationException e) {
            throw new ApiException(ErrorCode.IMAGE_NAME_DUPLICATED);
        }

    }

    private void saveImageUrl(URL url) {
        String imageUrl = extractImageUrl(url);
        imageRepository.save(Image.create(imageUrl));
    }

    private String extractImageUrl(URL url) {
        return Optional.of(url.toString().indexOf("?"))
            .filter(index -> index != -1)
            .map(index -> url.toString().substring(0, index))
            .orElse(url.toString());
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
