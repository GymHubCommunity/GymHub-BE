package com.example.temp.record.domain;

import com.example.temp.member.domain.Member;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ExerciseRecordRepository extends JpaRepository<ExerciseRecord, Long> {

    @Query("SELECT er FROM ExerciseRecord er "
        + "WHERE er.isSnapshot = false "
        + "AND er.id = :targetId")
    Optional<ExerciseRecord> findById(@Param("targetId") Long targetId);

    @Query("SELECT er FROM ExerciseRecord er "
        + "WHERE er.isSnapshot = false "
        + "AND er.member = :member "
        + "AND :startDate <= er.recordDate "
        + "AND er.recordDate <= :lastDate")
    List<ExerciseRecord> findAllByMemberAndPeriod(@Param("member") Member member,
        @Param("startDate") LocalDate startDate, @Param("lastDate") LocalDate lastDate);


    @Query("SELECT er FROM ExerciseRecord er "
        + "WHERE er.isSnapshot = true "
        + "AND er.id = :targetId")
    Optional<ExerciseRecord> findSnapshotById(@Param("targetId") long targetId);

    @Query("SELECT er FROM ExerciseRecord er "
        + "WHERE er.isSnapshot = true "
        + "AND (:lastId IS NULL OR er.id < :lastId) "
        + "AND er.member = :member "
        + "ORDER BY er.id DESC")
    Slice<ExerciseRecord> findPrevSnapshotsByMember(Long lastId, Pageable pageable, @Param("member") Member member);
}
