package com.halo.eventer.domain.map.controller;

import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

import org.springframework.web.bind.annotation.*;

import com.halo.eventer.domain.map.dto.mapcategory.MapCategoryCreateDto;
import com.halo.eventer.domain.map.dto.mapcategory.MapCategoryImageDto;
import com.halo.eventer.domain.map.dto.mapcategory.MapCategoryResDto;
import com.halo.eventer.domain.map.service.MapCategoryService;
import com.halo.eventer.global.common.dto.OrderUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "지도 카테고리", description = "지도 카테고리 관리 API")
public class MapCategoryController {

    private final MapCategoryService mapCategoryService;

    @Operation(summary = "지도 카테고리 생성", description = "새로운 지도 카테고리를 생성합니다.")
    @PostMapping("/{festivalId}/map-categories")
    public void create(
            @Min(1) @PathVariable("festivalId") Long festivalId,
            @Valid @RequestBody MapCategoryCreateDto mapCategoryCreateDto) {
        mapCategoryService.create(festivalId, mapCategoryCreateDto.getName());
    }

    @Operation(summary = "카테고리 이미지 조회", description = "지도 카테고리의 아이콘 및 핀 이미지를 조회합니다.")
    @GetMapping("/map-categories/{mapCategoryId}/image")
    public MapCategoryImageDto getMapCategoryImages(@Min(1) @PathVariable("mapCategoryId") Long mapCategoryId) {
        return mapCategoryService.getIconAndPin(mapCategoryId);
    }

    @Operation(summary = "지도 카테고리 목록 조회", description = "축제의 지도 카테고리 목록을 조회합니다.")
    @GetMapping("/{festivalId}/map-categories")
    public List<MapCategoryResDto> getMapCategories(@Min(1) @PathVariable("festivalId") Long festivalId) {
        return mapCategoryService.getMapCategories(festivalId);
    }

    @Operation(summary = "카테고리 이미지 수정", description = "지도 카테고리의 아이콘 및 핀 이미지를 수정합니다.")
    @PatchMapping("/map-categories/{mapCategoryId}/image")
    public void updateIconAndPin(
            @Min(1) @PathVariable("mapCategoryId") Long mapCategoryId,
            @RequestBody MapCategoryImageDto mapCategoryImageDto) {
        mapCategoryService.updateIconAndPin(mapCategoryId, mapCategoryImageDto);
    }

    @Operation(summary = "카테고리 표시 순서 수정", description = "지도 카테고리의 표시 순서를 수정합니다.")
    @PatchMapping("/{festivalId}/map-categories/displayOrder")
    public void updateDisplayOrder(
            @Min(1) @PathVariable("festivalId") Long festivalId,
            @Valid @RequestBody List<OrderUpdateRequest> orderUpdateRequests) {
        mapCategoryService.updateDisplayOrder(festivalId, orderUpdateRequests);
    }

    @Operation(summary = "지도 카테고리 삭제", description = "지도 카테고리를 삭제합니다.")
    @DeleteMapping("/map-categories/{mapCategoryId}")
    public void delete(@Min(1) @PathVariable("mapCategoryId") Long mapCategoryId) {
        mapCategoryService.delete(mapCategoryId);
    }
}
