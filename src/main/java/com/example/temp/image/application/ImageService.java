package com.example.temp.image.application;

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

    public URL createPresignedUrl() {
        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(r ->
            r.signatureDuration(Duration.ofSeconds(300))
                .putObjectRequest(createPutObjectRequest()));

        return presignedRequest.url();
    }

    private static Consumer<Builder> createPutObjectRequest() {
        return objectRequest -> objectRequest.bucket("test240209").key("test");
    }
}
