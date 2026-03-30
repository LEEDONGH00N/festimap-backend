package com.halo.eventer.domain.stamp.controller.v2;

import java.util.List;
import jakarta.validation.constraints.Min;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.halo.eventer.domain.stamp.dto.mission.response.MissionPrizeResDto;
import com.halo.eventer.domain.stamp.dto.stamp.request.StampTourSignUpTemplateResDto;
import com.halo.eventer.domain.stamp.dto.stamp.response.*;
import com.halo.eventer.domain.stamp.service.v2.StampTourTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/template/festivals/{festivalId}/stamp-tours")
@Slf4j
@Tag(name = "스탬프 투어 템플릿 (v2)", description = "스탬프 투어 템플릿 조회 API (v2)")
public class StampTourTemplateController {

    private final StampTourTemplateService templateService;

    @Operation(summary = "스탬프 투어 목록 조회", description = "축제의 스탬프 투어 목록을 조회합니다.")
    @GetMapping
    public List<StampTourSummaryResDto> getStampTourList(@PathVariable @Min(1) long festivalId) {
        return templateService.getStampTourList(festivalId);
    }

    @Operation(summary = "회원가입 템플릿 조회", description = "스탬프 투어의 회원가입 템플릿을 조회합니다.")
    @GetMapping("/{stampId}/signup")
    public StampTourSignUpTemplateResDto getSignUpSettings(
            @PathVariable @Min(1) long festivalId, @PathVariable @Min(1) long stampId) {
        return templateService.getSignupTemplate(festivalId, stampId);
    }

    @Operation(summary = "랜딩 페이지 조회", description = "스탬프 투어의 랜딩 페이지를 조회합니다.")
    @GetMapping("/{stampId}/landing")
    public StampTourLandingPageResDto getStampTourLandingPage(
            @PathVariable @Min(1) long festivalId, @PathVariable @Min(1) long stampId) {
        return templateService.getLandingPage(festivalId, stampId);
    }

    @Operation(summary = "참여 방법 템플릿 조회", description = "스탬프 투어의 참여 방법 템플릿을 조회합니다.")
    @GetMapping("/{stampId}/join-template")
    public StampTourJoinTemplateResDto getStampUserJoinTemplate(
            @PathVariable @Min(1) long festivalId, @PathVariable @Min(1) long stampId) {
        return templateService.getStampTourJoinMethod(festivalId, stampId);
    }

    @Operation(summary = "인증 방법 조회", description = "스탬프 투어의 인증 방법을 조회합니다.")
    @GetMapping("/{stampId}/auth-method")
    public StampTourAuthMethodResDto getStampTourAuthMethod(
            @PathVariable @Min(1) long festivalId, @PathVariable @Min(1) long stampId) {
        return templateService.getAuthMethod(festivalId, stampId);
    }

    @Operation(summary = "메인 페이지 조회", description = "스탬프 투어의 메인 페이지를 조회합니다.")
    @GetMapping("/{stampId}/main")
    public StampTourMainPageResDto getStampTourMainPage(
            @PathVariable @Min(1) long festivalId, @PathVariable @Min(1) long stampId) {
        return templateService.getMainPage(festivalId, stampId);
    }

    @Operation(summary = "알림 조회", description = "스탬프 투어의 알림을 조회합니다.")
    @GetMapping("/{stampId}/notice")
    public StampTourNotificationResDto getStampTourNotification(
            @PathVariable @Min(1) long festivalId, @PathVariable @Min(1) long stampId) {
        return templateService.getStampTourNotification(festivalId, stampId);
    }

    @Operation(summary = "참여 안내 조회", description = "스탬프 투어의 참여 안내를 조회합니다.")
    @GetMapping("/{stampId}/guide")
    public StampTourGuideResDto getStampTourParticipationGuide(
            @PathVariable @Min(1) long festivalId, @PathVariable @Min(1) long stampId) {
        return templateService.getParticipateGuide(festivalId, stampId);
    }

    @Operation(summary = "상품 목록 조회", description = "스탬프 투어의 상품 목록을 조회합니다.")
    @GetMapping("/{stampId}/prizes")
    public List<MissionPrizeResDto> getPrizeList(
            @PathVariable @Min(1) long festivalId, @PathVariable @Min(1) long stampId) {
        return templateService.getPrizes(festivalId, stampId);
    }

    @Operation(summary = "스탬프 투어 활성화 상태 조회", description = "스탬프 투어의 활성화 상태를 조회합니다.")
    @GetMapping("/{stampId}")
    public StampActiveResDto getStampActive(@PathVariable @Min(1) long festivalId, @PathVariable @Min(1) long stampId) {
        return templateService.getStampActive(festivalId, stampId);
    }

    @GetMapping("/{stampId}/prizes/exchange-image")
    public PrizeExchangeImgResDto getStampTourPrizeExchangeImg(
            @PathVariable long festivalId, @PathVariable @Min(1) long stampId) {
        return templateService.getStampTourPrizeExchangeImg(festivalId, stampId);
    }
}
