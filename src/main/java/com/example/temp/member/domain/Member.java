package com.example.temp.member.domain;

import com.example.temp.oauth.OAuthResponse;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

    private String email;

    private String profileUrl;

    @Builder
    private Member(String email, String profileUrl) {
        this.email = email;
        this.profileUrl = profileUrl;
    }

    public static Member of(OAuthResponse oAuthResponse) {
        return Member.builder()
            .email(oAuthResponse.email())
            .profileUrl(oAuthResponse.profileUrl())
            .build();
    }
}
