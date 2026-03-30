package com.halo.eventer.domain.stamp.controller.v2;

import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

import org.springframework.web.bind.annotation.*;

import com.halo.eventer.domain.stamp.dto.mission.request.*;
import com.halo.eventer.domain.stamp.dto.mission.response.*;
import com.halo.eventer.domain.stamp.dto.stamp.request.PrizeExchangeImgReqDto;
import com.halo.eventer.domain.stamp.service.v2.StampMissionAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/admin/festivals/{festivalId}/stamp-tours/{stampId}/missions")
@Tag(name = "스탬프 미션 관리 (v2)", description = "관리자용 스탬프 투어 미션 관리 API (v2)")
public class StampMissionAdminController {

    private final StampMissionAdminService stampMissionAdminService;

    @Operation(summary = "미션 생성", description = "스탬프 투어에 새로운 미션을 생성합니다.")
    @PostMapping
    public void createMission(
            @PathVariable @Min(1) long festivalId,
            @PathVariable @Min(1) long stampId,
            @RequestBody @Valid MissionCreateReqDto request) {
        stampMissionAdminService.createMission(festivalId, stampId, request.getTitle(), request.getShowMission());
    }

    @Operation(summary = "미션 목록 조회", description = "스탬프 투어의 미션 목록을 조회합니다.")
    @GetMapping
    public MissionListResDto getMissionList(@PathVariable @Min(1) long festivalId, @PathVariable @Min(1) long stampId) {
        return stampMissionAdminService.getMissions(festivalId, stampId);
    }

    @Operation(summary = "미션 삭제", description = "미션을 삭제합니다.")
    @DeleteMapping("/{missionId}")
    public void deleteMission(
            @PathVariable @Min(1) long festivalId,
            @PathVariable @Min(1) long stampId,
            @PathVariable @Min(1) long missionId) {
        stampMissionAdminService.deleteMission(festivalId, stampId, missionId);
    }

    @PatchMapping("/show")
    public void showMissions(
            @PathVariable @Min(1) long festivalId,
            @PathVariable @Min(1) long stampId,
            @Valid @RequestBody MissionsAllShowReqDto request) {
        stampMissionAdminService.showMissionsTrue(festivalId, stampId, request);
    }

    @Operation(summary = "미션 표시 여부 토글", description = "미션의 표시 여부를 변경합니다.")
    @PatchMapping("/{missionId}/show")
    public void toggleMissionShowing(
            @PathVariable @Min(1) long festivalId,
            @PathVariable @Min(1) long stampId,
            @PathVariable @Min(1) long missionId,
            @RequestBody @Valid MissionShowReqDto request) {
        stampMissionAdminService.toggleMissionShowing(festivalId, stampId, missionId, request);
    }

    @GetMapping("/{missionId}/show")
    public MissionShowResDto showMission(
            @PathVariable @Min(1) long festivalId,
            @PathVariable @Min(1) long stampId,
            @PathVariable @Min(1) long missionId) {
        return stampMissionAdminService.showMission(festivalId, stampId, missionId);
    }

    @Operation(summary = "미션 기본 설정 조회", description = "미션 기본 설정을 조회합니다.")
    @GetMapping("/settings")
    public StampMissionBasicSettingsResDto getBasicSettings(
            @PathVariable @Min(1) long festivalId, @PathVariable @Min(1) long stampId) {
        return stampMissionAdminService.getBasicSettings(festivalId, stampId);
    }

    @Operation(summary = "미션 기본 설정 수정", description = "미션 기본 설정을 수정합니다.")
    @PutMapping("/settings")
    public void upsertBasicSettings(
            @PathVariable @Min(1) long festivalId,
            @PathVariable @Min(1) long stampId,
            @RequestBody @Valid MissionBasicSettingsReqDto request) {
        stampMissionAdminService.updateBasicSettings(festivalId, stampId, request);
    }

    @Operation(summary = "상품 목록 조회", description = "미션 완료 시 지급될 상품 목록을 조회합니다.")
    @GetMapping("/prizes")
    public List<MissionPrizeResDto> getPrizeList(
            @PathVariable @Min(1) long festivalId, @PathVariable @Min(1) long stampId) {
        return stampMissionAdminService.getPrizes(festivalId, stampId);
    }

