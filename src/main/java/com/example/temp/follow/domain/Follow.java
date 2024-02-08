package com.example.temp.follow.domain;

import static com.example.temp.common.exception.ErrorCode.FOLLOW_ALREADY_RELATED;
import static com.example.temp.common.exception.ErrorCode.FOLLOW_INACTIVE;
import static com.example.temp.common.exception.ErrorCode.FOLLOW_NOT_PENDING;
import static com.example.temp.common.exception.ErrorCode.FOLLOW_STATUS_CHANGE_NOT_ALLOWED;

import com.example.temp.common.exception.ApiException;
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
            throw new ApiException(FOLLOW_STATUS_CHANGE_NOT_ALLOWED);
        }
        if (isActive()) {
            throw new ApiException(FOLLOW_ALREADY_RELATED);
        }
        this.status = changedStatus;
        return this;
    }

    public void accept() {
        if (this.getStatus() != FollowStatus.PENDING) {
            throw new ApiException(FOLLOW_NOT_PENDING);
        }
        changeStatus(FollowStatus.APPROVED);
    }

    public void unfollow() {
        changeStatus(FollowStatus.CANCELED);
    }

    public void reject() {
        changeStatus(FollowStatus.REJECTED);
    }

    private void changeStatus(FollowStatus status) {
        if (!isActive()) {
            throw new ApiException(FOLLOW_INACTIVE);
        }
        this.status = status;
    }

}
