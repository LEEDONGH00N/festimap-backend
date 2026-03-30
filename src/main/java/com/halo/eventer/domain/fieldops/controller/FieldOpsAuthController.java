package com.halo.eventer.domain.fieldops.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.halo.eventer.domain.fieldops.FieldOpsSession;
import com.halo.eventer.domain.fieldops.dto.request.FieldOpsPasswordVerifyRequest;
import com.halo.eventer.domain.fieldops.dto.response.FieldOpsHomeResponse;
import com.halo.eventer.domain.fieldops.dto.response.FieldOpsStatusResponse;
import com.halo.eventer.domain.fieldops.dto.response.FieldOpsVerifyResponse;
import com.halo.eventer.domain.fieldops.service.FieldOpsAuthService;
import com.halo.eventer.domain.fieldops.service.FieldOpsSessionService;
import com.halo.eventer.global.security.fieldops.FieldOpsAuthenticationFilter;
import com.halo.eventer.global.security.fieldops.FieldOpsUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/field-ops")
@RequiredArgsConstructor
@Tag(name = "FieldOps Auth", description = "현장 요원용 인증 API")
public class FieldOpsAuthController {

    private final FieldOpsAuthService fieldOpsAuthService;
    private final FieldOpsSessionService fieldOpsSessionService;

    @GetMapping("/{token}/status")
    @Operation(summary = "링크 유효성 확인", description = "FieldOps 링크가 유효한지 확인합니다")
    public ResponseEntity<FieldOpsStatusResponse> checkStatus(@PathVariable String token) {
        try {
            FieldOpsSession session = fieldOpsAuthService.validateTokenAndCheckStatus(token);
            return ResponseEntity.ok(new FieldOpsStatusResponse(session));
        } catch (Exception e) {
            return ResponseEntity.ok(FieldOpsStatusResponse.invalid());
        }
    }

    @PostMapping("/{token}/verify")
    @Operation(summary = "비밀번호 검증", description = "비밀번호를 검증하고 인증 쿠키를 발급합니다")
    public ResponseEntity<FieldOpsVerifyResponse> verifyPassword(
            @PathVariable String token,
            @Valid @RequestBody FieldOpsPasswordVerifyRequest request,
            HttpServletResponse response) {
        FieldOpsSession session = fieldOpsAuthService.verifyPassword(token, request.getPassword());

        Cookie cookie = createSessionCookie(session);
        response.addCookie(cookie);

        return ResponseEntity.ok(FieldOpsVerifyResponse.success());
    }

    @GetMapping("/{token}/home")
    @Operation(summary = "홈 화면 데이터", description = "인증된 현장 요원의 홈 화면 데이터를 반환합니다")
    public ResponseEntity<FieldOpsHomeResponse> getHome(
            @PathVariable String token, @AuthenticationPrincipal FieldOpsUserDetails userDetails) {
        FieldOpsSession session = fieldOpsSessionService.getSessionByToken(token);
        return ResponseEntity.ok(new FieldOpsHomeResponse(session));
    }

    private Cookie createSessionCookie(FieldOpsSession session) {
        Cookie cookie = new Cookie(FieldOpsAuthenticationFilter.COOKIE_NAME, session.getToken());
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/api/v1/field-ops/");
        cookie.setMaxAge(session.getSessionTtlHours() * 3600);
        return cookie;
    }
}
