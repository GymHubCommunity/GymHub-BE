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

    /**
     * ^ : 문자열의 시작을 나타냅니다.
     * # : 해시태그 기호를 나타냅니다.
     * [\w가-힣]은 모든 영문자, 숫자, 밑줄, 그리고 한글을 포함하는 문자를 의미합니다.
     * + : 앞의 문자나 그룹이 하나 이상 반복되는 패턴을 나타냅니다.
     * $ : 문자열의 끝을 나타냅니다.
     * #으로 시작해서 한글, 영문자, 숫자, 밑줄만 작성되었는지 확인하는 정규표현식 입니다.
     */
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
