package com.example.temp.common.dto;

import com.example.temp.auth.domain.Role;
import com.example.temp.member.domain.Member;
import lombok.Builder;

@Builder
public record UserContext(
    Long id,
    Role role) {

    public static UserContext fromMember(Member fromMember) {
        return new UserContext(fromMember.getId(), Role.NORMAL);
    }

    public boolean isNormal() {
        return role == Role.NORMAL;
    }
}
