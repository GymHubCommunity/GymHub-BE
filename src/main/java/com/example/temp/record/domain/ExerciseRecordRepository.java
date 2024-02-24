package com.example.temp.record.domain;

import com.example.temp.member.domain.Member;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ExerciseRecordRepository extends JpaRepository<ExerciseRecord, Long> {

    @Query("SELECT er FROM ExerciseRecord er"
        + " WHERE er.member = :member"
        + " AND er.isSnapshot = false"
        + " AND :startDate <= er.recordDate"
        + " AND er.recordDate <= :lastDate")
    List<ExerciseRecord> findAllByMemberAndPeriod(@Param("member") Member member,
        @Param("startDate") LocalDate startDate, @Param("lastDate") LocalDate lastDate);
}
