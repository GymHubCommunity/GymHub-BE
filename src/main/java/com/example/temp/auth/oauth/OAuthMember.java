package com.example.temp.auth.oauth;

import com.example.temp.member.domain.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "oauth_members")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class OAuthMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private Long idUsingResourceServer;

    @Column(nullable = false)
    private String profileUrl;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    private OAuthMember(String email, String nickname, Long idUsingResourceServer, String profileUrl, Member member) {
        this.email = email;
        this.nickname = nickname;
        this.idUsingResourceServer = idUsingResourceServer;
        this.profileUrl = profileUrl;
        this.member = member;
    }

    public static OAuthMember from(OAuthResponse response, Member member) {
        return OAuthMember.builder()
            .email(response.email())
            .nickname(response.nickname())
            .idUsingResourceServer(response.idUsingResourceServer())
            .profileUrl(response.profileUrl())
            .member(member)
            .build();
    }
}
