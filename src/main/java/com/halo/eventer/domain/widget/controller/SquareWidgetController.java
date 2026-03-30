package com.halo.eventer.domain.widget.controller;

import java.util.List;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.halo.eventer.domain.widget.dto.square_widget.SquareWidgetCreateDto;
import com.halo.eventer.domain.widget.dto.square_widget.SquareWidgetResDto;
import com.halo.eventer.domain.widget.service.SquareWidgetService;
import com.halo.eventer.global.common.dto.OrderUpdateRequest;
import com.halo.eventer.global.common.page.PagedResponse;
import com.halo.eventer.global.common.sort.SortOption;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/squareWidgets")
@Validated
@Tag(name = "정사각형 위젯", description = "홈 화면 정사각형 위젯 관리 API")
public class SquareWidgetController {

    private final SquareWidgetService squareWidgetService;

    @Operation(summary = "정사각형 위젯 생성", description = "새로운 정사각형 위젯을 생성합니다.")
    @PostMapping()
    public SquareWidgetResDto create(
            @RequestParam("festivalId") Long festivalId, @RequestBody SquareWidgetCreateDto squareWidgetCreateDto) {

        return squareWidgetService.create(festivalId, squareWidgetCreateDto);
    }

    @Operation(summary = "정사각형 위젯 상세 조회", description = "정사각형 위젯 ID로 상세 정보를 조회합니다.")
    @GetMapping("/{id}")
    public SquareWidgetResDto getWidget(@PathVariable("id") Long id) {
        return squareWidgetService.getSquareWidget(id);
    }

    @Operation(summary = "정사각형 위젯 목록 조회", description = "축제의 정사각형 위젯 목록을 페이징하여 조회합니다.")
    @GetMapping()
    public PagedResponse<SquareWidgetResDto> getWidgets(
            @RequestParam("festivalId") Long festivalId,
            @RequestParam(name = "sortOption") SortOption sortOption,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam @Min(1) @Max(50) int size) {
        return squareWidgetService.getSquareWidgetsWithOffsetPaging(festivalId, sortOption, page, size);
    }

    @Operation(summary = "정사각형 위젯 수정", description = "정사각형 위젯 정보를 수정합니다.")
    @PatchMapping
    public SquareWidgetResDto update(
            @RequestParam("id") Long id, @RequestBody SquareWidgetCreateDto squareWidgetCreateDto) {
        return squareWidgetService.update(id, squareWidgetCreateDto);
    }

    @Operation(summary = "정사각형 위젯 삭제", description = "정사각형 위젯을 삭제합니다.")
    @DeleteMapping
    public void delete(@RequestParam("id") Long id) {
        squareWidgetService.delete(id);
    }

    @Operation(summary = "정사각형 위젯 표시 순서 수정", description = "정사각형 위젯의 표시 순서를 수정합니다.")
    @PatchMapping("/displayOrder")
    public List<SquareWidgetResDto> updateDisplayOrder(
            @RequestParam long festivalId, @RequestBody List<OrderUpdateRequest> orderRequests) {
        return squareWidgetService.updateDisplayOrder(festivalId, orderRequests);
    }
}
