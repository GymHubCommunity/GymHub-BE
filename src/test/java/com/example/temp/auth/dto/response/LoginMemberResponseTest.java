package com.example.temp.auth.dto.response;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.temp.member.domain.Member;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class LoginMemberResponseTest {

    @Autowired
    EntityManager em;

    @Test
    @DisplayName("LoginInfoResponse를 생성한다")
    void create() throws Exception {
        // given
        Member member = Member.builder()
            .email("이멜")
            .profileUrl("프로필주소")
            .nickname("생성된 닉네임")
            .build();
        em.persist(member);

        // when
        LoginMemberResponse response = LoginMemberResponse.of(member);

        // then
        assertThat(response.id()).isNotNull();
        assertThat(response.profileUrl()).isEqualTo(member.getProfileUrl());
        assertThat(response.email()).isEqualTo(member.getEmail());
        assertThat(response.nickname()).isEqualTo(member.getNickname());
    }
}