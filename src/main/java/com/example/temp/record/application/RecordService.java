package com.example.temp.record.application;

import com.example.temp.common.dto.UserContext;
import com.example.temp.common.exception.ApiException;
import com.example.temp.common.exception.ErrorCode;
import com.example.temp.member.domain.Member;
import com.example.temp.member.domain.MemberRepository;
import com.example.temp.record.domain.ExerciseRecord;
import com.example.temp.record.domain.RecordRepository;
import com.example.temp.record.dto.request.RecordCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RecordService {

    private final RecordRepository recordRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public long create(UserContext userContext, RecordCreateRequest request) {
        Member member = findMember(userContext);
        ExerciseRecord exerciseRecord = request.toEntityWith(member);
        ExerciseRecord savedExerciseRecord = recordRepository.save(exerciseRecord);
        return savedExerciseRecord.getId();
    }

    private Member findMember(UserContext userContext) {
        return memberRepository.findById(userContext.id())
            .orElseThrow(() -> new ApiException(ErrorCode.AUTHENTICATED_FAIL));
    }
}
