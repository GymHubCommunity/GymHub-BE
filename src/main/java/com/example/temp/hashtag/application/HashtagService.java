package com.example.temp.hashtag.application;

import com.example.temp.hashtag.domain.Hashtag;
import com.example.temp.hashtag.domain.HashtagRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HashtagService {

    private final HashtagRepository hashtagRepository;

    @Transactional
    public List<Hashtag> saveHashtag(List<String> names) {
        return names.stream()
            .map(this::findOrElseSave)
            .toList();
    }

    private Hashtag findOrElseSave(String name) {
        return hashtagRepository
            .findByName(name)
            .orElseGet(() -> hashtagRepository.save(Hashtag.builder()
                .name(name)
                .build()));
    }
}
