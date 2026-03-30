package com.halo.eventer.domain.member.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.halo.eventer.domain.member.Member;
import com.halo.eventer.domain.member.dto.AgencySignupRequest;
import com.halo.eventer.domain.member.dto.AgencySignupResponse;
import com.halo.eventer.domain.member.dto.LoginDto;
import com.halo.eventer.domain.member.dto.LoginIdCheckResponse;
import com.halo.eventer.domain.member.dto.TokenDto;
import com.halo.eventer.domain.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin/auth")
@RequiredArgsConstructor
@Validated
@Tag(name = "관리자 인증", description = "관리자(AGENCY) 로그인 및 회원가입 API")
public class AdminAuthController {

    private final MemberService memberService;

    @Operation(summary = "관리자 로그인", description = "아이디와 비밀번호로 로그인하여 JWT 토큰을 발급받습니다.")
    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@RequestBody LoginDto loginDto) {
        return ResponseEntity.ok(memberService.login(loginDto));
    }

    @Operation(summary = "관리자 회원가입", description = "기관 담당자(AGENCY) 계정을 생성합니다.")
    @PostMapping("/signup")
    public ResponseEntity<AgencySignupResponse> signup(@Valid @RequestBody AgencySignupRequest request) {
        Member member = memberService.signupAgency(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new AgencySignupResponse(member));
    }

    @Operation(summary = "아이디 중복 검사", description = "아이디 사용 가능 여부를 확인합니다.")
    @GetMapping("/check-login-id")
    public ResponseEntity<LoginIdCheckResponse> checkLoginId(
            @RequestParam @NotBlank(message = "아이디는 필수입니다") String loginId) {
        boolean exists = memberService.existsByLoginId(loginId);
        return ResponseEntity.ok(new LoginIdCheckResponse(!exists));
    }
}
