package com.example.temp.record.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.temp.common.dto.UserContext;
import com.example.temp.common.entity.Email;
import com.example.temp.member.domain.FollowStrategy;
import com.example.temp.member.domain.Member;
import com.example.temp.member.domain.PrivacyPolicy;
import com.example.temp.member.domain.nickname.Nickname;
import com.example.temp.record.domain.ExerciseRecord;
import com.example.temp.record.dto.request.RecordCreateRequest;
import com.example.temp.record.dto.request.RecordCreateRequest.TrackCreateRequest;
import com.example.temp.record.dto.request.RecordCreateRequest.TrackCreateRequest.SetCreateRequest;
import jakarta.persistence.EntityManager;
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
    RecordService recordService;

    @Autowired
    EntityManager em;

    Member loginMember;

    UserContext loginUserContext;

    @BeforeEach
    void setUp() {
        loginMember = saveMember();
        loginUserContext = UserContext.fromMember(loginMember);
    }

    @Test
    @DisplayName("기록을 생성한다.")
    void createRecordSuccess() throws Exception {
        // given
        SetCreateRequest set1CreateRequest = new SetCreateRequest(10, 3);
        SetCreateRequest set2CreateRequest = new SetCreateRequest(10, 3);
        TrackCreateRequest trackCreateRequest = new TrackCreateRequest(
            "스쿼트", List.of(set1CreateRequest, set2CreateRequest));
        RecordCreateRequest request = new RecordCreateRequest(List.of(trackCreateRequest));

        // when
        long createdId = recordService.create(loginUserContext, request);

        // then
        ExerciseRecord exerciseRecord = em.find(ExerciseRecord.class, createdId);
        assertThat(exerciseRecord.getRecordDate()).isNotNull();
        assertThat(exerciseRecord.getWriter()).isEqualTo(loginMember);
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