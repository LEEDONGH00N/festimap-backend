package com.halo.eventer.domain.widget_item.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.halo.eventer.domain.image.dto.FileDto;
import com.halo.eventer.domain.image.dto.ImageDto;
import com.halo.eventer.domain.widget_item.dto.WidgetItemCreateDto;
import com.halo.eventer.domain.widget_item.dto.WidgetItemResDto;
import com.halo.eventer.domain.widget_item.service.WidgetItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/widgetItem")
@Tag(name = "위젯 아이템", description = "메인 위젯 내부 요소 관리 API (입장방법, 관람안내 등)")
public class WidgetItemController {
    private final WidgetItemService widgetItemService;

    @Operation(summary = "위젯 아이템 생성", description = "메인 위젯에 새로운 내부 요소를 생성합니다.")
    @PostMapping("/{widgetId}")
    public WidgetItemResDto createInfoName(
            @PathVariable("widgetId") Long widgetId, @RequestBody WidgetItemCreateDto widgetItemCreateDto) {
        return widgetItemService.create(widgetId, widgetItemCreateDto);
    }

    @Operation(summary = "위젯 아이템 목록 조회", description = "특정 위젯의 내부 요소 목록을 조회합니다.")
    @GetMapping("/{widgetId}")
    public List<WidgetItemResDto> getWidgetItemsByWidgetId(@PathVariable("widgetId") Long widgetId) {
        return widgetItemService.getWidgetItemWithWidgetId(widgetId);
    }

    @Operation(summary = "위젯 아이템 이미지 조회", description = "특정 위젯 아이템의 이미지 목록을 조회합니다.")
    @GetMapping
    public List<ImageDto> getWidgetItemImages(@RequestParam("widgetId") Long widgetId) {
        return widgetItemService.getWidgetItemImages(widgetId);
    }

    @Operation(summary = "위젯 아이템 수정", description = "위젯 아이템 정보를 수정합니다.")
    @PatchMapping("/{widgetId}")
    public WidgetItemResDto updateWidgetItem(
            @PathVariable("widgetId") Long id, @RequestBody WidgetItemCreateDto widgetItemCreateDto) {
        return widgetItemService.updateWidgetInfo(id, widgetItemCreateDto);
    }

    @Operation(summary = "위젯 아이템 이미지 추가", description = "위젯 아이템에 이미지를 추가합니다.")
    @PostMapping("/{widgetId}/images")
    public List<ImageDto> addImages(@PathVariable Long widgetId, @RequestBody List<FileDto> fileDtos) {
        return widgetItemService.addImages(widgetId, fileDtos);
    }

    @Operation(summary = "위젯 아이템 이미지 삭제", description = "위젯 아이템의 이미지를 삭제합니다.")
    @DeleteMapping("/{widgetId}/images")
    public void deleteImages(@PathVariable Long widgetId, @RequestBody List<Long> imageIds) {
        widgetItemService.deleteImages(widgetId, imageIds);
    }

    @Operation(summary = "위젯 아이템 삭제", description = "위젯 아이템을 삭제합니다.")
    @DeleteMapping("/{widgetId}")
    public void deleteConcertInfo(@PathVariable("widgetId") Long id) {
        widgetItemService.delete(id);
    }
}
