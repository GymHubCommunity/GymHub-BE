package com.example.temp.post.domain;

import com.example.temp.common.entity.BaseTimeEntity;
import com.example.temp.image.domain.Image;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "post_images")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PostImage extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_image_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    private String imageUrl;

    @Builder
    private PostImage(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public static PostImage createPostImage(Image image) {
        image.use();
        return PostImage.builder()
            .imageUrl(image.getUrl())
            .build();
    }

    //== 연관관계 편의 메소드 ==//
    public void relate(Post post) {
        if (this.post != null) {
            this.post.getPostImages().remove(this);
        }
        this.post = post;
        post.getPostImages().add(this);
    }
}
