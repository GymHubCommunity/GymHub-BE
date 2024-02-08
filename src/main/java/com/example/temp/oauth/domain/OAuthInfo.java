package com.example.temp.oauth.domain;

import com.example.temp.member.domain.Member;
import com.example.temp.oauth.OAuthProviderType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "oauth_infos")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class OAuthInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "oauth_info_id")
    private Long id;

    @Column(nullable = false)
    private String idUsingResourceServer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OAuthProviderType type;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    private OAuthInfo(String idUsingResourceServer, OAuthProviderType type, Member member) {
        this.idUsingResourceServer = idUsingResourceServer;
        this.type = type;
        this.member = member;
    }

    public static OAuthInfo of(String idUsingResourceServer, OAuthProviderType oAuthProviderType, Member member) {
        return OAuthInfo.builder()
            .idUsingResourceServer(idUsingResourceServer)
            .type(oAuthProviderType)
            .member(member)
            .build();
    }
}
