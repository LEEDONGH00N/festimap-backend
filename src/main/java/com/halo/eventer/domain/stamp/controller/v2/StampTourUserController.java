package com.halo.eventer.domain.stamp.controller.v2;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.halo.eventer.domain.member.dto.TokenDto;
import com.halo.eventer.domain.stamp.dto.mission.response.MissionBoardResDto;
import com.halo.eventer.domain.stamp.dto.mission.response.MissionTemplateResDto;
import com.halo.eventer.domain.stamp.dto.stampUser.request.MissionQrVerifyReqDto;
import com.halo.eventer.domain.stamp.dto.stampUser.request.StampUserLoginDto;
import com.halo.eventer.domain.stamp.dto.stampUser.request.StampUserSignupReqDto;
import com.halo.eventer.domain.stamp.dto.stampUser.response.PrizeClaimQrResDto;
import com.halo.eventer.domain.stamp.service.v2.StampTourUserService;
import com.halo.eventer.global.security.CustomStampUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/user/festivals/{festivalId}/stamp-tours")
@Slf4j
@Tag(name = "스탬프 투어 사용자 (v2)", description = "스탬프 투어 사용자용 API (v2)")
public class StampTourUserController {

    private final StampTourUserService stampTourUserService;

    @Operation(summary = "회원가입", description = "스탬프 투어에 새로운 사용자로 회원가입합니다.")
    @PostMapping("/{stampId}/signup")
    public void stampUserSignup(
            @PathVariable @Min(1) long festivalId,
            @PathVariable @Min(1) long stampId,
            @RequestBody @Valid StampUserSignupReqDto request) {
        stampTourUserService.signup(festivalId, stampId, request);
    }

    @Operation(summary = "로그인", description = "스탬프 투어에 로그인합니다.")
    @PostMapping("/{stampId}/login")
    public TokenDto login(
            @PathVariable @Min(1) long festivalId,
            @PathVariable @Min(1) long stampId,
            @RequestBody @Valid StampUserLoginDto request) {
        return stampTourUserService.login(festivalId, stampId, request);
    }

    @Operation(summary = "미션 보드 조회", description = "사용자의 미션 보드를 조회합니다.")
    @GetMapping("/{stampId}/missions")
    public MissionBoardResDto getUserMissionBoard(
            @AuthenticationPrincipal CustomStampUserDetails userDetails,
            @PathVariable @Min(1) long festivalId,
            @PathVariable @Min(1) long stampId) {
        return stampTourUserService.getMissionBoard(festivalId, stampId, userDetails.getUsername());
    }

    @Operation(summary = "미션 상세 조회", description = "미션의 상세 정보를 조회합니다.")
    @GetMapping("/{stampId}/missions/{missionId}")
    public MissionTemplateResDto getMissionDetails(
            @AuthenticationPrincipal CustomStampUserDetails userDetails,
            @PathVariable @Min(1) long festivalId,
            @PathVariable @Min(1) long stampId,
            @PathVariable @Min(1) long missionId) {
        return stampTourUserService.getMissionsTemplate(festivalId, stampId, missionId, userDetails.getUsername());
    }

    @Operation(summary = "QR 미션 인증", description = "QR 코드로 미션을 인증합니다.")
    @PatchMapping("/{stampId}/verify/qr")
    public void successMissionVerifyByQr(
            @AuthenticationPrincipal CustomStampUserDetails userDetails,
            @PathVariable @Min(1) long festivalId,
            @PathVariable @Min(1) long stampId,
            @RequestBody @Valid MissionQrVerifyReqDto request) {
        stampTourUserService.successMissionByQr(festivalId, stampId, request.getMissionId(), userDetails.getUsername());
    }

    @Operation(summary = "상품 수령 QR 조회", description = "상품 수령을 위한 QR 정보를 조회합니다.")
    @GetMapping("/{stampId}/prizes/qr")
    public PrizeClaimQrResDto getStampUserPrizeQrInfo(
            @AuthenticationPrincipal CustomStampUserDetails userDetails,
            @PathVariable @Min(1) long festivalId,
            @PathVariable @Min(1) long stampId) {
        return stampTourUserService.getPrizeReceiveQr(festivalId, stampId, userDetails.getUsername());
    }
}
