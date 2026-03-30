package com.halo.eventer.domain.member.controller;

import jakarta.validation.constraints.Min;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.halo.eventer.domain.member.dto.StampParticipateRequest;
import com.halo.eventer.domain.member.service.MemberStampTourService;
import com.halo.eventer.domain.stamp.dto.mission.response.MissionBoardResDto;
import com.halo.eventer.domain.stamp.dto.mission.response.MissionTemplateResDto;
import com.halo.eventer.domain.stamp.dto.stampUser.response.PrizeClaimQrResDto;
import com.halo.eventer.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v3/user/festivals/{festivalId}/stamp-tours")
@RequiredArgsConstructor
@Tag(name = "스탬프 투어 (Member)", description = "Member 기반 스탬프 투어 사용자 API")
public class MemberStampTourController {

    private final MemberStampTourService stampTourService;

    @Operation(summary = "스탬프 투어 참여", description = "스탬프 투어에 참여합니다")
    @PostMapping("/{stampId}/participate")
    public ResponseEntity<Void> participate(
            @PathVariable @Min(1) Long festivalId,
            @PathVariable @Min(1) Long stampId,
            @RequestBody StampParticipateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        stampTourService.participate(
                userDetails.getMemberId(),
                festivalId,
                stampId,
                request.getParticipantCountOrDefault(),
                request.getExtraText());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "미션 보드 조회", description = "사용자의 미션 보드를 조회합니다")
    @GetMapping("/{stampId}/missions")
    public ResponseEntity<MissionBoardResDto> getMissionBoard(
            @PathVariable @Min(1) Long festivalId,
            @PathVariable @Min(1) Long stampId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        MissionBoardResDto response = stampTourService.getMissionBoard(userDetails.getMemberId(), festivalId, stampId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "미션 상세 조회", description = "미션의 상세 정보를 조회합니다")
    @GetMapping("/{stampId}/missions/{missionId}")
    public ResponseEntity<MissionTemplateResDto> getMissionDetails(
            @PathVariable @Min(1) Long festivalId,
            @PathVariable @Min(1) Long stampId,
            @PathVariable @Min(1) Long missionId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        MissionTemplateResDto response =
                stampTourService.getMissionDetails(userDetails.getMemberId(), festivalId, stampId, missionId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "QR 미션 인증", description = "QR 코드로 미션을 인증합니다")
    @PatchMapping("/{stampId}/missions/{missionId}/verify")
    public ResponseEntity<Void> completeMissionByQr(
            @PathVariable @Min(1) Long festivalId,
            @PathVariable @Min(1) Long stampId,
            @PathVariable @Min(1) Long missionId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        stampTourService.completeMissionByQr(userDetails.getMemberId(), festivalId, stampId, missionId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "상품 수령 QR 조회", description = "상품 수령을 위한 QR 정보를 조회합니다")
    @GetMapping("/{stampId}/prizes/qr")
    public ResponseEntity<PrizeClaimQrResDto> getPrizeQrInfo(
            @PathVariable @Min(1) Long festivalId,
            @PathVariable @Min(1) Long stampId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        PrizeClaimQrResDto response = stampTourService.getPrizeQrInfo(userDetails.getMemberId(), festivalId, stampId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "투어 완료 확인", description = "투어 완료 조건을 확인하고 완료 처리합니다")
    @PostMapping("/{stampId}/finish")
    public ResponseEntity<Boolean> checkAndFinishTour(
            @PathVariable @Min(1) Long festivalId,
            @PathVariable @Min(1) Long stampId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        boolean finished = stampTourService.checkAndFinishTour(userDetails.getMemberId(), festivalId, stampId);
        return ResponseEntity.ok(finished);
    }

    @Operation(summary = "상품 수령", description = "상품 수령을 기록합니다")
    @PatchMapping("/{stampId}/prize")
    public ResponseEntity<Void> receivePrize(
            @PathVariable @Min(1) Long festivalId,
            @PathVariable @Min(1) Long stampId,
            @RequestParam String prizeName,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        stampTourService.receivePrize(userDetails.getMemberId(), festivalId, stampId, prizeName);
        return ResponseEntity.ok().build();
    }
}
