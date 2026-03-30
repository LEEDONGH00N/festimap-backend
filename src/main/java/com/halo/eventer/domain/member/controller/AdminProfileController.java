package com.halo.eventer.domain.member.controller;

import java.util.List;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.halo.eventer.domain.member.Member;
import com.halo.eventer.domain.member.NotificationRecipient;
import com.halo.eventer.domain.member.dto.AdminProfileResponse;
import com.halo.eventer.domain.member.dto.AdminProfileUpdateRequest;
import com.halo.eventer.domain.member.dto.CompanyInfoResponse;
import com.halo.eventer.domain.member.dto.CompanyInfoUpdateRequest;
import com.halo.eventer.domain.member.dto.NotificationRecipientRequest;
import com.halo.eventer.domain.member.dto.NotificationRecipientResponse;
import com.halo.eventer.domain.member.dto.PasswordChangeRequest;
import com.halo.eventer.domain.member.service.MemberService;
import com.halo.eventer.domain.member.service.NotificationRecipientService;
import com.halo.eventer.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin/profile")
@RequiredArgsConstructor
@Tag(name = "관리자 프로필", description = "관리자(AGENCY, SUPER_ADMIN) 프로필 관리 API")
public class AdminProfileController {

    private final MemberService memberService;
    private final NotificationRecipientService recipientService;

    @Operation(summary = "내 프로필 조회", description = "로그인한 관리자의 프로필 정보를 조회합니다.")
    @GetMapping("/me")
    public ResponseEntity<AdminProfileResponse> getMyProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Member member = memberService.getAdminProfile(userDetails.getMemberId());
        return ResponseEntity.ok(new AdminProfileResponse(member));
    }

    @Operation(summary = "프로필 수정", description = "관리자 프로필 정보(회사명, 담당자 정보 등)를 수정합니다.")
    @PutMapping("/me")
    public ResponseEntity<Void> updateProfile(
            @Valid @RequestBody AdminProfileUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        memberService.updateAdminProfile(userDetails.getMemberId(), request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "비밀번호 변경", description = "현재 비밀번호 확인 후 새 비밀번호로 변경합니다.")
    @PatchMapping("/password")
    public ResponseEntity<Void> changePassword(
            @Valid @RequestBody PasswordChangeRequest request, @AuthenticationPrincipal CustomUserDetails userDetails) {
        memberService.changePassword(userDetails.getMemberId(), request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "회사 정보 조회", description = "로그인한 관리자의 회사 정보를 조회합니다.")
    @GetMapping("/company")
    public ResponseEntity<CompanyInfoResponse> getCompanyInfo(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Member member = memberService.getCompanyInfo(userDetails.getMemberId());
        return ResponseEntity.ok(new CompanyInfoResponse(member));
    }

    @Operation(summary = "회사 정보 수정", description = "회사 정보(회사명, 회사 이메일, 회사 연락처)를 수정합니다.")
    @PutMapping("/company")
    public ResponseEntity<Void> updateCompanyInfo(
            @Valid @RequestBody CompanyInfoUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        memberService.updateCompanyInfo(userDetails.getMemberId(), request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "알림 수신자 목록 조회", description = "등록된 알림 수신자 목록을 조회합니다.")
    @GetMapping("/notification-recipients")
    public ResponseEntity<List<NotificationRecipientResponse>> getNotificationRecipients(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<NotificationRecipient> recipients = recipientService.getRecipients(userDetails.getMemberId());
        List<NotificationRecipientResponse> response =
                recipients.stream().map(NotificationRecipientResponse::new).toList();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "알림 수신자 추가", description = "새로운 알림 수신자를 추가합니다.")
    @PostMapping("/notification-recipients")
    public ResponseEntity<NotificationRecipientResponse> addNotificationRecipient(
            @Valid @RequestBody NotificationRecipientRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        NotificationRecipient recipient = recipientService.addRecipient(userDetails.getMemberId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new NotificationRecipientResponse(recipient));
    }

    @Operation(summary = "알림 수신자 수정", description = "알림 수신자 정보를 수정합니다.")
    @PutMapping("/notification-recipients/{recipientId}")
    public ResponseEntity<Void> updateNotificationRecipient(
            @PathVariable Long recipientId,
            @Valid @RequestBody NotificationRecipientRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        recipientService.updateRecipient(userDetails.getMemberId(), recipientId, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "알림 수신자 삭제", description = "알림 수신자를 삭제합니다.")
    @DeleteMapping("/notification-recipients/{recipientId}")
    public ResponseEntity<Void> deleteNotificationRecipient(
            @PathVariable Long recipientId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        recipientService.deleteRecipient(userDetails.getMemberId(), recipientId);
        return ResponseEntity.noContent().build();
    }
}
