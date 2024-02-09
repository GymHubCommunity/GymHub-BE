package com.example.temp.post.dto.response;

import com.example.temp.member.domain.Member;
import lombok.Builder;

@Builder
public record WriterInfo(
    Long id,
    String nickname,
    String email,
    String profileUrl) {

    public static WriterInfo from(Member member) {
        return WriterInfo.builder()
            .id(member.getId())
            .nickname(member.getNicknameValue())
            .email(member.getEmailValue())
            .profileUrl(member.getProfileUrl())
            .build();
    }
}
