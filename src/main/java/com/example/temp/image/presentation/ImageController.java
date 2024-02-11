package com.example.temp.image.presentation;

import com.example.temp.common.annotation.Login;
import com.example.temp.common.dto.UserContext;
import com.example.temp.image.application.ImageService;
import com.example.temp.image.dto.request.PresignedUrlRequest;
import com.example.temp.image.dto.response.PresignedUrlResponse;
import java.net.URL;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/images")
public class ImageController {

    private final ImageService imageService;

    @PostMapping("/presigned_url")
    public ResponseEntity<PresignedUrlResponse> providePresignedUrl(@Login UserContext userContext,
        @RequestBody PresignedUrlRequest request) {
        URL presignedUrl = imageService.createPresignedUrl(userContext, request);
        return ResponseEntity.ok(PresignedUrlResponse.create(presignedUrl));
    }
}
