package com.example.temp.hashtag.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
    @Test
    void validateNotPassCase1() {
        //when, then
        assertThatThrownBy(() -> createHashtag("해시태그"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("지원하지 않는 해시태그 형식입니다.");
    }

    @DisplayName("해시태그에는 +, -, @ 같은 특수문자는 들어올 수 없다.")
    @Test
    void validateNotPassCase2() {
        //when, then
        assertThatThrownBy(() -> createHashtag("#해시태그+"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("지원하지 않는 해시태그 형식입니다.");

        assertThatThrownBy(() -> createHashtag("#해%시태그@"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("지원하지 않는 해시태그 형식입니다.");
    }

    private Hashtag createHashtag(String name) {
        return Hashtag.builder()
            .name(name)
            .build();
    }
}