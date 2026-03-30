package com.halo.eventer.domain.stamp.controller.v2;

import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

import org.springframework.web.bind.annotation.*;

import com.halo.eventer.domain.stamp.dto.stamp.request.*;
import com.halo.eventer.domain.stamp.dto.stamp.response.*;
import com.halo.eventer.domain.stamp.service.v2.StampTourAdminService;
import com.halo.eventer.global.common.dto.OrderUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/admin/festivals/{festivalId}/stamp-tours")
@Tag(name = "스탬프 투어 관리 (v2)", description = "관리자용 스탬프 투어 관리 API (v2)")
public class StampTourAdminController {

    private final StampTourAdminService stampTourAdminService;

    @Operation(summary = "스탬프 투어 생성", description = "새로운 스탬프 투어를 생성합니다.")
    @PostMapping
    public void createStampTour(
            @PathVariable @Min(1) long festivalId, @RequestBody @Valid StampTourCreateReqDto request) {
        stampTourAdminService.createStampTourByFestival(festivalId, request);
    }

    @Operation(summary = "스탬프 투어 목록 조회", description = "축제의 스탬프 투어 목록을 조회합니다.")
    @GetMapping
    public List<StampTourSummaryResDto> getStampToursList(@PathVariable @Min(1) long festivalId) {
        return stampTourAdminService.getStampTourListByFestival(festivalId);
    }

    @Operation(summary = "참여 인증 방식 조회", description = "스탬프 투어의 참여 인증 방식을 조회합니다.")
    @GetMapping("/{stampId}/settings/user-info")
    public StampTourJoinVerificationResDto getStampTourVerificationMethod(
            @PathVariable @Min(1) long festivalId, @PathVariable @Min(1) long stampId) {
        return stampTourAdminService.getJoinVerification(festivalId, stampId);
    }

    @Operation(summary = "참여 인증 방식 수정", description = "스탬프 투어의 참여 인증 방식을 수정합니다.")
    @PatchMapping("/{stampId}/settings/user-info")
    public void updateStampTourVerificationMethod(
            @PathVariable @Min(1) long festivalId,
            @PathVariable @Min(1) long stampId,
            @Valid @RequestBody StampTourJoinConditionReqDto request) {
        stampTourAdminService.updateJoinCondition(festivalId, stampId, request);
    }

    @Operation(summary = "스탬프 투어 삭제", description = "스탬프 투어를 삭제합니다.")
    @DeleteMapping("/{stampId}")
    public void deleteStampTour(@PathVariable @Min(1) long festivalId, @PathVariable @Min(1) long stampId) {
        stampTourAdminService.deleteStampTour(festivalId, stampId);
    }

    @Operation(summary = "스탬프 투어 표시 여부 토글", description = "스탬프 투어의 표시 여부를 변경합니다.")
    @PatchMapping("/{stampId}/showStamp")
    public void toggleStampTourShow(
            @PathVariable @Min(1) long festivalId,
            @PathVariable @Min(1) long stampId,
            @RequestBody @Valid StampTourShowToggleReqDto request) {
        stampTourAdminService.toggleShowStamp(festivalId, stampId, request.isShowStamp());
    }

    @Operation(summary = "기본 설정 조회", description = "스탬프 투어의 기본 설정을 조회합니다.")
    @GetMapping("/{stampId}/settings/basic")
    public StampTourSettingBasicResDto getSettingBasicForStampTour(
            @PathVariable long festivalId, @PathVariable long stampId) {
        return stampTourAdminService.getStampTourSettingBasicByFestival(festivalId, stampId);
    }

    @Operation(summary = "기본 설정 수정", description = "스탬프 투어의 기본 설정을 수정합니다.")
    @PatchMapping("/{stampId}/settings/basic")
    public void upsertBasicSettingsForStampTour(
            @PathVariable @Min(1) long festivalId,
            @PathVariable @Min(1) long stampId,
            @RequestBody @Valid StampTourBasicUpdateReqDto request) {
        stampTourAdminService.updateBasicSettings(festivalId, stampId, request);
    }

    @Operation(summary = "알림 설정 조회", description = "스탬프 투어의 알림 설정을 조회합니다.")
    @GetMapping("/{stampId}/settings/notice")
    public StampTourNotificationResDto getNotificationForStampTour(
            @PathVariable @Min(1) long festivalId, @PathVariable @Min(1) long stampId) {
        return stampTourAdminService.getStampTourNotification(festivalId, stampId);
    }

    @Operation(summary = "알림 설정 수정", description = "스탬프 투어의 알림 설정을 수정합니다.")
    @PutMapping("/{stampId}/settings/notice")
    public void upsertNotificationForStampTour(
            @PathVariable @Min(1) long festivalId,
            @PathVariable @Min(1) long stampId,
            @RequestBody @Valid NotificationContentReqDto request) {
        stampTourAdminService.updateStampTourNotification(
                festivalId, stampId, request.getCautionContent(), request.getPersonalInformationContent());
    }

    @Operation(summary = "랜딩 페이지 조회", description = "스탬프 투어의 랜딩 페이지 설정을 조회합니다.")
    @GetMapping("/{stampId}/settings/landing")
    public StampTourLandingPageResDto getLandingPageForStampTour(
            @PathVariable @Min(1) long festivalId, @PathVariable @Min(1) long stampId) {
        return stampTourAdminService.getLandingPageSettings(festivalId, stampId);
    }

