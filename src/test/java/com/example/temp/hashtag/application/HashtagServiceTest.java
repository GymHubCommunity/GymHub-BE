package com.example.temp.hashtag.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.temp.hashtag.domain.Hashtag;
import com.example.temp.hashtag.domain.HashtagRepository;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class HashtagServiceTest {

    @Autowired
    private HashtagService hashtagService;

    @Autowired
    private HashtagRepository hashtagRepository;

    @DisplayName("새로운 해시태그가 들어오면 해시태그를 저장할 수 있다.")
    @Test
    void saveHashtag() {
        // given
        List<String> names = List.of("#test1", "#test2", "#test3");

        // when
        List<Hashtag> hashtags = hashtagService.saveHashtag(names);

        // then
        assertThat(hashtags).hasSize(3)
            .extracting("name")
            .containsExactly("#test1", "#test2", "#test3");
    }

    @DisplayName("리스트가 비어 있는 경우 비어 있는 해시태그 리스트를 반환한다.")
    @Test
    void saveHashtagEmptyList() {
        // given
        List<String> names = Collections.emptyList();

        // when
        List<Hashtag> hashtags = hashtagService.saveHashtag(names);

        // then
        assertThat(hashtags).isEmpty();
    }


    @DisplayName("이미 존재하는 해시태그를 저장하는 경우 존재하는 해시태그를 반환한다.")
    @Test
    void saveDuplicateHashtag() {
        // given
        String hashtagName = "#test";
        Hashtag existingHashtag = hashtagRepository.save(Hashtag
            .builder()
            .name(hashtagName)
            .build());

        List<String> names = List.of(hashtagName);

        // when
        List<Hashtag> hashtags = hashtagService.saveHashtag(names);

        // then
        assertThat(hashtags).hasSize(1);
        assertThat(hashtags.get(0)).isEqualTo(existingHashtag);
    }
}