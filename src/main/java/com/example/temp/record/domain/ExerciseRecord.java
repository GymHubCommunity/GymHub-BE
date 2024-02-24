package com.example.temp.record.domain;

import com.example.temp.common.entity.BaseTimeEntity;
import com.example.temp.common.exception.ApiException;
import com.example.temp.common.exception.ErrorCode;
import com.example.temp.member.domain.Member;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "records")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ExerciseRecord extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @OneToMany(mappedBy = "exerciseRecord", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Track> tracks = new ArrayList<>();

    private LocalDate recordDate;

    private boolean isSnapshot;

    @Builder
    private ExerciseRecord(Member member, List<Track> tracks, LocalDate recordDate, boolean isSnapshot) {
        validate(tracks);
        this.member = member;
        this.recordDate = recordDate;
        this.tracks = new ArrayList<>();
        tracks.forEach(track -> track.relate(this));
        this.isSnapshot = isSnapshot;
    }

    private void validate(List<Track> tracks) {
        Objects.requireNonNull(tracks);
        if (tracks.isEmpty()) {
            throw new ApiException(ErrorCode.TRACK_CANT_EMPTY);
        }
    }

    public static ExerciseRecord create(Member member, List<Track> tracks) {
        return ExerciseRecord.builder()
            .member(member)
            .recordDate(LocalDate.now())
            .tracks(tracks)
            .isSnapshot(false)
            .build();
    }

    public ExerciseRecord createSnapshot(Member member) {
        List<Track> tracksCopy = this.tracks.stream()
            .map(Track::copy)
            .toList();
        return ExerciseRecord.builder()
            .member(member)
            .recordDate(this.recordDate)
            .tracks(tracksCopy)
            .isSnapshot(true)
            .build();
    }

    public boolean isOwnedBy(Member member) {
        Objects.requireNonNull(member);
        return Objects.equals(member, getMember());
    }

    /**
     * ExerciseRecord를 입력받아, 내부의 Track을 변경합니다. ConcurrentModificationException 으로 인해 updated의 tracks를 복사한 뒤 사용합니다.
     */
    public void update(ExerciseRecord updated) {
        this.tracks.clear();
        List<Track> copies = List.copyOf(updated.getTracks());
        copies.forEach(track -> track.relate(this));
    }
}
