package com.halo.eventer.domain.map.controller;

import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

import org.springframework.web.bind.annotation.*;

import com.halo.eventer.domain.map.dto.map.MapCreateDto;
import com.halo.eventer.domain.map.dto.map.MapItemDto;
import com.halo.eventer.domain.map.dto.map.MapResDto;
import com.halo.eventer.domain.map.dto.map.MapUpdateDto;
import com.halo.eventer.domain.map.service.MapService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "지도", description = "지도 마커 관리 API")
public class MapController {

    private final MapService mapService;

    @Operation(summary = "지도 마커 생성", description = "특정 카테고리에 새로운 지도 마커를 생성합니다.")
    @PostMapping("/map-categories/{mapCategoryId}/maps")
    public MapResDto createMap(
            @Min(1) @PathVariable("mapCategoryId") Long mapCategoryId, @Valid @RequestBody MapCreateDto mapCreateDto) {
        return mapService.create(mapCreateDto, mapCategoryId);
    }

    @Operation(summary = "지도 마커 상세 조회", description = "지도 마커 ID로 상세 정보를 조회합니다.")
    @GetMapping("/maps/{mapId}")
    public MapResDto getMap(@Min(1) @PathVariable("mapId") Long mapId) {
        return mapService.getMap(mapId);
    }

    @Operation(summary = "카테고리별 지도 마커 목록 조회", description = "특정 카테고리의 지도 마커 목록을 조회합니다.")
    @GetMapping("/map-categories/{mapCategoryId}/maps")
    public List<MapItemDto> getMaps(@Min(1) @PathVariable("mapCategoryId") Long mapCategoryId) {
        return mapService.getMaps(mapCategoryId);
    }

    @Operation(summary = "지도 마커 수정", description = "지도 마커 정보를 수정합니다.")
    @PutMapping("/map-categories/{mapCategoryId}/maps/{mapId}")
    public MapResDto update(
            @Min(1) @PathVariable("mapCategoryId") Long mapCategoryId,
            @Min(1) @PathVariable("mapId") Long mapId,
            @Valid @RequestBody MapUpdateDto mapUpdateDto) {
        return mapService.update(mapId, mapUpdateDto, mapCategoryId);
    }

    @Operation(summary = "지도 마커 삭제", description = "지도 마커를 삭제합니다.")
    @DeleteMapping("/maps/{mapId}")
    public void delete(@Min(1) @PathVariable("mapId") Long id) {
        mapService.delete(id);
    }
}
