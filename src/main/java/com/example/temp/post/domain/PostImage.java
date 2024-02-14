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
import jakarta.persistence.OneToOne;
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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id")
    private Image image;

    @Builder
    private PostImage(Image image) {
        this.image = image;
    }

    public static PostImage createPostImage(Image image) {
        image.use();
        return PostImage.builder()
            .image(image)
            .build();
    }

    //== 연관관계 편의 메소드 ==//
    public void addPost(Post post) {
        if (this.post != null) {
            this.post.getPostImages().remove(this);
        }
        this.post = post;
        post.getPostImages().add(this);
    }
}
