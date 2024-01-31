package com.example.temp.oauth.domain;

import com.example.temp.oauth.OAuthProviderType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OAuthMemberRepository extends JpaRepository<OAuthMember, Long> {

    Optional<OAuthMember> findByIdUsingResourceServerAndType(String idUsingResourceServer, OAuthProviderType type);
}
