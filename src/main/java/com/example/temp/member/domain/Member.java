package com.example.temp.member.domain;

import com.example.temp.common.entity.Email;
import com.example.temp.exception.ApiException;
import com.example.temp.exception.ErrorCode;
import com.example.temp.follow.domain.FollowStatus;
import com.example.temp.member.infrastructure.nickname.Nickname;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
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

    @Embedded
    @Column(nullable = false)
    private Email email;

    @Column(nullable = false)
    private String profileUrl;

    @Embedded
    @Column(nullable = false, unique = true)
    private Nickname nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FollowStrategy followStrategy;

    private boolean publicAccount;

    @Builder
    private Member(Email email, boolean registered, String profileUrl, Nickname nickname,
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

    public static Member createInitStatus(Email email, String profileUrl, Nickname nickname) {
        return Member.builder()
            .registered(false)
            .publicAccount(false)
            .followStrategy(FollowStrategy.LAZY)
            .email(email)
            .profileUrl(profileUrl)
            .nickname(nickname)
            .build();
    }

    public void init(Nickname nickname, String profileUrl) {
        if (registered) {
            throw new ApiException(ErrorCode.MEMBER_ALREADY_REGISTER);
        }
        this.registered = true;
        this.nickname = nickname;
        this.profileUrl = profileUrl;
    }

    public String getNicknameStr() {
        return nickname.getNickname();
    }

    public String getEmailStr() {
        return email.getEmail();
    }
}

