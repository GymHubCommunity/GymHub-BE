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
import com.example.temp.record.dto.request.RecordCreateRequest;
import com.example.temp.record.dto.request.RecordCreateRequest.TrackCreateRequest;
import com.example.temp.record.dto.request.RecordCreateRequest.TrackCreateRequest.SetCreateRequest;
import jakarta.persistence.EntityManager;
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
class ExerciseExerciseRecordServiceTest {

    @Autowired
    ExerciseRecordService exerciseRecordService;

    @Autowired
    EntityManager em;

    Member loginMember;

    UserContext loginUserContext;

    UserContext noLoginUserContext;

    @BeforeEach
    void setUp() {
        loginMember = saveMember();
        loginUserContext = UserContext.fromMember(loginMember);
        noLoginUserContext = new UserContext(99999999L, Role.NORMAL);
    }

    @Test
    @DisplayName("운동 기록을 저장한다.")
    void create() throws Exception {
        // given
        RecordCreateRequest request = new RecordCreateRequest(Collections.emptyList());

        // when
        long createdId = exerciseRecordService.create(loginUserContext, request);

        // then
        ExerciseRecord exerciseRecord = em.find(ExerciseRecord.class, createdId);
        assertThat(exerciseRecord.getRecordDate()).isNotNull();
        assertThat(exerciseRecord.getWriter()).isEqualTo(loginMember);
    }

    @Test
    @DisplayName("인증되지 않은 사용자는 운동 기록을 할 수 없다.")
    void createFailNoAuthenticated() throws Exception {
        // given
        RecordCreateRequest request = new RecordCreateRequest(Collections.emptyList());

        // when & then
        assertThatThrownBy(() -> exerciseRecordService.create(noLoginUserContext, request))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.AUTHENTICATED_FAIL.getMessage());
    }

    @Test
    @DisplayName("운동기록을 저장하면 연관된 세트들이 저장된다.")
    void createRecordSuccess() throws Exception {
        // given
        SetCreateRequest set1CreateRequest = new SetCreateRequest(10, 3);
        SetCreateRequest set2CreateRequest = new SetCreateRequest(20, 5);
        TrackCreateRequest trackCreateRequest = new TrackCreateRequest("스쿼트",
            List.of(set1CreateRequest, set2CreateRequest));
        RecordCreateRequest request = new RecordCreateRequest(List.of(trackCreateRequest));

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
                tuple(1, 10, 3),
                tuple(2, 20, 5)
            );

        assertThat(em.find(SetInTrack.class, track.getSetsInTrack().get(0).getId())).isNotNull();
        assertThat(em.find(SetInTrack.class, track.getSetsInTrack().get(1).getId())).isNotNull();
    }


    private Member saveMember() {
        Member member = Member.builder()
            .email(Email.create("test@test.com"))
            .followStrategy(FollowStrategy.EAGER)
            .privacyPolicy(PrivacyPolicy.PUBLIC)
            .profileUrl("https://profileurl")
            .nickname(Nickname.create("nick"))
            .build();
        em.persist(member);
        return member;
    }
}