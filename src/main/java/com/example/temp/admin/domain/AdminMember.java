package com.example.temp.admin.domain;

import com.example.temp.common.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "admin_members")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class AdminMember extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_member_id")
    private Long id;

    @Column(nullable = false, unique = true, length = 32)
    private String username;

    @Column(nullable = false, length = 64)
    private String password;

    @Column(nullable = false)
    private LocalDateTime lastLogin;

    @Builder
    private AdminMember(String username, String password, LocalDateTime lastLogin) {
        this.username = username;
        this.password = password;
        this.lastLogin = lastLogin;
    }

}
