package com.example.temp.record.domain;

import com.example.temp.member.domain.Member;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExerciseRecordRepository extends JpaRepository<ExerciseRecord, Long> {

    List<ExerciseRecord> findAllByMemberAndRecordDateBetween(Member member, LocalDate start, LocalDate end);
}
