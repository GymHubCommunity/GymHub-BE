package com.example.temp.follow.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.temp.member.domain.Member;
import jakarta.persistence.EntityManager;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class FollowRepositoryTest {

    @Autowired
    FollowRepository followRepository;

    @Autowired
    EntityManager em;

    @Test
    @DisplayName("fromId와 toId가 일치하는 Follow를 찾는다.")
    void findByFromIdAndToIdSuccess() throws Exception {
        // given
        Member fromMember = createMember();
        Member toMember = createMember();

        Follow follow = Follow.builder()
            .from(fromMember)
            .to(toMember)
            .build();
        em.persist(follow);

        // when
        Follow result = followRepository.findByFromIdAndToId(fromMember.getId(), toMember.getId()).get();

        // then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getFrom().getId()).isEqualTo(fromMember.getId());
        assertThat(result.getTo().getId()).isEqualTo(toMember.getId());
    }

    @Test
    @DisplayName("fromId와 toId가 일치하는 Follow가 없을 땐 Optional.empty를 반환한다.")
    void findByFromIdAndToIdNotFound() throws Exception {
        // given
        Member fromMember = createMember();
        Member toMember = createMember();

        Follow follow = Follow.builder()
            .from(fromMember)
            .to(toMember)
            .build();
        em.persist(follow);

        // when
        Optional<Follow> resultOpt = followRepository.findByFromIdAndToId(toMember.getId(), fromMember.getId());

        // then
        assertThat(resultOpt).isEmpty();
    }


    private Member createMember() {
        Member fromMember = Member.builder().build();
        em.persist(fromMember);
        return fromMember;
    }
}