package com.example.temp.image.application;

import com.example.temp.common.entity.Extension;
import com.example.temp.common.exception.ApiException;
import com.example.temp.common.exception.ErrorCode;
import com.example.temp.common.properties.S3Properties;
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

    private final S3Presigner s3Presigner;
    private final S3Properties s3Properties;

    public URL createPresignedUrl(PresignedUrlRequest request) {
        long contentLength = request.contentLength();
        if (contentLength > s3Properties.imgMaxContentLength()) {
            throw new ApiException(ErrorCode.IMAGE_TOO_BIG);
        }
        validateExtension(request.extension());

        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(r ->
            r.signatureDuration(Duration.ofSeconds(s3Properties.presignedExpires()))
                .putObjectRequest(createPutObjectRequest(contentLength)));

        return presignedRequest.url();
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

    private Consumer<Builder> createPutObjectRequest(long contentLength) {
        return objectRequest -> objectRequest.bucket(s3Properties.bucket())
            .contentLength(contentLength)
            .key("test");
    }
}
