package com.example.temp.image.presentation;

import com.example.temp.image.application.ImageService;
import com.example.temp.image.dto.response.PresignedUrlResponse;
import java.net.URL;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/images")
public class ImageController {

    private final ImageService imageService;

    @PostMapping("/presigned_url")
    public ResponseEntity<PresignedUrlResponse> providePresignedUrl() {
        URL presignedUrl = imageService.createPresignedUrl();
        return ResponseEntity.ok(PresignedUrlResponse.create(presignedUrl));
    }
}