    @Operation(summary = "랜딩 페이지 수정", description = "스탬프 투어의 랜딩 페이지 설정을 수정합니다.")
    @PutMapping("/{stampId}/settings/landing")
    public void upsertLandingPageForStampTour(
            @PathVariable @Min(1) long festivalId,
            @PathVariable @Min(1) long stampId,
            @RequestBody @Valid StampTourLandingPageReqDto request) {
        stampTourAdminService.updateLandingPage(festivalId, stampId, request);
    }

    @Operation(summary = "메인 페이지 조회", description = "스탬프 투어의 메인 페이지 설정을 조회합니다.")
    @GetMapping("/{stampId}/settings/main")
    public StampTourMainPageResDto getMainPageForStampTour(
            @PathVariable @Min(1) long festivalId, @PathVariable @Min(1) long stampId) {
        return stampTourAdminService.getMainPageSettings(festivalId, stampId);
    }

    @Operation(summary = "메인 페이지 수정", description = "스탬프 투어의 메인 페이지 설정을 수정합니다.")
    @PutMapping("/{stampId}/settings/main")
    public void upsertMainPageForStampTour(
            @PathVariable @Min(1) long festivalId,
            @PathVariable @Min(1) long stampId,
            @RequestBody @Valid StampTourMainPageReqDto request) {
        stampTourAdminService.updateMainPageSettings(festivalId, stampId, request);
    }

    @Operation(summary = "참여 안내 조회", description = "스탬프 투어의 참여 안내를 조회합니다.")
    @GetMapping("/{stampId}/settings/guides")
    public StampTourParticipateGuideResDto getParticipateIntroductionFromStampTour(
            @PathVariable @Min(1) long festivalId, @PathVariable @Min(1) long stampId) {
        return stampTourAdminService.getParticipateGuide(festivalId, stampId);
    }

    @Operation(summary = "참여 안내 표시 순서 수정", description = "참여 안내 페이지의 표시 순서를 수정합니다.")
    @PatchMapping("/{stampId}/settings/guides")
    public List<ParticipateGuidePageSummaryResDto> updateDisplayOrder(
            @PathVariable @Min(1) long festivalId,
            @PathVariable @Min(1) long stampId,
            @RequestBody List<OrderUpdateRequest> requests) {
        return stampTourAdminService.updateDisplayOrder(festivalId, stampId, requests);
    }

    @Operation(summary = "참여 안내 수정", description = "스탬프 투어의 참여 안내를 수정합니다.")
    @PutMapping("/{stampId}/settings/guides")
    public void upsertParticipateIntroductionFromStampTour(
            @PathVariable @Min(1) long festivalId,
            @PathVariable @Min(1) long stampId,
            @RequestBody @Valid StampTourParticipateGuideReqDto request) {
        stampTourAdminService.updateParticipateGuide(festivalId, stampId, request);
    }

    @Operation(summary = "참여 안내 페이지 삭제", description = "참여 안내 페이지를 삭제합니다.")
    @DeleteMapping("/{stampId}/settings/guides/pages/{pageId}")
    public void deleteParticipateIntroductionPageFromStampTour(
            @PathVariable @Min(1) long festivalId,
            @PathVariable @Min(1) long stampId,
            @PathVariable @Min(1) long pageId) {
        stampTourAdminService.deleteParticipateGuidePage(festivalId, stampId, pageId);
    }

    @Operation(summary = "참여 안내 페이지 생성", description = "새로운 참여 안내 페이지를 생성합니다.")
    @PostMapping("/{stampId}/settings/guides/pages")
    public void createParticipationGuidePage(
            @PathVariable @Min(1) long festivalId,
            @PathVariable @Min(1) long stampId,
            @RequestBody @Valid StampTourParticipateGuidePageReqDto request) {
        stampTourAdminService.createParticipateGuidePage(festivalId, stampId, request);
    }

    @Operation(summary = "참여 안내 페이지 상세 조회", description = "참여 안내 페이지의 상세 정보를 조회합니다.")
    @GetMapping("/{stampId}/settings/guides/pages/{pageId}")
    public ParticipateGuidePageDetailsResDto getParticipationGuidePage(
            @PathVariable @Min(1) long festivalId,
            @PathVariable @Min(1) long stampId,
            @PathVariable @Min(1) long pageId) {
        return stampTourAdminService.getParticipateGuidePageDetails(festivalId, stampId, pageId);
    }

    @Operation(summary = "참여 안내 페이지 수정", description = "참여 안내 페이지를 수정합니다.")
    @PutMapping("/{stampId}/settings/guides/pages/{pageId}")
    public void upsertParticipateGuidePageFromStampTour(
            @PathVariable @Min(1) long festivalId,
            @PathVariable @Min(1) long stampId,
            @PathVariable @Min(1) long pageId,
            @RequestBody @Valid StampTourParticipateGuidePageReqDto request) {
        stampTourAdminService.updateParticipateGuidePage(festivalId, stampId, pageId, request);
    }

    @Operation(summary = "스탬프 투어 활성화 상태 조회", description = "스탬프 투어의 활성화 상태를 조회합니다.")
    @GetMapping("/{stampId}")
    public StampActiveResDto getStampActive(@PathVariable @Min(1) long festivalId, @PathVariable @Min(1) long stampId) {
        return stampTourAdminService.getStampActive(festivalId, stampId);
    }
}
