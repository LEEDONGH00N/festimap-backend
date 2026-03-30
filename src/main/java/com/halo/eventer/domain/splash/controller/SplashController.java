package com.halo.eventer.domain.splash.controller;

import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import org.springframework.web.bind.annotation.*;

import com.halo.eventer.domain.splash.dto.request.ImageLayerDto;
import com.halo.eventer.domain.splash.dto.response.SplashGetDto;
import com.halo.eventer.domain.splash.service.SplashService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/splash")
@Tag(name = "스플래시", description = "앱 시작 화면 스플래시 이미지 관리 API")
public class SplashController {
    private final SplashService splashService;

    @Operation(summary = "스플래시 이미지 업로드", description = "스플래시 화면에 표시될 이미지를 업로드합니다.")
    @PostMapping
    public void uploadSplashImage(@Min(1) @RequestParam long festivalId, @RequestBody List<@Valid ImageLayerDto> dto) {
        splashService.uploadSplashImage(festivalId, dto);
    }

    @Operation(summary = "스플래시 이미지 삭제", description = "스플래시 이미지를 삭제합니다.")
    @DeleteMapping
    public void deleteSplashImage(
            @Min(1) @RequestParam long festivalId, @RequestBody List<@NotBlank String> layerTypes) {
        splashService.deleteSplashImage(festivalId, layerTypes);
    }

    @Operation(summary = "스플래시 이미지 조회", description = "축제의 스플래시 이미지를 조회합니다.")
    @GetMapping
    public SplashGetDto getSplash(@Min(1) @RequestParam long festivalId) {
        return splashService.getSplash(festivalId);
    }
}
