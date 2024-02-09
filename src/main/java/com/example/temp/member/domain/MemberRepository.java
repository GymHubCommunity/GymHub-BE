package com.example.temp.member.domain;

import com.example.temp.member.domain.nickname.Nickname;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByNickname(Nickname nickname);

}
