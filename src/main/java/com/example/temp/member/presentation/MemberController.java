package com.example.temp.member.presentation;

import com.example.temp.auth.dto.response.MemberInfo;
import com.example.temp.common.annotation.Login;
import com.example.temp.common.dto.UserContext;
import com.example.temp.member.application.MemberService;
import com.example.temp.member.domain.PrivacyPolicy;
import com.example.temp.member.dto.request.MemberRegisterRequest;
import com.example.temp.member.dto.request.MemberUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
    public ResponseEntity<MemberInfo> register(@Login UserContext userContext,
        @RequestBody @Validated MemberRegisterRequest memberRegisterRequest) {
        MemberInfo response = memberService.register(userContext, memberRegisterRequest);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{memberId}")
    public ResponseEntity<Void> withdraw(@Login UserContext userContext, @PathVariable long memberId) {
        memberService.withdraw(userContext, memberId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/account/privacy")
    public ResponseEntity<Void> togglePrivacy(
        @Login UserContext userContext,
        @RequestParam("policy") PrivacyPolicy privacyPolicy) {
        memberService.changePrivacy(userContext, privacyPolicy);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{memberId}")
    public ResponseEntity<MemberInfo> retrieveMemberInfo(@PathVariable long memberId) {
        MemberInfo memberInfo = memberService.retrieveMemberInfo(memberId);
        return ResponseEntity.ok(memberInfo);
    }

    @PutMapping("/me")
    public ResponseEntity<Void> updateMemberInfo(@Login UserContext userContext,
        @RequestBody MemberUpdateRequest request) {
        memberService.updateMemberInfo(userContext, request);
        return ResponseEntity.noContent().build();
    }
}
