package com.halo.eventer.domain.stamp.controller;

import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

import org.springframework.web.bind.annotation.*;

import com.halo.eventer.domain.stamp.dto.mission.MissionUpdateDto;
import com.halo.eventer.domain.stamp.dto.mission.request.MissionSetReqDto;
import com.halo.eventer.domain.stamp.dto.mission.response.MissionDetailGetResDto;
import com.halo.eventer.domain.stamp.dto.mission.response.MissionSummaryResDto;
import com.halo.eventer.domain.stamp.service.MissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/stamp/mission")
@Tag(name = "미션 (v1)", description = "스탬프 미션 관리 API (v1)")
public class MissionController {

    private final MissionService missionService;

    @Operation(summary = "미션 생성", description = "스탬프에 새로운 미션을 생성합니다.")
    @PostMapping
    public void createMissionList(
            @Min(1) @RequestParam("stampId") Long stampId,
            @RequestBody @Valid @NotEmpty List<@Valid MissionSetReqDto> dto) {
        missionService.createMission(stampId, dto);
    }

    @Operation(summary = "미션 상세 조회", description = "미션 ID로 상세 정보를 조회합니다.")
    @GetMapping
    public MissionDetailGetResDto getMission(@Min(1) @RequestParam("missionId") Long missionId) {
        return missionService.getMission(missionId);
    }

    @Operation(summary = "미션 목록 조회", description = "스탬프에 속한 모든 미션 목록을 조회합니다.")
    @GetMapping("/all")
    public List<MissionSummaryResDto> getMissionList(@Min(1) @RequestParam("stampId") Long stampId) {
        return missionService.getMissions(stampId);
    }

    @Operation(summary = "미션 수정", description = "미션 정보를 수정합니다.")
    @PatchMapping
    public void updateMission(
            @Min(1) @RequestParam("missionId") Long missionId, @RequestBody MissionUpdateDto missionUpdateDto) {
        missionService.updateMission(missionId, missionUpdateDto);
    }
}
