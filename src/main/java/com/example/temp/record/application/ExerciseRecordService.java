package com.example.temp.record.application;

import com.example.temp.common.dto.UserContext;
import com.example.temp.common.exception.ApiException;
import com.example.temp.common.exception.ErrorCode;
import com.example.temp.member.domain.Member;
import com.example.temp.member.domain.MemberRepository;
import com.example.temp.record.domain.ExerciseRecord;
import com.example.temp.record.domain.ExerciseRecordRepository;
import com.example.temp.record.dto.request.ExerciseRecordCreateRequest;
import com.example.temp.record.dto.request.ExerciseRecordUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ExerciseRecordService {

    private final ExerciseRecordRepository exerciseRecordRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public long create(UserContext userContext, ExerciseRecordCreateRequest request) {
        Member member = findMember(userContext);
        ExerciseRecord exerciseRecord = request.toEntityWith(member);
        exerciseRecordRepository.save(exerciseRecord);
        return exerciseRecord.getId();
    }

    /**
     * 기존 Track과 Set을 모두 지우고 새로운 데이터를 넣는 방식을 사용할 예정입니다. 추후 내부 로직을 리팩토링할 예정입니다.
     */
    @Transactional
    public void update(UserContext userContext, long targetId, ExerciseRecordUpdateRequest request) {
        Member member = findMember(userContext);
        ExerciseRecord exerciseRecord = exerciseRecordRepository.findById(targetId)
            .orElseThrow(() -> new ApiException(ErrorCode.RECORD_NOT_FOUND));
        if (!exerciseRecord.isOwnedBy(member)) {
            throw new ApiException(ErrorCode.AUTHORIZED_FAIL);
        }
        exerciseRecord.update(request.toEntityWith(member));
    }

    @Transactional
    public void delete(UserContext userContext, long targetId) {
        Member member = findMember(userContext);
        ExerciseRecord exerciseRecord = exerciseRecordRepository.findById(targetId)
            .orElseThrow(() -> new ApiException(ErrorCode.RECORD_NOT_FOUND));
        if (!exerciseRecord.isOwnedBy(member)) {
            throw new ApiException(ErrorCode.AUTHORIZED_FAIL);
        }
        exerciseRecordRepository.delete(exerciseRecord);
    }

    private Member findMember(UserContext userContext) {
        return memberRepository.findById(userContext.id())
            .orElseThrow(() -> new ApiException(ErrorCode.AUTHENTICATED_FAIL));
    }
}
