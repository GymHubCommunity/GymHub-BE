package com.example.temp.member.event;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class MemberDeletedEvent {

    private final long memberId;

    @Builder
    private MemberDeletedEvent(long memberId) {
        this.memberId = memberId;
    }

    public static MemberDeletedEvent create(long memberId) {
        return MemberDeletedEvent.builder()
            .memberId(memberId)
            .build();
    }
}
