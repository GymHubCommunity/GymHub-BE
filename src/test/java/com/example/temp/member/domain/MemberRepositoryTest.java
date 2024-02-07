package com.example.temp.member.domain;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    EntityManager em;

    @Test
    @DisplayName("해당 닉네임이 DB에 존재하면 true를 반환한다.")
    void existsByNicknameSuccess() throws Exception {
        String nickname = "firstNick";
        assertThat(memberRepository.existsByNickname(nickname)).isFalse();

    }

    @Test
    @DisplayName("해당 닉네임이 DB에 존재하지 않으면 false를 반환한다.")
    void existsByNicknameFailDuplicated() throws Exception {
        // given
        String nickname = "duplicated";
        Member member = createMember(nickname);
        em.persist(member);

        // when & then
        assertThat(memberRepository.existsByNickname(nickname)).isTrue();

    }

    private Member createMember(String nickname) {
        Member member = Member.builder()
            .nickname(nickname)
            .email("이멜")
            .profileUrl("프로필")
            .followStrategy(FollowStrategy.EAGER)
            .build();
        em.persist(member);
        return member;
    }
}