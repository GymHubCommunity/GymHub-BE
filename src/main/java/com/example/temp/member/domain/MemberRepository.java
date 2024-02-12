package com.example.temp.member.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    /**
     * 회원가입 처리가 완료되고, 삭제가 되지 않은 회원을 조회합니다.
     */
    @Query("SELECT m FROM Member m WHERE m.id = :memberId"
        + " AND m.registered = true"
        + " AND m.deleted = false")
    Optional<Member> findById(@Param(value = "memberId") long memberId);

    @Query("SELECT CASE WHEN COUNT(m) > 0 THEN true ELSE false END FROM Member m"
        + " WHERE m.nickname.value = :nickname"
        + " AND m.deleted = false")
    boolean existsByNickname(@Param(value = "nickname") String nickname);

    @Query("SELECT m FROM Member m WHERE m.id = :memberId AND m.deleted = false")
    Optional<Member> findMemberIncludingUnregisteredById(@Param(value = "memberId") long memberId);

}
