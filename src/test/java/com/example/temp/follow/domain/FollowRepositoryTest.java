package com.example.temp.follow.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.temp.member.domain.FollowStrategy;
import com.example.temp.member.domain.Member;
import jakarta.persistence.EntityManager;
import java.util.List;
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

    long notExistId = 999_999_999L;

    int globalIdx = 0;

    @Test
    @DisplayName("fromId와 toId가 일치하는 Follow를 찾는다.")
    void findByFromIdAndToIdSuccess() throws Exception {
        // given
        Member fromMember = saveMember();
        Member toMember = saveMember();

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
        Member fromMember = saveMember();
        Member toMember = saveMember();

        Follow follow = Follow.builder()
            .from(fromMember)
            .to(toMember)
            .build();
        em.persist(follow);

        // when
        Optional<Follow> resultOpt = followRepository.findByFromIdAndToId(fromMember.getId(), notExistId);

        // then
        assertThat(resultOpt).isEmpty();
    }

    @Test
    @DisplayName("executor가 target을 팔로우하면 true를 반환한다")
    void checkExecutorFollowTargetTrue() throws Exception {
        // given
        Member executor = saveMember();
        Member target = saveMember();

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
        Member executor = saveMember();
        Member target = saveMember();

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
        Member executor = saveMember();
        Member target = saveMember();
        Member anotherMember = saveMember();

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

    @Test
    @DisplayName("fromId와 status가 일치하는 Follow의 목록을 조회한다")
    void findAllByFromIdAndStatus() throws Exception {
        // given
        Member fromMember = saveMember();
        Member toMember1 = saveMember();
        Member toMember2 = saveMember();
        FollowStatus targetStatus = FollowStatus.PENDING;
        Follow follow1 = saveFollow(fromMember, toMember1, targetStatus);
        Follow follow2 = saveFollow(fromMember, toMember2, targetStatus);

        // when
        List<Follow> result = followRepository.findAllByFromIdAndStatus(fromMember.getId(), targetStatus);

        // then
        assertThat(result).hasSize(2)
            .contains(follow1, follow2);
    }

    @Test
    @DisplayName("fromId와 status가 일치하는 Follow가 없을 때 비어있는 리스트를 반환한다")
    void findAllByFromIdAndStatusEmptyResult() throws Exception {
        // when
        List<Follow> result = followRepository.findAllByFromIdAndStatus(notExistId, FollowStatus.SUCCESS);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("toId와 status가 일치하는 Follow의 목록을 조회한다")
    void findAllByToIdAndStatus() throws Exception {
        // given
        Member fromMember1 = saveMember();
        Member fromMember2 = saveMember();
        Member toMember = saveMember();
        FollowStatus targetStatus = FollowStatus.PENDING;
        Follow follow1 = saveFollow(fromMember1, toMember, targetStatus);
        Follow follow2 = saveFollow(fromMember2, toMember, targetStatus);

        // when
        List<Follow> result = followRepository.findAllByToIdAndStatus(toMember.getId(), targetStatus);

        // then
        assertThat(result).hasSize(2)
            .contains(follow1, follow2);
    }

    @Test
    @DisplayName("toId와 status가 일치하는 Follow가 없을 때 비어있는 리스트를 반환한다")
    void findAllByToIdAndStatusEmptyResult() throws Exception {
        // when
        List<Follow> result = followRepository.findAllByToIdAndStatus(notExistId, FollowStatus.SUCCESS);

        // then
        assertThat(result).isEmpty();
    }

    private Follow saveFollow(Member fromMember, Member toMember1, FollowStatus status) {
        Follow follow = Follow.builder()
            .from(fromMember)
            .to(toMember1)
            .status(status)
            .build();
        em.persist(follow);
        return follow;
    }

    private Member saveMember() {
        Member member = Member.builder()
            .email("이메일")
            .profileUrl("프로필")
            .nickname("nickname" + globalIdx++)
            .followStrategy(FollowStrategy.EAGER)
            .publicAccount(true)
            .build();
            em.persist(member);
        return member;
    }

}