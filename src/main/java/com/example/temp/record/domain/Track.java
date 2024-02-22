package com.example.temp.record.domain;

import com.example.temp.common.exception.ApiException;
import com.example.temp.common.exception.ErrorCode;
import com.example.temp.machine.domain.BodyPart;
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
import java.util.Set;
import java.util.stream.Collectors;
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

    @Column(nullable = false)
    private BodyPart majorBodyPart;

    @OneToMany(mappedBy = "track", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SetInTrack> setsInTrack = new ArrayList<>();

    @Builder
    public Track(ExerciseRecord exerciseRecord, String machineName, BodyPart majorBodyPart,
        List<SetInTrack> setsInTrack) {
        machineName = machineName.trim();
        validate(machineName, setsInTrack);
        this.exerciseRecord = exerciseRecord;
        this.machineName = machineName;
        this.setsInTrack = new ArrayList<>();
        this.majorBodyPart = majorBodyPart;
        setsInTrack.forEach(set -> set.relate(this));
    }

    private void validate(String machineName, List<SetInTrack> setsInTrack) {
        if (machineName.isBlank()) {
            throw new ApiException(ErrorCode.TRACK_MACHINE_NAME_INVALID);
        }
        if (setsInTrack.isEmpty()) {
            throw new ApiException(ErrorCode.SET_CANT_EMPTY);
        }
        validateSetOrder(setsInTrack);
    }

    /**
     * 트랙내 세트가 N개일 때, 트랙의 번호가 1부터 N까지 이루어졌는지 검증한다.
     */
    private void validateSetOrder(List<SetInTrack> setsInTrack) {
        Set<Integer> orders = setsInTrack.stream()
            .map(SetInTrack::getOrder)
            .collect(Collectors.toSet());
        if (setsInTrack.size() != orders.size()) {
            throw new IllegalArgumentException("트랙 내 세트들의 순서는 1부터 순차적으로 올라가야 합니다.");
        }
        for (int i = 1; i <= orders.size(); i++) {
            if (!orders.contains(i)) {
                throw new IllegalArgumentException("트랙 내 세트들의 순서는 1부터 순차적으로 올라가야 합니다.");
            }
        }
    }

    /**
     * Track 엔티티를 생성합니다. 추후 relate 메서드를 사용해 Record 객체를 연결해야 합니다.
     */
    public static Track createWithoutRecord(String machineName, BodyPart majorBodyPart, List<SetInTrack> setInTracks) {
        return Track.builder()
            .machineName(machineName)
            .majorBodyPart(majorBodyPart)
            .setsInTrack(setInTracks)
            .build();
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
