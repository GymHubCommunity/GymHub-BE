package com.example.temp.common.dto;

import com.example.temp.member.domain.Member;
import lombok.Builder;

@Builder
public record UserContext(Long id) {

    public static UserContext from(Member fromMember) {
        return new UserContext(fromMember.getId());
    }
}
