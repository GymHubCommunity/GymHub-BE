package com.example.temp.follow.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {

    Optional<Follow> findByFromIdAndToId(long fromId, Long toId);

    @Query("SELECT f FROM Follow f JOIN FETCH f.to WHERE f.from.id = :fromId AND f.status = :status")
    List<Follow> findAllByFromIdAndStatus(@Param("fromId") long fromId, @Param("status") FollowStatus status);

    @Query("SELECT f FROM Follow f JOIN FETCH f.to WHERE f.from.id = :fromId AND f.status = :status")
    Slice<Follow> findAllByFromIdAndStatus(@Param("fromId") long fromId,
        @Param("status") FollowStatus status, Pageable pageable);

    @Query("SELECT f FROM Follow f JOIN FETCH f.from WHERE f.to.id = :toId AND f.status = :status")
    List<Follow> findAllByToIdAndStatus(@Param("toId") long toId, @Param("status") FollowStatus status);

    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM Follow f"
        + " WHERE f.from.id = :executorId AND f.to.id = :targetId AND f.status = 'APPROVED'")
    boolean checkExecutorFollowsTarget(@Param("executorId") long executorId, @Param("targetId") long targetId);

    @Query("SELECT f FROM Follow f WHERE f.from.id = :memberId OR f.to.id = :memberId")
    List<Follow> findAllRelatedByMemberId(@Param("memberId") long memberId);
}
