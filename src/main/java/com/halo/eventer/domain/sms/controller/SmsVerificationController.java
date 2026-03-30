package com.halo.eventer.domain.sms.controller;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.halo.eventer.domain.sms.dto.SmsSendCodeRequest;
import com.halo.eventer.domain.sms.dto.SmsVerifyCodeRequest;
import com.halo.eventer.domain.sms.service.SmsVerificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth/sms")
@RequiredArgsConstructor
@Tag(name = "SMS 인증", description = "SMS 본인인증 API")
public class SmsVerificationController {

    private final SmsVerificationService smsVerificationService;

    @PostMapping("/send")
    @Operation(summary = "인증번호 발송", description = "입력된 전화번호로 6자리 인증번호를 발송합니다. 유효시간 3분.")
    public ResponseEntity<Void> sendCode(@Valid @RequestBody SmsSendCodeRequest request) {
        smsVerificationService.sendVerificationCode(request.getPhone());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/verify")
    @Operation(summary = "인증번호 확인", description = "발송된 인증번호를 검증합니다. 5회 오류 시 재발송 필요. role 지정 시 해당 역할의 전화번호 중복 검사도 수행.")
    public ResponseEntity<Void> verifyCode(@Valid @RequestBody SmsVerifyCodeRequest request) {
        smsVerificationService.verifyCode(request.getPhone(), request.getCode(), request.getRole());
        return ResponseEntity.ok().build();
    }
}
