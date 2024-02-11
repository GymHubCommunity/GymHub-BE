package com.example.temp.oauth.domain;

import com.example.temp.oauth.OAuthProviderType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OAuthInfoRepository extends JpaRepository<OAuthInfo, Long> {

    Optional<OAuthInfo> findByIdUsingResourceServerAndType(String idUsingResourceServer, OAuthProviderType type);
}
