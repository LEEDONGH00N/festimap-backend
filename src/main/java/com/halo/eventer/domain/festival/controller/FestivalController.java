package com.halo.eventer.domain.festival.controller;

import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.halo.eventer.domain.festival.dto.*;
import com.halo.eventer.domain.festival.service.FestivalService;
import com.halo.eventer.domain.image.dto.FileDto;
import com.halo.eventer.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/festivals")
@Tag(name = "축제", description = "축제 관리 API")
public class FestivalController {

    private final FestivalService festivalService;

    @Operation(summary = "축제 생성", description = "새로운 축제를 생성합니다. 생성한 회원이 owner로 설정됩니다.")
    @PostMapping()
    public void createFestival(
            @Valid @RequestBody FestivalCreateDto festivalCreateDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        festivalService.create(festivalCreateDto, userDetails.getMember());
    }

    @Operation(summary = "축제 상세 조회", description = "축제 ID로 축제 상세 정보를 조회합니다.")
    @GetMapping("/{id}")
    public FestivalResDto getFestival(@Min(1) @PathVariable("id") Long id) {
        return festivalService.findById(id);
    }

    @Operation(summary = "축제 목록 조회", description = "모든 축제 목록을 조회합니다.")
    @GetMapping
    public List<FestivalSummaryDto> getFestivals() {
        return festivalService.findAll();
    }

    @Operation(summary = "축제 수정", description = "축제 정보를 수정합니다.")
    @PutMapping()
    public FestivalResDto updateFestival(
            @Min(1) @RequestParam("id") Long id, @Valid @RequestBody FestivalCreateDto festivalCreateDto) {
        return festivalService.update(id, festivalCreateDto);
    }

    @Operation(summary = "축제 삭제", description = "축제를 삭제합니다.")
    @DeleteMapping()
    public void deleteFestival(@Min(1) @RequestParam("id") Long id) {
        festivalService.delete(id);
    }

    @Operation(summary = "축제 색상 수정", description = "축제의 테마 색상을 수정합니다.")
    @PatchMapping("/{festivalId}/color")
    public void addColor(@Min(1) @PathVariable("festivalId") Long festivalId, @Valid @RequestBody ColorDto colorDto) {
        festivalService.updateColor(festivalId, colorDto);
    }

    @Operation(summary = "축제 로고 수정", description = "축제의 로고 이미지를 수정합니다.")
    @PatchMapping("/{festivalId}/logo")
    public void addLogo(@Min(1) @PathVariable("festivalId") Long festivalId, @RequestBody FileDto fileDto) {
        festivalService.updateLogo(festivalId, fileDto);
    }

    @Operation(summary = "축제 위치 수정", description = "축제의 위치 정보를 수정합니다.")
    @PatchMapping("/{festivalId}/location")
    public FestivalResDto updateFestivalLocation(
            @Min(1) @PathVariable("festivalId") Long festivalId, @RequestBody FestivalLocationDto festivalLocationDto) {
        return festivalService.updateLocation(festivalId, festivalLocationDto);
    }

    @Operation(summary = "축제 위치 조회", description = "축제의 위치 정보를 조회합니다.")
    @GetMapping("/{festivalId}/location")
    public FestivalLocationDto getFestivalLocationInfo(@PathVariable("festivalId") Long festivalId) {
        return festivalService.getFestivalLocationInfo(festivalId);
    }

    @Operation(summary = "축제 이름 수정", description = "축제의 이름을 수정합니다.")
    @PatchMapping("/{festivalId}/name")
    public void updateFestivalName(
            @PathVariable("festivalId") Long festivalId, @RequestBody FestivalNameReqDto festivalNameReqDto) {
        festivalService.updateFestivalName(festivalId, festivalNameReqDto);
    }

    @Operation(summary = "축제 서브 도메인 수정", description = "축제의 서브 도메인을 수정합니다.")
    @PatchMapping("/{festivalId}/sub-domain")
    public void updateFestivalSubDomain(
            @PathVariable("festivalId") Long festivalId, @RequestBody FestivalSubDomainReqDto festivalSubdomainReqDto) {
        festivalService.updateFestivalSubDomain(festivalId, festivalSubdomainReqDto);
    }
}
