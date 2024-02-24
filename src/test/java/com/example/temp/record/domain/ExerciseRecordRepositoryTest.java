package com.example.temp.record.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.temp.common.entity.Email;
import com.example.temp.member.domain.FollowStrategy;
import com.example.temp.member.domain.Member;
import com.example.temp.member.domain.PrivacyPolicy;
import com.example.temp.member.domain.nickname.Nickname;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class ExerciseRecordRepositoryTest {

    @Autowired
    ExerciseRecordRepository exerciseRecordRepository;

    @Autowired
    EntityManager em;

    @Test
    @DisplayName("특정 기한 내에 등록된 운동기록 목록을 조회한다.")
    void findAllByMemberAndPeriodSuccessInRange() throws Exception {
        // given
        Member member = saveMember("회원");
        saveExerciseRecord(member, LocalDate.of(2019, 12, 31), false);
        ExerciseRecord record1 = saveExerciseRecord(member, LocalDate.of(2020, 1, 1), false);
        ExerciseRecord record2 = saveExerciseRecord(member, LocalDate.of(2020, 1, 2), false);
        saveExerciseRecord(member, LocalDate.of(2020, 1, 3), false);

        em.flush();
        em.clear();

        // when
        List<ExerciseRecord> results = exerciseRecordRepository.findAllByMemberAndPeriod(member,
            LocalDate.of(2020, 1, 1),
            LocalDate.of(2020, 1, 2));

        // then
        assertThat(results).hasSize(2)
            .extracting(ExerciseRecord::getId)
            .containsExactlyInAnyOrder(record1.getId(), record2.getId());
    }

    @Test
    @DisplayName("특정 기한 내에 등록된 운동기록 목록을 조회할 때, 스냅샷은 가져오지 않는다.")
    void findAllByMemberAndPeriodNoSnapshot() throws Exception {
        // given
        Member member = saveMember("회원");
        ExerciseRecord record = saveExerciseRecord(member, LocalDate.of(2020, 1, 1), false);
        ExerciseRecord snapshot = saveExerciseRecord(member, LocalDate.of(2020, 1, 1), true);

        em.flush();
        em.clear();

        // when
        List<ExerciseRecord> results = exerciseRecordRepository.findAllByMemberAndPeriod(member,
            LocalDate.of(2020, 1, 1),
            LocalDate.of(2020, 1, 2));

        // then
        assertThat(results).hasSize(1)
            .extracting(ExerciseRecord::getId)
            .containsExactlyInAnyOrder(record.getId());
    }

    @Test
    @DisplayName("특정 기한 내에 등록된 운동기록들이 없으면 비어있는 리스트를 반환한다.")
    void findAllByMemberAndPeriodSuccessEmpty() throws Exception {
        // given
        Member member = saveMember("any");
        // when
        List<ExerciseRecord> results = exerciseRecordRepository.findAllByMemberAndPeriod(member,
            LocalDate.of(2020, 1, 1),
            LocalDate.of(2020, 1, 2));

        // then
        assertThat(results).isEmpty();
    }

    @Test
    @DisplayName("일치하는 회원의 운동 기록만을 가져온다.")
    void findAllByMemberAndPeriodSuccessOnSameMember() throws Exception {
        // given
        Member member = saveMember("회원");
        ExerciseRecord record = saveExerciseRecord(member, LocalDate.of(2019, 12, 31), false);

        Member another = saveMember("다른사람");
        saveExerciseRecord(another, LocalDate.of(2019, 12, 31), false);

        em.flush();
        em.clear();
        // when
        List<ExerciseRecord> results = exerciseRecordRepository.findAllByMemberAndPeriod(member,
            LocalDate.of(2019, 12, 31),
            LocalDate.of(2019, 12, 31));

        // then
        assertThat(results).hasSize(1)
            .extracting(ExerciseRecord::getId)
            .containsExactlyInAnyOrder(record.getId());
    }

    private Member saveMember(String nickname) {
        Member member = Member.builder()
            .email(Email.create("이메일"))
            .profileUrl("프로필")
            .nickname(Nickname.create(nickname))
            .followStrategy(FollowStrategy.EAGER)
            .privacyPolicy(PrivacyPolicy.PUBLIC)
            .build();
        em.persist(member);
        return member;
    }

    private ExerciseRecord saveExerciseRecord(Member member, LocalDate recordDate, boolean isSnapshot) {
        ExerciseRecord exerciseRecord = ExerciseRecord.builder()
            .member(member)
            .tracks(List.of(Mockito.mock(Track.class)))
            .recordDate(recordDate)
            .isSnapshot(isSnapshot)
            .build();
        em.persist(exerciseRecord);
        return exerciseRecord;
    }
}