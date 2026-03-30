package com.halo.eventer.domain.manager.controller;

import org.springframework.web.bind.annotation.*;

import com.halo.eventer.domain.manager.dto.ManagerCreateReqDto;
import com.halo.eventer.domain.manager.dto.ManagerListResDto;
import com.halo.eventer.domain.manager.service.ManagerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "관리자", description = "축제 관리자 계정 관리 API")
public class ManagerController {

    private final ManagerService managerService;

    @Operation(summary = "관리자 생성", description = "특정 축제에 새로운 관리자 계정을 생성합니다.")
    @PostMapping("/manager")
    public ManagerListResDto createManager(
            @RequestParam("festivalId") Long festivalId, @RequestBody ManagerCreateReqDto managerCreateReqDto) {
        managerService.createManager(festivalId, managerCreateReqDto);
        return new ManagerListResDto(managerService.getManagerList(festivalId));
    }

    @Operation(summary = "관리자 목록 조회", description = "특정 축제의 모든 관리자 목록을 조회합니다.")
    @GetMapping("/manager")
    public ManagerListResDto getManagerList(@RequestParam("festivalId") Long festivalId) {
        return new ManagerListResDto(managerService.getManagerList(festivalId));
    }

    @Operation(summary = "관리자 삭제", description = "특정 관리자 계정을 삭제합니다.")
    @DeleteMapping("/manager")
    public ManagerListResDto deleteManager(
            @RequestParam("festivalId") Long festivalId, @RequestParam("managerId") Long managerId) {
        managerService.deleteManager(managerId);
        return new ManagerListResDto(managerService.getManagerList(festivalId));
    }
}
