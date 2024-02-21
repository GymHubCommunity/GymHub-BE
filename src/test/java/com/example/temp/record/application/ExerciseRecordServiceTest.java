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
import com.example.temp.record.dto.request.ExerciseRecordUpdateRequest;
import com.example.temp.record.dto.request.ExerciseRecordUpdateRequest.TrackUpdateRequest;
import com.example.temp.record.dto.request.ExerciseRecordUpdateRequest.TrackUpdateRequest.SetInTrackUpdateRequest;
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
        ExerciseRecordCreateRequest request = makeExerciseRecordCreateRequest("머신1", 10, 3);

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
    @DisplayName("운동기록을 삭제한다.")
    void delete() throws Exception {
        // given
        Member member = saveMember("nick1");
        ExerciseRecord record = saveExerciseRecord(member);

        // when
        exerciseRecordService.delete(UserContext.fromMember(member), record.getId());

        // then
        assertThat(em.find(ExerciseRecord.class, record.getId())).isNull();
    }

    /**
     * ExerciseRecord, Track, SetInTrack 엔티티들이 DB에 저장되었는지 확인합니다. delete 명령 이후, 해당 엔티티들이 DB에서 삭제되었는지 확인합니다.
     */
    @Test
    @DisplayName("운동기록이 삭제되면 해당 기록에 포함된 Track, Set 엔티티를 함께 삭제한다.")
    void deleteCheckAllChildrenDelete() throws Exception {
        // given
        Member member = saveMember("nick1");
        Track trackBeforeSaved = createTrack("머신1", List.of(createSetInTrack(1), createSetInTrack(2)));
        ExerciseRecord savedExerciseRecord = saveExerciseRecord(member, trackBeforeSaved);
        Track savedTrack = savedExerciseRecord.getTracks().get(0);
        List<SetInTrack> savedSetsInTrack = savedTrack.getSetsInTrack();

        assertThat(em.find(ExerciseRecord.class, savedExerciseRecord.getId())).isNotNull();
        assertThat(em.find(Track.class, savedTrack.getId())).isNotNull();
        for (SetInTrack setInTrack : savedSetsInTrack) {
            assertThat(em.find(SetInTrack.class, setInTrack.getId())).isNotNull();
        }

        // when
        exerciseRecordService.delete(UserContext.fromMember(member), savedExerciseRecord.getId());

        // then
        assertThat(em.find(ExerciseRecord.class, savedExerciseRecord.getId())).isNull();
        assertThat(em.find(Track.class, savedTrack.getId())).isNull();
        for (SetInTrack setInTrack : savedSetsInTrack) {
            assertThat(em.find(SetInTrack.class, setInTrack.getId())).isNull();
        }
    }

    private Track createTrack(String machineName, List<SetInTrack> setsInTrack) {
        return Track.builder()
            .machineName(machineName)
            .setsInTrack(setsInTrack)
            .build();
    }

    private SetInTrack createSetInTrack(int order) {
        return SetInTrack.builder()
            .order(order)
            .weight(10)
            .repeatCnt(5)
            .build();
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
    void deleteFailNoAuthZ() throws Exception {
        // given
        ExerciseRecord record = saveExerciseRecord(loginMember);
        Member anotherMember = saveMember("another1");

        // when & then
        assertThatThrownBy(() -> exerciseRecordService.delete(UserContext.fromMember(anotherMember), record.getId()))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.AUTHORIZED_FAIL.getMessage());
    }

    @Test
    @DisplayName("운동기록 수정 요청을 보내면, 기존에 등록되어 있던 트랙들은 삭제되고 새로운 트랙들로 대체된다.")
    void updateSuccess() throws Exception {
        // given
        Track trackBeforeSaved = createTrack("머신1", List.of(createSetInTrack(1), createSetInTrack(2)));
        ExerciseRecord record = saveExerciseRecord(loginMember, trackBeforeSaved);
        ExerciseRecordUpdateRequest request = makeExerciseRecordUpdateRequest("변경한_머신", 100, 10);

        Long prevTrackId = record.getTracks().get(0).getId();
        assertThat(em.find(Track.class, prevTrackId)).isNotNull();

        // when
        exerciseRecordService.update(loginUserContext, record.getId(), request);
        em.flush();
        em.clear();

        // then
        ExerciseRecord updatedRecord = em.find(ExerciseRecord.class, record.getId());

        assertThat(updatedRecord.getRecordDate()).isEqualTo(record.getRecordDate());
        assertThat(updatedRecord.getTracks()).hasSize(request.tracks().size());
        assertThat(em.find(Track.class, prevTrackId)).isNull();
    }

    @Test
    @DisplayName("로그인한 사용자만 운동기록을 수정할 수 있다.")
    void updateFailNoAuthN() throws Exception {
        // given
        ExerciseRecord record = saveExerciseRecord(loginMember);
        ExerciseRecordUpdateRequest request = new ExerciseRecordUpdateRequest(Collections.emptyList());

        // when & then
        assertThatThrownBy(() -> exerciseRecordService.update(noLoginUserContext, record.getId(), request))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.AUTHENTICATED_FAIL.getMessage());
    }

    @Test
    @DisplayName("인가 권한이 없는 사용자는 운동기록을 수정할 수 없다.")
    void updateFailNoAuthZ() throws Exception {
        // given
        ExerciseRecord record = saveExerciseRecord(loginMember);
        Member anotherMember = saveMember("another1");
        ExerciseRecordUpdateRequest request = new ExerciseRecordUpdateRequest(Collections.emptyList());

        // when & then
        assertThatThrownBy(() ->
            exerciseRecordService.update(UserContext.fromMember(anotherMember), record.getId(), request))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.AUTHORIZED_FAIL.getMessage());
    }

    private ExerciseRecord saveExerciseRecord(Member member) {
        Track tracks = createTrack("머신1", List.of(createSetInTrack(1)));
        return saveExerciseRecord(member, tracks);
    }

    private ExerciseRecord saveExerciseRecord(Member member, Track track) {
        ExerciseRecord record = ExerciseRecord.builder()
            .member(member)
            .tracks(List.of(track))
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

    /**
     * machineName이라는 운동 기구를 사용하는 1세트짜리 트랙을 CREATE하는 요청을 만듭니다.
     */
    private ExerciseRecordCreateRequest makeExerciseRecordCreateRequest(String machineName, int weight, int repeatCnt) {
        SetInTrackCreateRequest setInTrackCreateRequest = new SetInTrackCreateRequest(weight, repeatCnt);
        List<TrackCreateRequest> trackCreateRequests = List.of(
            new TrackCreateRequest(machineName, List.of(setInTrackCreateRequest)));
        return new ExerciseRecordCreateRequest(trackCreateRequests);
    }


    /**
     * machineName이라는 운동 기구를 사용하는 1세트짜리 트랙을 UPDATE하는 요청을 만듭니다.
     */
    private ExerciseRecordUpdateRequest makeExerciseRecordUpdateRequest(String machineName, int weight, int repeatCnt) {
        SetInTrackUpdateRequest setInTrackUpdateRequest = new SetInTrackUpdateRequest(weight, repeatCnt);
        List<TrackUpdateRequest> trackUpdateRequests = List.of(
            new TrackUpdateRequest(machineName, List.of(setInTrackUpdateRequest)));
        return new ExerciseRecordUpdateRequest(trackUpdateRequests);
    }


}