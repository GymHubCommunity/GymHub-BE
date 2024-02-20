package com.example.temp.post.dto.request;

import jakarta.annotation.Nullable;
import java.util.List;

public record PostUpdateRequest(
    String content,
    @Nullable
    List<String> imageUrls,
    @Nullable
    List<String> hashTags
) {

}
