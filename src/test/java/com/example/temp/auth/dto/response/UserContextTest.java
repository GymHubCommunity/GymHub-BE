package com.example.temp.auth.dto.response;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.temp.common.entity.Email;
import com.example.temp.member.domain.FollowStrategy;
import com.example.temp.member.domain.Member;
import com.example.temp.member.domain.PrivacyStrategy;
import com.example.temp.member.infrastructure.nickname.Nickname;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class UserContextTest {

    @Autowired
    EntityManager em;

    @Test
    @DisplayName("생성자의 순서가 올바른지 테스트한다")
    void create() throws Exception {
        // given
        long id = 1L;
        String email = "email";
        String profileUrl = "profile";
        String nickname = "nick";
        boolean registered = true;

        // when
        MemberInfo result = new MemberInfo(id, email, profileUrl, nickname, registered);

        // then
        assertThat(result.id()).isEqualTo(id);
        assertThat(result.email()).isEqualTo(email);
        assertThat(result.profileUrl()).isEqualTo(profileUrl);
        assertThat(result.nickname()).isEqualTo(nickname);
        assertThat(result.registered()).isEqualTo(registered);
    }

    @Test
    @DisplayName("LoginInfoResponse를 생성한다")
    void ofSuccess() throws Exception {
        // given
        Member member = Member.builder()
            .email(Email.create("이멜"))
            .profileUrl("프로필주소")
            .nickname(Nickname.create("생성된닉네임"))
            .registered(true)
            .followStrategy(FollowStrategy.EAGER)
            .privacyStrategy(PrivacyStrategy.PRIVATE)
            .build();
        em.persist(member);

        // when
        MemberInfo response = MemberInfo.of(member);

        // then
        assertThat(response.id()).isNotNull();
        assertThat(response.profileUrl()).isEqualTo(member.getProfileUrl());
        assertThat(response.email()).isEqualTo(member.getEmailValue());
        assertThat(response.nickname()).isEqualTo(member.getNicknameValue());
        assertThat(response.registered()).isEqualTo(member.isRegistered());
    }
}