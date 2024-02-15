package com.example.temp.hashtag.domain;

import com.example.temp.common.entity.BaseTimeEntity;
import com.example.temp.common.exception.ApiException;
import com.example.temp.common.exception.ErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "hashtags")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EqualsAndHashCode
public class Hashtag extends BaseTimeEntity {

    private static final String HASHTAG_REGEX = "^#[\\w가-힣]+$";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hashtag_id")
    private Long id;

    @Column(unique = true)
    private String name;

    @Builder
    public Hashtag(String name) {
        validate(name);
        this.name = name;
    }

    private void validate(String name) {
        if (!name.matches(HASHTAG_REGEX)) {
            throw new ApiException(ErrorCode.HASHTAG_PATTERN_MISMATCH);
        }
    }
}
