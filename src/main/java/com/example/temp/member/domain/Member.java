package com.example.temp.member.domain;

import com.example.temp.common.entity.Email;
import com.example.temp.common.exception.ApiException;
import com.example.temp.common.exception.ErrorCode;
import com.example.temp.follow.domain.FollowStatus;
import com.example.temp.member.domain.nickname.Nickname;
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

    public static final String DEFAULT_PROFILE = "https://avatars.githubusercontent.com/u/87960006?v=4";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    /**
     * 회원가입 처리가 끝난 이후 true 값을 갖습니다.
     */
    private boolean registered;

    /**
     * 탈퇴한 회원의 경우 true 값을 갖습니다.
     */
    private boolean deleted;

    @Embedded
    private Nickname nickname;

    @Embedded
    private Email email;

    @Column(nullable = false)
    private String profileUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PrivacyPolicy privacyPolicy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FollowStrategy followStrategy;

    @Builder
    private Member(boolean registered, boolean deleted, Nickname nickname, Email email, String profileUrl,
        PrivacyPolicy privacyPolicy, FollowStrategy followStrategy) {
        this.registered = registered;
        this.deleted = deleted;
        this.nickname = nickname;
        this.email = email;
        this.profileUrl = profileUrl;
        this.privacyPolicy = privacyPolicy;
        this.followStrategy = followStrategy;
    }

    /**
     * 가입이 완료되지 않은 회원 엔티티를 생성합니다. 해당 회원은 현재 가입되지 않은 상태이고, 비공개 계정이며, LAZY 팔로우 전략을 갖습니다.
     *
     * @param email
     * @param profileUrl
     * @param nickname
     * @return 가입이 완료되지 않은 회원 엔티티를 반환합니다.
     */
    public static Member createInitStatus(Email email, String profileUrl, Nickname nickname) {
        return Member.builder()
            .registered(false)
            .deleted(false)
            .privacyPolicy(PrivacyPolicy.PRIVATE)
            .followStrategy(FollowStrategy.LAZY)
            .email(email)
            .profileUrl(profileUrl)
            .nickname(nickname)
            .build();
    }

    /**
     * nickname과 profileUrl을 입력받아 회원가입 처리를 완료합니다. 공개 계정이며, EAGER 팔로우 전략을 갖습니다. 만약 profileUrl을 입력받지 않았다면 DEFAULT_PROFILE로
     * 회원을 등록합니다.
     *
     * @param nickname
     * @param profileUrl nullable
     * @throws ApiException MEMBER_ALREADY_REGISTER: 이미 가입이 완료된 회원이 해당 메서드를 호출했을 때 발생합니다.
     */
    public void init(Nickname nickname, String profileUrl) {
        if (profileUrl == null) {
            profileUrl = DEFAULT_PROFILE;
        }
        if (registered) {
            throw new ApiException(ErrorCode.MEMBER_ALREADY_REGISTER);
        }
        this.registered = true;
        this.privacyPolicy = PrivacyPolicy.PUBLIC;
        this.followStrategy = FollowStrategy.EAGER;
        this.nickname = nickname;
        this.profileUrl = profileUrl;
    }

    /**
     * 해당 회원을 팔로우했을 때, 생성될 팔로우 엔티티의 상태를 반환합니다. FollowStrategy의 text 컬럼을 통해 자세한 전략을 확인할 수 있습니다.
     *
     * @return 팔로우 엔티티의 상태값을 반환합니다. (ex. APPROVED, PENDING)
     */
    public FollowStatus getStatusBasedOnStrategy() {
        return followStrategy.getFollowStatus();
    }

    public String getNicknameValue() {
        return nickname.getValue();
    }

    public String getEmailValue() {
        return email.getValue();
    }

    public void changePrivacy(PrivacyPolicy privacyPolicy) {
        this.privacyPolicy = privacyPolicy;
    }

    public boolean isPublicAccount() {
        return privacyPolicy == PrivacyPolicy.PUBLIC;
    }

    public void delete() {
        this.deleted = true;
    }

}
