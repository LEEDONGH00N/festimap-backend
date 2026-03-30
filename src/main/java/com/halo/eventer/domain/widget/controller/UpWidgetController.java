package com.halo.eventer.domain.widget.controller;

import java.time.LocalDateTime;
import java.util.List;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.halo.eventer.domain.widget.dto.up_widget.UpWidgetCreateDto;
import com.halo.eventer.domain.widget.dto.up_widget.UpWidgetResDto;
import com.halo.eventer.domain.widget.service.UpWidgetService;
import com.halo.eventer.global.common.page.PagedResponse;
import com.halo.eventer.global.common.sort.SortOption;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/upWidgets")
@Validated
@Tag(name = "상단 위젯", description = "홈 화면 상단 위젯 관리 API")
public class UpWidgetController {

    private final UpWidgetService upWidgetService;

    @Operation(summary = "상단 위젯 생성", description = "새로운 상단 위젯을 생성합니다.")
    @PostMapping()
    public UpWidgetResDto create(
            @RequestParam(name = "festivalId") Long festivalId, @RequestBody UpWidgetCreateDto upWidgetCreateDto) {
        return upWidgetService.create(festivalId, upWidgetCreateDto);
    }

    @Operation(summary = "상단 위젯 상세 조회", description = "상단 위젯 ID로 상세 정보를 조회합니다.")
    @GetMapping("/{upWidgetId}")
    public UpWidgetResDto getUpWidget(@PathVariable("upWidgetId") Long upWidgetId) {
        return upWidgetService.getUpWidget(upWidgetId);
    }

    @Operation(summary = "상단 위젯 목록 조회", description = "축제의 상단 위젯 목록을 페이징하여 조회합니다.")
    @GetMapping
    public PagedResponse<UpWidgetResDto> getUpWidgets(
            @RequestParam(name = "festivalId") Long festivalId,
            @RequestParam(name = "sortOption") SortOption sortOption,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam @Min(1) @Max(50) int size) {
        return upWidgetService.getUpWidgetsWithOffsetPaging(festivalId, sortOption, page, size);
    }

    @Operation(summary = "상단 위젯 수정", description = "상단 위젯 정보를 수정합니다.")
    @PatchMapping()
    public UpWidgetResDto update(
            @RequestParam("upWidgetId") Long upWidgetId, @RequestBody UpWidgetCreateDto widgetCreateDto) {
        return upWidgetService.update(upWidgetId, widgetCreateDto);
    }

    @Operation(summary = "상단 위젯 삭제", description = "상단 위젯을 삭제합니다.")
    @DeleteMapping()
    public void delete(@RequestParam("upWidgetId") Long upWidgetId) {
        upWidgetService.delete(upWidgetId);
    }

    @Operation(summary = "시간별 상단 위젯 조회", description = "특정 시간에 표시될 상단 위젯 목록을 조회합니다.")
    @GetMapping("/datetime")
    public List<UpWidgetResDto> getUpWidgetListByDateTime(
            @RequestParam("festivalId") Long festivalId,
            @RequestParam("now") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime now) {
        return upWidgetService.getUpWidgetsByNow(festivalId, now);
    }
}
