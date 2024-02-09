package com.example.temp.image.application;

import com.example.temp.common.dto.UserContext;
import com.example.temp.image.dto.response.PresignedUrlResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ImageService {

    public PresignedUrlResponse createPresignedUrl(UserContext userContext) {
        return null;
    }
}
