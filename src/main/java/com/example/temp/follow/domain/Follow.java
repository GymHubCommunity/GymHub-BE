package com.example.temp.follow.domain;

import com.example.temp.member.domain.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
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
@Table(name = "follows")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Follow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "follow_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_id")
    private Member from;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_id")
    private Member to;

    @Enumerated(value = EnumType.STRING)
    private FollowStatus status;

    @Builder
    private Follow(Member from, Member to, FollowStatus status) {
        this.from = from;
        this.to = to;
        this.status = status;
    }

    public boolean isActive() {
        return getStatus().isActive();
    }

    public Follow reactive(FollowStatus changedStatus) {
        if (!changedStatus.isActive()) {
            throw new IllegalArgumentException("해당 상태로는 변경할 수 없습니다.");
        }
        if (isActive()) {
            throw new IllegalArgumentException("이미 둘 사이에 관계가 존재합니다.");
        }
        this.status = changedStatus;
        return this;
    }

    public void unfollow() {
        if (!isActive()) {
            throw new IllegalArgumentException("이미 비활성화된 관계입니다.");
        }
        this.status = FollowStatus.CANCELED;
    }
}
