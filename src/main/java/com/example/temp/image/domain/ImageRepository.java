package com.example.temp.image.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

    boolean existsByUrl(String url);

    Image findByUrl(String url);

    List<Image> findByUrlIn(List<String> urls);

}
