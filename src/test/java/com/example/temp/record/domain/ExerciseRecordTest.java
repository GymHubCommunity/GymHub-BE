package com.example.temp.record.domain;

import com.example.temp.common.entity.Email;
import com.example.temp.member.domain.FollowStrategy;
import com.example.temp.member.domain.Member;
import com.example.temp.member.domain.PrivacyPolicy;
import com.example.temp.member.domain.nickname.Nickname;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ExerciseRecordTest {

    @Test
    @DisplayName("해당 운동기록의 소유주가 일치하면 true를 반환한다.")
    void isOwnedBy() throws Exception {
        // given
        Member member = createMember("nick1");
        ExerciseRecord record = createExerciseRecord(member);

        // when & then
        Assertions.assertThat(record.isOwnedBy(member)).isTrue();
    }

    @Test
    @DisplayName("해당 운동기록의 소유주가 아니라면 false를 반환한다.")
    void isNotOwnedBy() throws Exception {
        // given
        Member member = createMember("nick1");
        ExerciseRecord record = createExerciseRecord(member);

        Member another = createMember("another");

        // when & then
        Assertions.assertThat(record.isOwnedBy(another)).isFalse();
    }


    private ExerciseRecord createExerciseRecord(Member member) {
        return ExerciseRecord.builder()
            .member(member)
            .build();
    }

    private Member createMember(String nickname) {
        return Member.builder()
            .email(Email.create("test@test.com"))
            .followStrategy(FollowStrategy.EAGER)
            .privacyPolicy(PrivacyPolicy.PUBLIC)
            .profileUrl("https://profileurl")
            .nickname(Nickname.create(nickname))
            .build();
    }
}