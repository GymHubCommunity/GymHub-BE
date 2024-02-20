package com.example.temp.post.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostHashtagRepository extends JpaRepository<PostHashtag, Long> {

    @Query("DELETE FROM PostHashtag p WHERE p.post IN :posts")
    @Modifying
    void deleteAllInBatchByPostIn(@Param("posts") List<Post> posts);
}
