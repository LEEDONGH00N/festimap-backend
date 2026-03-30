package com.halo.eventer.domain.stamp.controller;

import java.util.List;
import jakarta.validation.constraints.Min;

import org.springframework.web.bind.annotation.*;

import com.halo.eventer.domain.stamp.dto.mission.response.MissionSummaryResDto;
import com.halo.eventer.domain.stamp.dto.stamp.*;
import com.halo.eventer.domain.stamp.service.StampService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stamp")
@Tag(name = "스탬프 (v1)", description = "스탬프 관리 API (v1)")
public class StampController {

    private final StampService stampService;

    @Operation(summary = "스탬프 등록", description = "축제에 새로운 스탬프를 등록합니다.")
    @PostMapping
    public List<StampGetDto> registerStamp(@Min(1) @RequestParam("festivalId") long festivalId) {
        return stampService.registerStamp(festivalId);
    }

    @Operation(summary = "스탬프 목록 조회", description = "축제의 스탬프 목록을 조회합니다.")
    @GetMapping
    public List<StampGetDto> getStampList(@Min(1) @RequestParam("festivalId") Long festivalId) {
        return stampService.getStampByFestivalId(festivalId);
    }

    @Operation(summary = "스탬프 활성화", description = "스탬프를 활성화합니다.")
    @PatchMapping
    public void updateStampOn(@Min(1) @RequestParam("stampId") Long stampId) {
        stampService.updateStampOn(stampId);
    }

    @Operation(summary = "스탬프 삭제", description = "스탬프를 삭제합니다.")
    @DeleteMapping
    public void deleteStamp(@Min(1) @RequestParam("stampId") Long stampId) {
        stampService.deleteStamp(stampId);
    }

    @Operation(summary = "미션 목록 조회", description = "스탬프에 속한 미션 목록을 조회합니다.")
    @GetMapping("/missions")
    public List<MissionSummaryResDto> getMissionList(@Min(1) @RequestParam("stampId") Long stampId) {
        return stampService.getMissions(stampId);
    }

    @Operation(summary = "스탬프 사용자 목록 조회", description = "스탬프에 참여한 사용자 목록을 조회합니다.")
    @GetMapping("/users")
    public List<StampUsersGetDto> getStampUsers(@Min(1) @RequestParam("stampId") Long stampId) {
        return stampService.getStampUsers(stampId);
    }

    @Operation(summary = "완료 필요 미션 수 설정", description = "스탬프 완료에 필요한 미션 수를 설정합니다.")
    @PostMapping("/finishCnt")
    public void setFinishCnt(@Min(1) @RequestParam("stampId") Long stampId, @Min(1) @RequestParam("cnt") int cnt) {
        stampService.setFinishCnt(stampId, cnt);
    }
}
