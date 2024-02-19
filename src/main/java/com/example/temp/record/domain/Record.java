package com.example.temp.record.domain;

import com.example.temp.common.entity.BaseTimeEntity;
import com.example.temp.member.domain.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "records")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Record extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id")
    private Long id;

    @Column(name = "writer_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Member writer;

    private LocalDate recordDate;

    @Builder
    private Record(Member writer, LocalDate recordDate) {
        this.writer = writer;
        this.recordDate = recordDate;
    }
}
