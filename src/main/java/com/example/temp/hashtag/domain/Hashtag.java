package com.example.temp.hashtag.domain;

import com.example.temp.common.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "hashtags")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Hashtag extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hashtag_id")
    private Long id;

    @Column(unique = true)
    private String name;

    @Builder
    public Hashtag(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof Hashtag)) {
            return false;
        }
        Hashtag hashTag = (Hashtag) o;
        return Objects.equals(getName(), hashTag.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}
