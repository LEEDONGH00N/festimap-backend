package com.halo.eventer.domain.widget.controller;

import java.util.List;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.halo.eventer.domain.widget.dto.middle_widget.*;
import com.halo.eventer.domain.widget.service.MiddleWidgetService;
import com.halo.eventer.global.common.dto.OrderUpdateRequest;
import com.halo.eventer.global.common.page.PagedResponse;
import com.halo.eventer.global.common.sort.SortOption;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/middleWidgets")
@Tag(name = "중간 위젯", description = "홈 화면 중간 위젯 관리 API")
public class MiddleWidgetController {
    private final MiddleWidgetService middleWidgetService;

    @Operation(summary = "중간 위젯 생성", description = "새로운 중간 위젯을 생성합니다.")
    @PostMapping()
    public MiddleWidgetResDto create(
            @RequestParam("festivalId") Long festivalId, @RequestBody MiddleWidgetCreateDto middleWidgetCreateDto) {
        return middleWidgetService.create(festivalId, middleWidgetCreateDto);
    }

    @Operation(summary = "중간 위젯 상세 조회", description = "중간 위젯 ID로 상세 정보를 조회합니다.")
    @GetMapping("/{id}")
    public MiddleWidgetResDto getMiddleWidget(@PathVariable("id") Long id) {
        return middleWidgetService.getMiddleWidget(id);
    }

    @Operation(summary = "중간 위젯 목록 조회", description = "축제의 중간 위젯 목록을 페이징하여 조회합니다.")
    @GetMapping()
    public PagedResponse<MiddleWidgetResDto> getMiddleWidgets(
            @RequestParam("festivalId") Long festivalId,
            @RequestParam(name = "sortOption") SortOption sortOption,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam @Min(1) @Max(50) int size) {
        return middleWidgetService.getMiddleWidgetsWithOffsetPaging(festivalId, sortOption, page, size);
    }

    @Operation(summary = "중간 위젯 수정", description = "중간 위젯 정보를 수정합니다.")
    @PatchMapping()
    public MiddleWidgetResDto update(
            @RequestParam("id") Long id, @RequestBody MiddleWidgetCreateDto middleWidgetCreateDto) {
        return middleWidgetService.update(id, middleWidgetCreateDto);
    }

    @Operation(summary = "중간 위젯 삭제", description = "중간 위젯을 삭제합니다.")
    @DeleteMapping()
    public void delete(@RequestParam("id") Long id) {
        middleWidgetService.delete(id);
    }

    @Operation(summary = "중간 위젯 표시 순서 수정", description = "중간 위젯의 표시 순서를 수정합니다.")
    @PatchMapping("/displayOrder")
    public List<MiddleWidgetResDto> updateDisplayOrder(
            @RequestParam Long festivalId, @RequestBody List<OrderUpdateRequest> orderRequests) {
        return middleWidgetService.updateDisplayOrder(festivalId, orderRequests);
    }
}
