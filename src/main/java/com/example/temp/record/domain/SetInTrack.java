package com.example.temp.record.domain;

import com.example.temp.common.exception.ApiException;
import com.example.temp.common.exception.ErrorCode;
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
public class SetInTrack {

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
    private SetInTrack(Track track, int order, int weight, int repeatCnt) {
        validate(order, weight, repeatCnt);
        this.track = track;
        this.order = order;
        this.weight = weight;
        this.repeatCnt = repeatCnt;
    }

    private void validate(int order, int weight, int repeatCnt) {
        if (order < 1) {
            throw new IllegalArgumentException("Track 내 Set의 순서는 0보다 커야 합니다.");
        }
        if (weight < 0) {
            throw new ApiException(ErrorCode.SET_WEIGHT_INVALID);
        }
        if (repeatCnt < 0) {
            throw new ApiException(ErrorCode.SET_REPEAT_CNT_INVALID);
        }
    }

    /**
     * 연관관계 편의 메서드
     */
    public void relate(Track track) {
        if (this.track != null) {
            this.track.getSetsInTrack().remove(this);
        }
        this.track = track;
        this.track.getSetsInTrack().add(this);
    }

}
