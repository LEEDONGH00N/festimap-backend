package com.halo.eventer.domain.home.controller;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.halo.eventer.domain.festival.dto.FestivalSummaryDto;
import com.halo.eventer.domain.festival.service.FestivalService;
import com.halo.eventer.domain.home.dto.HomeDto;
import com.halo.eventer.domain.home.service.HomeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "홈", description = "홈 화면 및 축제 정보 조회 API")
public class HomeController {

    private final HomeService homeService;
    private final FestivalService festivalService;

    @Operation(summary = "홈 화면 조회", description = "축제 ID로 홈 화면에 필요한 정보를 조회합니다.")
    @GetMapping("/home/{festivalId}")
    public HomeDto getHomeInfo(@Min(1) @PathVariable Long festivalId) {
        return homeService.getMainPage(festivalId);
    }

    @Operation(summary = "서브 도메인으로 축제 조회", description = "서브 도메인 주소로 축제 요약 정보를 조회합니다.")
    @GetMapping("/home")
    public FestivalSummaryDto getFestivalBySubAddress(@NotNull @RequestParam("subAddress") String name) {
        return festivalService.findBySubAddress(name);
    }

    @Operation(summary = "헬스 체크", description = "서버 상태를 확인합니다.")
    @GetMapping("/")
    public void getOk() {}
}
