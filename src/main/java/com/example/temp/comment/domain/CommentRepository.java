package com.example.temp.comment.domain;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c JOIN FETCH c.member JOIN FETCH c.post WHERE c.post.id = :postId ORDER BY c.registeredAt DESC")
    Slice<Comment> findByPostId(@Param("postId") Long postId, Pageable pageable);
}
