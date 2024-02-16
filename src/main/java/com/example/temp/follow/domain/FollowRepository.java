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

    /**
     * fromId와 status를 사용해 pageable.size만큼 팔로우 엔티티를 가져옵니다. 커서 기반 페이징을 지원하기 위한 메서드입니다.
     *
     * @param fromId
     * @param status
     * @param lastId   입력된 lastId보다 id가 큰 Follow만 조회가 가능합니다.
     * @param pageable page는 0이어야 합니다.(PageRequest.ofSize를 사용해 객체를 만들어주세요)
     */
    @Query("SELECT f FROM Follow f JOIN FETCH f.to "
        + "WHERE f.from.id = :fromId "
        + "AND f.status = :status "
        + "AND f.id > :lastId "
        + "ORDER BY f.id ASC")
    Slice<Follow> findAllByFromIdAndStatus(@Param("fromId") long fromId,
        @Param("status") FollowStatus status,
        @Param("lastId") long lastId,
        Pageable pageable);

    @Query("SELECT f FROM Follow f JOIN FETCH f.from WHERE f.to.id = :toId AND f.status = :status")
    List<Follow> findAllByToIdAndStatus(@Param("toId") long toId, @Param("status") FollowStatus status);

    /**
     * toId와 status를 사용해 pageable.size만큼 팔로우 엔티티를 가져옵니다. 커서 기반 페이징을 지원하기 위한 메서드입니다.
     *
     * @param toId
     * @param status
     * @param lastId   입력된 lastId보다 id가 큰 Follow만 조회가 가능합니다.
     * @param pageable page는 0이어야 합니다.(PageRequest.ofSize를 사용해 객체를 만들어주세요)
     */
    @Query("SELECT f FROM Follow f JOIN FETCH f.from "
        + "WHERE f.to.id = :toId "
        + "AND f.status = :status "
        + "AND f.id > :lastId "
        + "ORDER BY f.id ASC")
    Slice<Follow> findAllByToIdAndStatus(@Param("toId") long toId,
        @Param("status") FollowStatus status,
        @Param("lastId") long lastId,
        Pageable pageable);


    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM Follow f"
        + " WHERE f.from.id = :executorId AND f.to.id = :targetId AND f.status = 'APPROVED'")
    boolean checkExecutorFollowsTarget(@Param("executorId") long executorId, @Param("targetId") long targetId);

    @Query("SELECT f FROM Follow f WHERE f.from.id = :memberId OR f.to.id = :memberId")
    List<Follow> findAllRelatedByMemberId(@Param("memberId") long memberId);
}
