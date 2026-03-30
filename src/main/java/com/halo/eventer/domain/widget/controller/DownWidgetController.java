package com.halo.eventer.domain.widget.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.halo.eventer.domain.widget.dto.down_widget.DownWidgetCreateDto;
import com.halo.eventer.domain.widget.dto.down_widget.DownWidgetResDto;
import com.halo.eventer.domain.widget.service.DownWidgetService;
import com.halo.eventer.global.common.dto.OrderUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/downWidgets")
@Tag(name = "하단 위젯", description = "홈 화면 하단 위젯 관리 API")
public class DownWidgetController {

    private final DownWidgetService downWidgetService;

    @Operation(summary = "하단 위젯 생성", description = "새로운 하단 위젯을 생성합니다.")
    @PostMapping()
    public DownWidgetResDto create(
            @RequestParam Long festivalId, @RequestBody DownWidgetCreateDto downWidgetCreateDto) {
        return downWidgetService.create(festivalId, downWidgetCreateDto);
    }

    @Operation(summary = "하단 위젯 목록 조회", description = "축제의 하단 위젯 목록을 조회합니다.")
    @GetMapping()
    public List<DownWidgetResDto> getDownWidgets(@RequestParam("festivalId") Long festivalId) {
        return downWidgetService.getAllDownWidgets(festivalId);
    }

    @Operation(summary = "하단 위젯 수정", description = "하단 위젯 정보를 수정합니다.")
    @PatchMapping()
    public DownWidgetResDto update(@RequestParam Long id, @RequestBody DownWidgetCreateDto downWidgetCreateDto) {
        return downWidgetService.update(id, downWidgetCreateDto);
    }

    @Operation(summary = "하단 위젯 삭제", description = "하단 위젯을 삭제합니다.")
    @DeleteMapping()
    public void delete(@RequestParam Long id) {
        downWidgetService.delete(id);
    }

    @Operation(summary = "하단 위젯 표시 순서 수정", description = "하단 위젯의 표시 순서를 수정합니다.")
    @PatchMapping("/displayOrder")
    public List<DownWidgetResDto> updateDisplayOrder(
            @RequestParam long festivalId, @RequestBody List<OrderUpdateRequest> orderRequests) {
        return downWidgetService.updateDisplayOrder(festivalId, orderRequests);
    }
}
