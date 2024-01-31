package com.example.temp.oauth.domain;

import com.example.temp.oauth.OAuthProviderType;
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
    private String idUsingResourceServer;

    @Column(nullable = false)
    private OAuthProviderType type;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    private OAuthMember(String idUsingResourceServer, OAuthProviderType type, Member member) {
        this.idUsingResourceServer = idUsingResourceServer;
        this.type = type;
        this.member = member;
    }

    public static OAuthMember of(String idUsingResourceServer, OAuthProviderType oAuthProviderType, Member member) {
        return OAuthMember.builder()
            .idUsingResourceServer(idUsingResourceServer)
            .type(oAuthProviderType)
            .member(member)
            .build();
    }
}
