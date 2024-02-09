package com.example.temp.image.application;

import com.example.temp.common.properties.S3Properties;
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

    public URL createPresignedUrl() {
        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(r ->
            r.signatureDuration(Duration.ofSeconds(s3Properties.presignedExpires()))
                .putObjectRequest(createPutObjectRequest()));

        return presignedRequest.url();
    }

    private Consumer<Builder> createPutObjectRequest() {
        return objectRequest -> objectRequest.bucket(s3Properties.bucket()).key("test");
    }
}
