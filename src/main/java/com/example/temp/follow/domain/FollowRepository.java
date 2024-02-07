package com.example.temp.follow.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    Optional<Follow> findByFromIdAndToId(long fromId, Long toId);

    @Query("SELECT f FROM Follow f join fetch f.to where f.from.id = :fromId and f.status = :status")
    List<Follow> findAllByFromIdAndStatus(long fromId, FollowStatus status);

    @Query("SELECT f FROM Follow f join fetch f.from where f.to.id = :toId and f.status = :status")
    List<Follow> findAllByToIdAndStatus(@Param("toId") long toId, @Param("status") FollowStatus status);

    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM Follow f"
        + " WHERE f.from.id = :executorId AND f.to.id = :targetId and f.status = 'APPROVED'")
    boolean checkExecutorFollowsTarget(@Param("executorId") long executorId, @Param("targetId") long targetId);

}
