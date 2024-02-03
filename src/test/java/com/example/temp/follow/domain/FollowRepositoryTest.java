package com.example.temp.follow.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.temp.member.domain.Member;
import jakarta.persistence.EntityManager;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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

    @Test
    @DisplayName("executor가 target을 팔로우하면 true를 반환한다")
    void checkExecutorFollowTargetTrue() throws Exception {
        // given
        Member executor = createMember();
        Member target = createMember();

        Follow follow = Follow.builder()
            .from(executor)
            .to(target)
            .status(FollowStatus.SUCCESS)
            .build();
        em.persist(follow);

        // when
        boolean result = followRepository.checkExecutorFollowsTarget(executor.getId(), target.getId());

        // then
        assertThat(result).isTrue();
    }

    @ParameterizedTest
    @DisplayName("executor가 target에 대해 팔로우가 SUCCESS 이외의 상태라면 false를 반환한다")
    @ValueSource(strings = {"PENDING", "REJECTED", "CANCELED"})
    void checkExecutorFollowTargetFalse1(String statusStr) throws Exception {
        // given
        Member executor = createMember();
        Member target = createMember();

        Follow follow = Follow.builder()
            .from(executor)
            .to(target)
            .status(FollowStatus.valueOf(statusStr))
            .build();
        em.persist(follow);

        // when
        boolean result = followRepository.checkExecutorFollowsTarget(executor.getId(), target.getId());

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("executor가 target을 팔로우하고 있지 않으면 false를 반환한다")
    void checkExecutorFollowTargetFalse2() throws Exception {
        // given
        Member executor = createMember();
        Member target = createMember();
        Member anotherMember = createMember();

        Follow follow = Follow.builder()
            .from(executor)
            .to(anotherMember)
            .status(FollowStatus.SUCCESS)
            .build();
        em.persist(follow);

        // when
        boolean result = followRepository.checkExecutorFollowsTarget(executor.getId(), target.getId());

        // then
        assertThat(result).isFalse();
    }

    private Member createMember() {
        Member fromMember = Member.builder().build();
        em.persist(fromMember);
        return fromMember;
    }
}