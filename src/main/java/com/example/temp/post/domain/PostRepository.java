package com.example.temp.post.domain;

import com.example.temp.member.domain.Member;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    Slice<Post> findByMemberInOrderByRegisteredAtDesc(List<Member> members, Pageable pageable);
}
