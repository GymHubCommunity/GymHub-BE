package com.example.temp.record.domain;

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
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tracks")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Track {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "track_id")
    private Long id;

    @JoinColumn(name = "record_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private ExerciseRecord exerciseRecord;

    @Column(nullable = false)
    private String machineName;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Set> sets = new ArrayList<>();

    @Builder
    public Track(ExerciseRecord exerciseRecord, String machineName, List<Set> sets) {
        this.exerciseRecord = exerciseRecord;
        this.machineName = machineName;
        if (sets != null) {
            this.sets = new ArrayList<>();
            this.sets.addAll(sets);
        }
    }

    /**
     * Track 엔티티를 생성합니다. 추후 relate 메서드를 사용해 Record 객체를 연결해야 합니다.
     */
    public static Track createWithoutRecord(String machineName, List<Set> sets) {
        Track track = Track.builder()
            .machineName(machineName)
            .build();
        sets.forEach(set -> set.relate(track));
        return track;
    }

    /**
     * 연관관계 편의 메서드
     */
    public void relate(ExerciseRecord exerciseRecord) {
        if (this.exerciseRecord != null) {
            this.exerciseRecord.getTracks().remove(this);
        }
        this.exerciseRecord = exerciseRecord;
        this.exerciseRecord.getTracks().add(this);
    }
}
