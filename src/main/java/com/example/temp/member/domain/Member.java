package com.example.temp.member.domain;

import com.example.temp.exception.ApiException;
import com.example.temp.exception.ErrorCode;
import com.example.temp.follow.domain.FollowStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "members")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    /**
     * 사용자에게 profileUrl과 nickname을 인증받았다면 true, 그렇지 않다면 false를 갖습니다.
     */
    private boolean registered;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String profileUrl;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FollowStrategy followStrategy;

    private boolean publicAccount;

    @Builder
    private Member(String email, boolean registered, String profileUrl, String nickname,
        FollowStrategy followStrategy, boolean publicAccount) {
        this.email = email;
        this.registered = registered;
        this.profileUrl = profileUrl;
        this.nickname = nickname;
        this.followStrategy = followStrategy;
        this.publicAccount = publicAccount;
    }

    public FollowStatus getStatusBasedOnStrategy() {
        return followStrategy.getFollowStatus();
    }

    public static Member createInitStatus(String email, String profileUrl, String nickname) {
        return Member.builder()
            .registered(false)
            .publicAccount(false)
            .followStrategy(FollowStrategy.LAZY)
            .email(email)
            .profileUrl(profileUrl)
            .nickname(nickname)
            .build();
    }

    public void init(String nickname, String profileUrl) {
        if (registered) {
            throw new ApiException(ErrorCode.MEMBER_ALREADY_REGISTER);
        }
        this.registered = true;
        this.nickname = nickname;
        this.profileUrl = profileUrl;
    }
}
