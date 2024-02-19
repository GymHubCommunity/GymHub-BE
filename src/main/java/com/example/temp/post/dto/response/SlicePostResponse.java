package com.example.temp.post.dto.response;

import com.example.temp.post.domain.Post;
import java.util.List;
import lombok.Builder;
import org.springframework.data.domain.Slice;

@Builder
public record SlicePostResponse(
    List<PostElementResponse> posts,
    boolean hasNext
) {

    public static SlicePostResponse from(Slice<Post> posts) {
        List<PostElementResponse> postElements = posts.stream()
            .map(PostElementResponse::from)
            .toList();
        return new SlicePostResponse(postElements, posts.hasNext());
    }
}
