package com.example.temp.record.domain;

import com.example.temp.common.entity.BaseTimeEntity;
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
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

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
    @JoinColumn(name = "writer_id", nullable = false)
    private Member writer;

    @OneToMany(mappedBy = "exerciseRecord", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Track> tracks = new ArrayList<>();

    @CreatedDate
    private LocalDate recordDate;

    @Builder
    private ExerciseRecord(Member writer, List<Track> tracks, LocalDate recordDate) {
        this.writer = writer;
        this.recordDate = recordDate;
        if (tracks != null) {
            this.tracks = new ArrayList<>();
            this.tracks.addAll(tracks);
        }
    }

    public static ExerciseRecord create(Member writer, List<Track> tracks) {
        ExerciseRecord exerciseRecord = ExerciseRecord.builder()
            .writer(writer)
            .build();
        tracks.forEach(track -> track.relate(exerciseRecord));
        return exerciseRecord;
    }
}
