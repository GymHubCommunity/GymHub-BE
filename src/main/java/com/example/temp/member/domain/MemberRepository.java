package com.example.temp.member.domain;

import com.example.temp.member.domain.nickname.Nickname;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    @Query("SELECT CASE WHEN COUNT(m) > 0 THEN true ELSE false END FROM Member m"
        + " WHERE m.nickname = :nickname"
        + " AND m.deleted = false")
    boolean existsByNickname(Nickname nickname);

    @Query("SELECT m FROM Member m WHERE m.id = :memberId AND m.deleted = false")
    Optional<Member> findById(@Param(value = "memberId") long memberId);

    @Query("SELECT m FROM Member m WHERE m.id = :memberId"
        + " AND m.registered = true"
        + " AND m.deleted = false")
    Optional<Member> findRegisteredMemberById(@Param(value = "memberId") long memberId);

}
