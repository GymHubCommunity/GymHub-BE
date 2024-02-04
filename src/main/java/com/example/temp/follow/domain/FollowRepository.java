package com.example.temp.follow.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    Optional<Follow> findByFromIdAndToId(long fromId, Long toId);

    List<Follow> findAllByFromIdAndStatus(long fromId, FollowStatus status);

    List<Follow> findAllByToIdAndStatus(long toId, FollowStatus status);

    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM Follow f"
        + " WHERE f.from.id = :executorId AND f.to.id = :targetId and f.status = 'SUCCESS'")
    boolean checkExecutorFollowsTarget(long executorId, long targetId);

}
