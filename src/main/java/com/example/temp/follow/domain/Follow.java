package com.example.temp.follow.domain;

import static com.example.temp.follow.domain.FollowStatus.PENDING;
import static com.example.temp.follow.domain.FollowStatus.SUCCESS;

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
import lombok.Setter;

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
    @Setter
    private FollowStatus status;

    @Builder
    private Follow(Member from, Member to, FollowStatus status) {
        this.from = from;
        this.to = to;
        this.status = status;
    }

    public boolean isValid() {
        return getStatus().isValid();
    }
}
