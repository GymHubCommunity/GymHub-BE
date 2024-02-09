package com.example.temp.image.dto.response;

import java.net.URL;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PresignedUrlResponse {

    private final String presignedUrl;

    @Builder
    private PresignedUrlResponse(String presignedUrl) {
        this.presignedUrl = presignedUrl;
    }

    public static PresignedUrlResponse create(URL presignedUrl) {
        return PresignedUrlResponse.builder()
            .presignedUrl(presignedUrl.toString())
            .build();
    }
}
