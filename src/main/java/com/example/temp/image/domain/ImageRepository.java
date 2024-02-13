package com.example.temp.image.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

    boolean existsByUrl(String url);

    Image findByUrl(String url);

}
