package com.example.temp.record.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

import com.example.temp.auth.domain.Role;
import com.example.temp.common.dto.UserContext;
import com.example.temp.common.entity.Email;
import com.example.temp.common.exception.ApiException;
import com.example.temp.common.exception.ErrorCode;
import com.example.temp.member.domain.FollowStrategy;
import com.example.temp.member.domain.Member;
import com.example.temp.member.domain.PrivacyPolicy;
import com.example.temp.member.domain.nickname.Nickname;
import com.example.temp.record.domain.ExerciseRecord;
import com.example.temp.record.domain.SetInTrack;
import com.example.temp.record.domain.Track;
import com.example.temp.record.dto.request.ExerciseRecordCreateRequest;
import com.example.temp.record.dto.request.ExerciseRecordCreateRequest.TrackCreateRequest;
import com.example.temp.record.dto.request.ExerciseRecordCreateRequest.TrackCreateRequest.SetInTrackCreateRequest;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class ExerciseRecordServiceTest {

    @Autowired
    ExerciseRecordService exerciseRecordService;

    @Autowired
    EntityManager em;

    Member loginMember;

    UserContext loginUserContext;

    UserContext noLoginUserContext;

    @BeforeEach
    void setUp() {
        loginMember = saveMember("loginMember");
        loginUserContext = UserContext.fromMember(loginMember);
        noLoginUserContext = new UserContext(99999999L, Role.NORMAL);
    }

    @Test
    @DisplayName("운동 기록을 저장한다.")
    void create() throws Exception {
        // given
        ExerciseRecordCreateRequest request = new ExerciseRecordCreateRequest(Collections.emptyList());

        // when
        long createdId = exerciseRecordService.create(loginUserContext, request);

        // then
        ExerciseRecord exerciseRecord = em.find(ExerciseRecord.class, createdId);
        assertThat(exerciseRecord.getRecordDate()).isNotNull();
        assertThat(exerciseRecord.getMember()).isEqualTo(loginMember);
    }

    @Test
    @DisplayName("인증되지 않은 사용자는 운동 기록을 할 수 없다.")
    void createFailNoAuthenticated() throws Exception {
        // given
        ExerciseRecordCreateRequest request = new ExerciseRecordCreateRequest(Collections.emptyList());

        // when & then
        assertThatThrownBy(() -> exerciseRecordService.create(noLoginUserContext, request))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.AUTHENTICATED_FAIL.getMessage());
    }

    @Test
    @DisplayName("운동기록을 저장하면 연관된 세트들이 저장된다.")
    void createRecordSuccess() throws Exception {
        // given
        SetInTrackCreateRequest set1CreateRequest = new SetInTrackCreateRequest(10, 3);
        SetInTrackCreateRequest set2CreateRequest = new SetInTrackCreateRequest(20, 5);
        TrackCreateRequest trackCreateRequest = new TrackCreateRequest("스쿼트",
            List.of(set1CreateRequest, set2CreateRequest));
        ExerciseRecordCreateRequest request = new ExerciseRecordCreateRequest(List.of(trackCreateRequest));

        // when
        long createdId = exerciseRecordService.create(loginUserContext, request);

        // then
        ExerciseRecord exerciseRecord = em.find(ExerciseRecord.class, createdId);
        List<Track> tracks = exerciseRecord.getTracks();
        assertThat(tracks).hasSize(1)
            .extracting("machineName")
            .contains("스쿼트");

        long trackId = tracks.get(0).getId();
        Track track = em.find(Track.class, trackId);
        assertThat(track.getSetsInTrack()).hasSize(2)
            .extracting("order", "weight", "repeatCnt")
            .containsExactlyInAnyOrder(
                tuple(1, set1CreateRequest.weight(), set1CreateRequest.repeatCnt()),
                tuple(2, set2CreateRequest.weight(), set2CreateRequest.repeatCnt())
            );

        assertThat(em.find(SetInTrack.class, track.getSetsInTrack().get(0).getId())).isNotNull();
        assertThat(em.find(SetInTrack.class, track.getSetsInTrack().get(1).getId())).isNotNull();
    }

    @Test
    @DisplayName("로그인한 사용자만 운동기록을 삭제할 수 있다.")
    void deleteFailNoAuthN() throws Exception {
        // given
        ExerciseRecord record = saveExerciseRecord(loginMember);

        // when & then
        assertThatThrownBy(() -> exerciseRecordService.delete(noLoginUserContext, record.getId()))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.AUTHENTICATED_FAIL.getMessage());
    }

    @Test
    @DisplayName("인가 권한이 없는 사용자는 운동기록을 삭제할 수 없다.")
    void dd() throws Exception {
        // given
        ExerciseRecord record = saveExerciseRecord(loginMember);
        Member anotherMember = saveMember("another1");

        // when & then
        assertThatThrownBy(() -> exerciseRecordService.delete(UserContext.fromMember(anotherMember), record.getId()))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.AUTHORIZED_FAIL.getMessage());
    }


    private ExerciseRecord saveExerciseRecord(Member member) {
        ExerciseRecord record = ExerciseRecord.builder()
            .member(member)
            .tracks(Collections.emptyList())
            .recordDate(LocalDate.now())
            .build();
        em.persist(record);
        return record;
    }


    private Member saveMember(String nickname) {
        Member member = Member.builder()
            .email(Email.create("test@test.com"))
            .followStrategy(FollowStrategy.EAGER)
            .privacyPolicy(PrivacyPolicy.PUBLIC)
            .profileUrl("https://profileurl")
            .nickname(Nickname.create(nickname))
            .build();
        em.persist(member);
        return member;
    }
}