    @Operation(summary = "상품 추가", description = "새로운 상품을 추가합니다.")
    @PostMapping("/prizes")
    public void addPrize(
            @PathVariable @Min(1) long festivalId,
            @PathVariable @Min(1) long stampId,
            @RequestBody @Valid MissionPrizeCreateReqDto request) {
        stampMissionAdminService.addPrize(festivalId, stampId, request);
    }

    @Operation(summary = "상품 수정", description = "상품 정보를 수정합니다.")
    @PutMapping("/prizes/{prizeId}")
    public void updatePrize(
            @PathVariable @Min(1) long festivalId,
            @PathVariable @Min(1) long stampId,
            @PathVariable @Min(1) long prizeId,
            @RequestBody @Valid MissionPrizeUpdateReqDto request) {
        stampMissionAdminService.updatePrize(festivalId, stampId, prizeId, request);
    }

    @Operation(summary = "상품 삭제", description = "상품을 삭제합니다.")
    @DeleteMapping("/prizes/{prizeId}")
    public void deletePrize(
            @PathVariable @Min(1) long festivalId,
            @PathVariable @Min(1) long stampId,
            @PathVariable @Min(1) long prizeId) {
        stampMissionAdminService.deletePrize(festivalId, stampId, prizeId);
    }

    // TODO : 경품 교환권 이미지 변경 시 기본 이미지 설정 여부 및 기본/커스텀 타입 설정
    @PatchMapping("/prizes/exchange-image")
    public void updateTicketImage(
            @PathVariable @Min(1) long festivalId,
            @PathVariable @Min(1) long stampId,
            @RequestBody PrizeExchangeImgReqDto request) {
        stampMissionAdminService.updatePrizeTicketImg(festivalId, stampId, request);
    }

    @Operation(summary = "미션 상세 정보 조회", description = "미션의 상세 정보 템플릿을 조회합니다.")
    @GetMapping("/{missionId}/details")
    public MissionDetailsTemplateResDto getMissionDetails(
            @PathVariable @Min(1) long festivalId,
            @PathVariable @Min(1) long stampId,
            @PathVariable @Min(1) long missionId) {
        return stampMissionAdminService.getMissionDetailsTemplate(festivalId, stampId, missionId);
    }

    @Operation(summary = "미션 상세 정보 수정", description = "미션의 상세 정보 템플릿을 수정합니다.")
    @PutMapping("/{missionId}/details")
    public void upsertTemplate(
            @PathVariable @Min(1) long festivalId,
            @PathVariable @Min(1) long stampId,
            @PathVariable @Min(1) long missionId,
            @RequestBody @Valid MissionDetailsTemplateReqDto request) {
        stampMissionAdminService.upsertMissionDetailsTemplate(festivalId, stampId, missionId, request);
    }

    @Operation(summary = "미션 완료 이미지 조회", description = "미션 완료 시 표시될 이미지를 조회합니다.")
    @GetMapping("/{missionId}/clear-img")
    public StampMissionClearImageResDto getMissionClearImg(
            @PathVariable @Min(1) long festivalId,
            @PathVariable @Min(1) long stampId,
            @PathVariable @Min(1) long missionId) {
        return stampMissionAdminService.getStampMissionCompleteImage(festivalId, stampId, missionId);
    }

    @Operation(summary = "미션 완료 이미지 수정", description = "미션 완료 시 표시될 이미지를 수정합니다.")
    @PatchMapping("/{missionId}/clear-img")
    public void updateMissionClearImg(
            @PathVariable @Min(1) long festivalId,
            @PathVariable @Min(1) long stampId,
            @PathVariable @Min(1) long missionId,
            @RequestBody @Valid StampMissionClearImageReqDto request) {
        stampMissionAdminService.updateStampMissionCompleteImage(festivalId, stampId, missionId, request);
    }

    @Operation(summary = "미션 QR 데이터 조회", description = "미션 인증용 QR 코드 데이터를 조회합니다.")
    @GetMapping("/qr")
    public List<MissionQrDataResDto> getMissionsQrData(
            @PathVariable @Min(1) long festivalId, @PathVariable @Min(1) long stampId) {
        return stampMissionAdminService.getMissionsQrData(festivalId, stampId);
    }
}
