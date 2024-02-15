package com.example.temp.hashtag.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.temp.common.exception.ApiException;
import com.example.temp.common.exception.ErrorCode;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class HashtagTest {

    @DisplayName("해시태그는 #뒤에 한글, 영어, 숫자, '_' 기호만 올 수 있다.")
    @Test
    void validatePassCase() {
        //given
        Hashtag hashtag1 = createHashtag("#해시태그");
        Hashtag hashtag2 = createHashtag("#hashtag");
        Hashtag hashtag3 = createHashtag("#해시태그");
        Hashtag hashtag4 = createHashtag("#1234");
        Hashtag hashtag5 = createHashtag("#_해시tag123");
        List<Hashtag> hashtags = List.of(hashtag1, hashtag2, hashtag3, hashtag4, hashtag5);

        //when, then
        assertThat(hashtags).extracting("name")
            .containsExactly("#해시태그", "#hashtag", "#해시태그", "#1234", "#_해시tag123");
    }

    @DisplayName("해시태그는 반드시 #으로 시작해야 한다.")
    @ParameterizedTest
    @ValueSource(strings = {"해시태그", "hashtag", "hash#tag", "hashtag#"})
    void validateNotPassCase1(String hashtag) {
        //when, then
        assertThatThrownBy(() -> createHashtag(hashtag))
            .isInstanceOf(ApiException.class)
            .hasMessage(ErrorCode.HASHTAG_PATTERN_MISMATCH.getMessage());
    }

    @DisplayName("해시태그에는 +, -, @ 같은 특수문자는 들어올 수 없다.")
    @ParameterizedTest
    @ValueSource(strings = {"#해시태그+", "#해%시태그@", "@@@", "ahsh2@haah"})
    void validateNotPassCase2(String hashtag) {
        assertThatThrownBy(() -> createHashtag(hashtag))
            .isInstanceOf(ApiException.class)
            .hasMessage(ErrorCode.HASHTAG_PATTERN_MISMATCH.getMessage());
    }

    private Hashtag createHashtag(String name) {
        return Hashtag.builder()
            .name(name)
            .build();
    }
}