package com.example.temp.image.application;

import com.example.temp.common.dto.UserContext;
import java.net.URL;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final S3Presigner s3Presigner;

    public URL createPresignedUrl(UserContext userContext) {
        PutObjectRequest objectRequest = PutObjectRequest.builder()
            .bucket("test240209")
            .key("test")
            .build();

        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(r ->
            r.signatureDuration(Duration.ofSeconds(300))
                .putObjectRequest(objectRequest));

        return presignedRequest.url();
    }
}
