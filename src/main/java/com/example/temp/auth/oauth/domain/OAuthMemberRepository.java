package com.example.temp.auth.oauth.domain;

import com.example.temp.auth.oauth.OAuthMember;
import com.example.temp.auth.oauth.OAuthProviderType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OAuthMemberRepository extends JpaRepository<OAuthMember, Long> {

    Optional<OAuthMember> findByIdUsingResourceServerAndType(Long idUsingResourceServer, OAuthProviderType type);
}
