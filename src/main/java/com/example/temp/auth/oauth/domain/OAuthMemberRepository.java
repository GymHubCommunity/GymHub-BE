package com.example.temp.auth.oauth.domain;

import com.example.temp.auth.oauth.OAuthMember;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OAuthMemberRepository extends JpaRepository<OAuthMember, Long> {

    // TODO JPQL 적용, 메서드 이름 변경
    Optional<OAuthMember> findByS(Long aLong, String provider);
}
