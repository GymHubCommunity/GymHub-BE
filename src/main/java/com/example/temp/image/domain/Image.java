package com.example.temp.image.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "images")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long id;

    @Column(unique = true, nullable = false)
    private String url;

    /**
     * 다른 엔티티(Post)에서 해당 이미지를 사용할 때 true 값을 갖습니다.
     */
    private boolean used;

    @Builder
    private Image(String url, boolean used) {
        this.url = url;
        this.used = used;
    }

    /**
     * 초기 상태 이미지를 생성합니다.
     *
     * @param url
     */
    public static Image create(String url) {
        return Image.builder()
            .url(url)
            .used(false)
            .build();
    }

    public void activate() {
        this.used = true;
    }

    public void deactivate() {
        this.used = false;
    }

}
