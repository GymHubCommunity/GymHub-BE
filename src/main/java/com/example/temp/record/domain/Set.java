package com.example.temp.record.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sets")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Set {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "set_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "track_id", nullable = false)
    private Track track;

    @Column(nullable = false, name = "set_order")
    private int order;

    @Column
    private int weight;

    @Column
    private int repeatCnt;

    @Builder
    private Set(Track track, int order, int weight, int repeatCnt) {
        this.track = track;
        this.order = order;
        this.weight = weight;
        this.repeatCnt = repeatCnt;
    }

    /**
     * 연관관계 편의 메서드
     */
    public void relate(Track track) {
        if (this.track != null) {
            this.track.getSets().remove(this);
        }
        this.track = track;
        this.track.getSets().add(this);
    }
}
