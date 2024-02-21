package com.example.temp.record.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.temp.common.entity.Email;
import com.example.temp.common.exception.ApiException;
import com.example.temp.common.exception.ErrorCode;
import com.example.temp.member.domain.FollowStrategy;
import com.example.temp.member.domain.Member;
import com.example.temp.member.domain.PrivacyPolicy;
import com.example.temp.member.domain.nickname.Nickname;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ExerciseRecordTest {

    @Test
    @DisplayName("ExerciseRecord가 잘 생성되는지 확인한다.")
    void create() throws Exception {
        // given
        Member member = createMember("nick1");
        Track track = createTrack("머신1");

        // when
        ExerciseRecord exerciseRecord = ExerciseRecord.create(member, List.of(track));

        // then
        assertThat(exerciseRecord.getMember()).isEqualTo(member);
        assertThat(exerciseRecord.getTracks()).hasSize(1)
            .containsExactlyInAnyOrder(track);
    }

    @Test
    @DisplayName("List<Track>이 비어있는 상태의 ExerciseRecord를 만들 수 없다.")
    void createFailEmptyTrack() throws Exception {
        // given
        Member member = createMember("nick1");

        // when & then
        assertThatThrownBy(() -> ExerciseRecord.create(member, Collections.emptyList()))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.TRACK_CANT_EMPTY.getMessage());
    }

    @Test
    @DisplayName("List<Track>을 입력하지 않고 ExerciseRecord를 만들 수 없다.")
    void createFailTrackIsNull() throws Exception {
        // given
        Member member = createMember("nick1");

        // when & then
        assertThatThrownBy(() -> ExerciseRecord.create(member, null))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("해당 운동기록의 소유주가 일치하면 true를 반환한다.")
    void isOwnedBy() throws Exception {
        // given
        Member member = createMember("nick1");
        ExerciseRecord record = createExerciseRecord(member);

        // when & then
        assertThat(record.isOwnedBy(member)).isTrue();
    }

    @Test
    @DisplayName("해당 운동기록의 소유주가 아니라면 false를 반환한다.")
    void isNotOwnedBy() throws Exception {
        // given
        Member member = createMember("nick1");
        ExerciseRecord record = createExerciseRecord(member);

        Member another = createMember("another");

        // when & then
        assertThat(record.isOwnedBy(another)).isFalse();
    }

    private Track createTrack(String machineName) {
        return Track.builder()
            .machineName(machineName)
            .setsInTrack(List.of(createSetInTrack(1)))
            .build();
    }

    private SetInTrack createSetInTrack(int order) {
        return SetInTrack.builder()
            .order(order)
            .weight(10)
            .repeatCnt(5)
            .build();
    }


    private ExerciseRecord createExerciseRecord(Member member) {
        return ExerciseRecord.builder()
            .member(member)
            .tracks(List.of(createTrack("머신1")))
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