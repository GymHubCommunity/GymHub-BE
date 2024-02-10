package com.example.temp.post.domain;

import com.example.temp.member.domain.Member;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findByMemberInOrderByCreatedAtDesc(List<Member> members, Pageable pageable);
}
