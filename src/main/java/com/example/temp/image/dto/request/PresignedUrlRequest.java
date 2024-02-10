package com.example.temp.image.dto.request;

public record PresignedUrlRequest(
    long contentLength,
    String extension
) {

}
