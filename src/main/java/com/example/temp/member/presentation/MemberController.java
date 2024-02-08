package com.example.temp.member.presentation;

import com.example.temp.auth.dto.response.MemberInfo;
import com.example.temp.member.application.MemberService;
import com.example.temp.member.domain.PrivacyPolicy;
import com.example.temp.member.dto.request.MemberRegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/register")
    public ResponseEntity<MemberInfo> register(@RequestAttribute(name = "executor") long executorId,
        @RequestBody MemberRegisterRequest memberRegisterRequest) {
        MemberInfo response = memberService.register(executorId, memberRegisterRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/account/privacy")
    public ResponseEntity<Void> togglePrivacy(
        @RequestAttribute(name = "executor", required = false) long executorId,
        @RequestParam("policy") PrivacyPolicy privacyPolicy) {
        memberService.changePrivacy(1L, privacyPolicy);
        return ResponseEntity.noContent().build();
    }
}
