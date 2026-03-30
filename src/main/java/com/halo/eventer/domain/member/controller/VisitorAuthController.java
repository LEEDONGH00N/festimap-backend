package com.halo.eventer.domain.member.controller;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.halo.eventer.domain.member.dto.SocialLoginResult;
import com.halo.eventer.domain.member.dto.SocialTokenLoginRequest;
import com.halo.eventer.domain.member.dto.TokenDto;
import com.halo.eventer.domain.member.dto.VisitorSignupRequest;
import com.halo.eventer.domain.member.service.VisitorAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Visitor 인증", description = "VISITOR 회원 인증 API")
public class VisitorAuthController {

    private final VisitorAuthService visitorAuthService;

    @PostMapping("/social-login")
    @Operation(summary = "소셜 로그인", description = "프론트에서 받은 소셜 accessToken으로 로그인. 기존 회원이면 JWT 발급, 신규면 isMember=false 응답")
    public ResponseEntity<SocialLoginResult> socialLogin(@Valid @RequestBody SocialTokenLoginRequest request) {
        SocialLoginResult result = visitorAuthService.socialLogin(request.getProvider(), request.getAccessToken());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/signup")
    @Operation(summary = "VISITOR 회원가입", description = "소셜 로그인 후 회원가입 완료 (기본정보 + 마케팅동의 + 설문)")
    public ResponseEntity<TokenDto> signup(@RequestBody VisitorSignupRequest request) {
        TokenDto token = visitorAuthService.completeSignup(request);
        return ResponseEntity.ok(token);
    }
}
