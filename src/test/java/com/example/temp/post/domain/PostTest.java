package com.example.temp.post.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.temp.common.entity.Email;
import com.example.temp.image.domain.Image;
import com.example.temp.member.domain.FollowStrategy;
import com.example.temp.member.domain.Member;
import com.example.temp.member.domain.PrivacyPolicy;
import com.example.temp.member.domain.nickname.Nickname;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PostTest {

    @DisplayName("PostImage에 등록된 url 중 처음등록된  url을 가져온다.")
    @Test
    void getImageUrl() {
        // Given
        Post post = Post.builder()
            .member(getMember())
            .content(Content.create("content"))
            .registeredAt(LocalDateTime.now())
            .build();
        List<PostImage> postImages = getPostImages(List.of("image1", "image2"), post);

        // When
        String imageUrl = post.getImageUrl().orElse(null);

        // Then
        assertThat(imageUrl).isEqualTo("image1");
    }

    private Member getMember() {
        return Member.builder()
            .email(Email.create("email@test.com"))
            .profileUrl("프로필")
            .nickname(Nickname.create("nick"))
            .followStrategy(FollowStrategy.EAGER)
            .privacyPolicy(PrivacyPolicy.PUBLIC)
            .build();
    }

    private List<PostImage> getPostImages(List<String> imageUrls, Post post) {
        return imageUrls.stream()
            .map(url -> {
                Image image = Image.create(url);
                PostImage postImage = PostImage.createPostImage(image);
                postImage.relate(post);
                return postImage;
            })
            .toList();
    }

}