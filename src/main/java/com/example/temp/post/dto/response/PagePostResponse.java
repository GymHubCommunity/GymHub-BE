package com.example.temp.post.dto.response;

import com.example.temp.post.domain.Post;
import java.util.List;
import lombok.Builder;
import org.springframework.data.domain.Page;

@Builder
public record PagePostResponse(
    List<PostElementResponse> posts,
    int totalPages,
    int totalElements
) {

    public static PagePostResponse from(Page<Post> posts) {
        List<PostElementResponse> postElements = posts.stream()
            .map(PostElementResponse::from)
            .toList();
        return new PagePostResponse(postElements, posts.getTotalPages(), (int) posts.getTotalElements());
    }
}
