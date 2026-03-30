package com.halo.eventer.domain.stamp.controller.v2;

import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

import org.springframework.web.bind.annotation.*;

import com.halo.eventer.domain.stamp.dto.mission.request.MissionClearReqDto;
import com.halo.eventer.domain.stamp.dto.stampUser.enums.Finished;
import com.halo.eventer.domain.stamp.dto.stampUser.enums.SortType;
import com.halo.eventer.domain.stamp.dto.stampUser.request.MissionCompletionUpdateReq;
import com.halo.eventer.domain.stamp.dto.stampUser.request.StampUserInfoUpdateReqDto;
import com.halo.eventer.domain.stamp.dto.stampUser.response.*;
import com.halo.eventer.domain.stamp.service.v2.StampUserAdminService;
import com.halo.eventer.global.common.page.PagedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/admin/festivals/{festivalId}/stamp-tours/{stampId}/users")
@Tag(name = "스탬프 사용자 관리 (v2)", description = "관리자용 스탬프 투어 사용자 관리 API (v2)")
public class StampUserAdminController {

    private final StampUserAdminService stampUserAdminService;

    @Operation(summary = "전체 사용자 목록 조회", description = "스탬프 투어의 모든 사용자 목록을 조회합니다.")
    @GetMapping("/all")
    public List<StampUserInfoResDto> getAllStampUsers(
            @PathVariable @Min(1) long festivalId, @PathVariable @Min(1) long stampId) {
        return stampUserAdminService.getAllStampUsers(festivalId, stampId);
    }

    @Operation(summary = "사용자 목록 필터 조회", description = "조건에 맞는 사용자 목록을 페이징하여 조회합니다.")
    @GetMapping
    public PagedResponse<StampUserSummaryResDto> getStampUsersFiltered(
            @PathVariable @Min(1) long festivalId,
            @PathVariable @Min(1) long stampId,
            @RequestParam(defaultValue = "", required = false) String q,
            @RequestParam(defaultValue = "ALL", required = false) Finished finished,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "NAME", required = false) SortType sortType) {
        return stampUserAdminService.getStampUsers(festivalId, stampId, q, finished, page, size, sortType);
    }

    @Operation(summary = "사용자 완료 상태 수정", description = "사용자의 스탬프 투어 완료 상태와 상품을 수정합니다.")
    @PatchMapping("/{userId}/tour-finish")
    public void updateStampUser(
            @PathVariable @Min(1) long festivalId,
            @PathVariable @Min(1) long stampId,
            @PathVariable @Min(1) long userId,
            @RequestBody @Valid MissionCompletionUpdateReq req) {
        stampUserAdminService.updateStampUserPrizeAndFinished(festivalId, stampId, userId, req);
    }

    @Operation(summary = "사용자 상세 조회", description = "사용자의 상세 정보를 조회합니다.")
    @GetMapping("/{userId}")
    public StampUserDetailResDto getStampUser(
            @PathVariable @Min(1) long festivalId,
            @PathVariable @Min(1) long stampId,
            @PathVariable @Min(1) long userId) {
        return stampUserAdminService.getUserDetail(festivalId, stampId, userId);
    }

    @Operation(summary = "사용자 미션 상태 수정", description = "사용자의 특정 미션 상태를 수정합니다.")
    @PatchMapping("/{userId}/user-mission/{userMissionId}")
    public void updateStampUserMission(
            @PathVariable @Min(1) long festivalId,
            @PathVariable @Min(1) long stampId,
            @PathVariable @Min(1) long userId,
            @PathVariable @Min(1) long userMissionId,
            @RequestBody @Valid MissionClearReqDto request) {
        stampUserAdminService.updateUserMissionState(festivalId, stampId, userId, userMissionId, request);
    }

    @Operation(summary = "사용자 정보 수정", description = "사용자의 기본 정보를 수정합니다.")
    @PatchMapping("/{userId}")
    public void updateStampUserInfo(
            @PathVariable @Min(1) long festivalId,
            @PathVariable @Min(1) long stampId,
            @PathVariable @Min(1) long userId,
            @RequestBody @Valid StampUserInfoUpdateReqDto request) {
        stampUserAdminService.updateStampUserInfo(festivalId, stampId, userId, request);
    }

    @Operation(summary = "UUID로 사용자 ID 조회", description = "UUID로 사용자 ID를 조회합니다.")
    @GetMapping("/uuid/{uuid}")
    public StampUserUserIdResDto getStampUserUuid(
            @PathVariable @Min(1) long festivalId, @PathVariable @Min(1) long stampId, @PathVariable String uuid) {
        return stampUserAdminService.getStampUserId(festivalId, stampId, uuid);
    }
}
