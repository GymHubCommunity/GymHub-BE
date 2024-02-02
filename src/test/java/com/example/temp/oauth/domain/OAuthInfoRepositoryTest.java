package com.example.temp.oauth.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.temp.oauth.OAuthProviderType;
import jakarta.persistence.EntityManager;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class OAuthInfoRepositoryTest {

    @Autowired
    OAuthInfoRepository repository;

    @Autowired
    EntityManager em;

    @Test
    @DisplayName("리소스서버의 ID와 타입이 일치하는 OAuthInfo를 찾는다")
    void findByIdUsingResourceServerAndType() throws Exception {
        // given
        String idUsingResourceServer = "123";
        OAuthProviderType type = OAuthProviderType.GOOGLE;
        OAuthInfo oAuthInfo = OAuthInfo.builder()
            .idUsingResourceServer(idUsingResourceServer)
            .type(type)
            .build();
        em.persist(oAuthInfo);

        // when
        OAuthInfo result = repository.findByIdUsingResourceServerAndType(idUsingResourceServer, type).get();

        // then
        assertThat(result).isEqualTo(oAuthInfo);
    }

    @Test
    @DisplayName("리소스서버의 ID와 타입이 일치하는 OAuthInfo가 없을 때 비어있는 Optional 반환한다")
    void findByIdUsingResourceServerAndTypeNotFound() throws Exception {
        // given
        String notRegisteredId = "123";
        OAuthProviderType type = OAuthProviderType.GOOGLE;

        // when
        Optional<OAuthInfo> resultOpt = repository.findByIdUsingResourceServerAndType(
            notRegisteredId, type);

        // then
        assertThat(resultOpt).isEmpty();
    }
}