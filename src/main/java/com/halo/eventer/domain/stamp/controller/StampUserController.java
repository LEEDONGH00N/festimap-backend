package com.halo.eventer.domain.stamp.controller;

import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import org.springframework.web.bind.annotation.*;

import com.halo.eventer.domain.stamp.dto.stamp.StampUsersGetDto;
import com.halo.eventer.domain.stamp.dto.stampUser.*;
import com.halo.eventer.domain.stamp.service.StampUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stamp/user")
@Tag(name = "스탬프 사용자 (v1)", description = "스탬프 사용자 관리 API (v1)")
public class StampUserController {
    private final StampUserService stampUserService;

    @Operation(summary = "사용자 회원가입", description = "커스텀 필드 없이 스탬프에 사용자를 등록합니다.")
    @PostMapping
    public StampUserGetDto signup(
            @Min(1) @RequestParam("stampId") Long stampId,
            @RequestBody @Valid SignupWithoutCustomDto signupWithoutCustomDto) {
        return stampUserService.signup(stampId, signupWithoutCustomDto);
    }

    @Operation(summary = "사용자 회원가입 (커스텀 필드)", description = "커스텀 필드와 함께 스탬프에 사용자를 등록합니다.")
    @PostMapping("/custom")
    public StampUserGetDto signupWithCustom(
            @Min(1) @RequestParam("stampId") Long stampId,
            @RequestBody @Valid SignupWithCustomDto signupWithCustomDto) {
        return stampUserService.signup(stampId, signupWithCustomDto);
    }

    @Operation(summary = "사용자 회원가입 (v2)", description = "스탬프에 사용자를 등록합니다. (v2)")
    @PostMapping("/signup")
    public StampUserGetDto signup(
            @Min(1) @RequestParam("stampId") long stampId, @RequestBody @Valid SignupDto signupDto) {
        return stampUserService.signupV2(stampId, signupDto);
    }

    @Operation(summary = "사용자 로그인", description = "스탬프 사용자로 로그인합니다.")
    @PostMapping("/login")
    public StampUserGetDto login(@Min(1) @RequestParam("stampId") long stampId, @RequestBody @Valid LoginDto loginDto) {
        return stampUserService.login(stampId, loginDto);
    }

    @Operation(summary = "사용자 미션 정보 조회", description = "UUID로 사용자의 미션 진행 상태를 조회합니다.")
    @GetMapping("/{uuid}")
    public UserMissionInfoWithFinishedGetListDto getMissionInfo(@Size(min = 36, max = 36) @PathVariable String uuid) {
        return stampUserService.getUserMissionWithFinished(uuid);
    }

    @Operation(summary = "스탬프 사용자 목록 조회", description = "스탬프에 참여한 모든 사용자 목록을 조회합니다.")
    @GetMapping("/all")
    public List<StampUsersGetDto> getStampUsers(@Min(1) @RequestParam("stampId") long stampId) {
        return stampUserService.getStampUsers(stampId);
    }

    @Operation(summary = "미션 완료 처리", description = "사용자의 특정 미션을 완료 처리합니다.")
    @PatchMapping("/{uuid}/{userMissionId}")
    public void updateUserMission(
            @Size(min = 36, max = 36) @PathVariable String uuid, @Min(1) @PathVariable long userMissionId) {
        stampUserService.updateUserMission(uuid, userMissionId);
    }

    @Operation(summary = "스탬프 완료 확인", description = "사용자의 스탬프 완료 여부를 확인합니다.")
    @PatchMapping("/check/{uuid}")
    public String checkFinish(@Size(min = 36, max = 36) @PathVariable String uuid) {
        return stampUserService.checkFinish(uuid);
    }

    @Operation(summary = "스탬프 완료 확인 (v2)", description = "사용자의 스탬프 완료 여부를 확인합니다. (v2)")
    @PatchMapping("/check/v2/{uuid}")
    public String checkV2Finish(@Size(min = 36, max = 36) @PathVariable String uuid) {
        return stampUserService.checkV2Finish(uuid);
    }

    @Operation(summary = "스탬프 사용자 삭제", description = "스탬프 사용자를 삭제합니다.")
    @DeleteMapping("/{uuid}")
    public void deleteStampByUuid(@Size(min = 36, max = 36) @PathVariable String uuid) {
        stampUserService.deleteStampByUuid(uuid);
    }
}
