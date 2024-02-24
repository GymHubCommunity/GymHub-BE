package com.example.temp.post.domain;

import com.example.temp.member.domain.Member;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    Slice<Post> findAllByMemberInOrderByRegisteredAtDesc(List<Member> members, Pageable pageable);

    List<Post> findAllByMemberId(@Param("memberId") Long memberId);

    @Query("SELECT p "
        + "FROM Post p "
        + "JOIN p.postHashtags ph "
        + "JOIN ph.hashtag h "
        + "WHERE h.id = :hashtagId "
        + "AND p.member.privacyPolicy = 'PUBLIC' "
        + "ORDER BY p.registeredAt DESC")
    Page<Post> findAllPostByHashtag(@Param("hashtagId") Long hashtagId, Pageable pageable);

}
