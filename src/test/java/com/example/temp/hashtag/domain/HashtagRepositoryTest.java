package com.example.temp.hashtag.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class HashtagRepositoryTest {

    @Autowired
    private HashtagRepository hashtagRepository;

    private Hashtag hashtag;

    @BeforeEach
    public void setup() {
        hashtag = new Hashtag("#test");
        hashtagRepository.save(hashtag);
    }

    @AfterEach
    public void cleanup() {
        hashtagRepository.deleteAllInBatch();
    }

    @DisplayName("저장 된 해시태그를 이름으로 찾아 올 수 있다.")
    @Test
    void findByNameTest() {
        //when
        Optional<Hashtag> result = hashtagRepository.findByName("#test");

        //given, then
        assertThat(result).isNotEmpty();
        assertThat(result.get().getName()).isEqualTo("#test");
    }

    @DisplayName("저장되어 있지 않은 해시태그는 찾을 수 없다.")
    @Test
    void findByNameNotFoundTest() {
        //when
        Optional<Hashtag> result = hashtagRepository.findByName("notFound");

        //given, then
        assertThat(result).isEmpty();
    }
}