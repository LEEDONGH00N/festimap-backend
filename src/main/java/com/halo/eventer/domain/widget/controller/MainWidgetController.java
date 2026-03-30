package com.halo.eventer.domain.widget.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.halo.eventer.domain.widget.dto.main_widget.MainWidgetCreateDto;
import com.halo.eventer.domain.widget.dto.main_widget.MainWidgetResDto;
import com.halo.eventer.domain.widget.service.MainWidgetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mainWidgets")
@Tag(name = "메인 위젯", description = "홈 화면 메인 위젯 관리 API")
public class MainWidgetController {

    private final MainWidgetService mainWidgetService;

    @Operation(summary = "메인 위젯 생성", description = "새로운 메인 위젯을 생성합니다.")
    @PostMapping()
    public MainWidgetResDto create(
            @RequestParam Long festivalId, @RequestBody MainWidgetCreateDto mainWidgetCreateDto) {
        return mainWidgetService.create(festivalId, mainWidgetCreateDto);
    }

    @Operation(summary = "메인 위젯 목록 조회", description = "축제의 메인 위젯 목록을 조회합니다.")
    @GetMapping()
    public List<MainWidgetResDto> getDownWidgets(@RequestParam("festivalId") Long festivalId) {
        return mainWidgetService.getAllMainWidget(festivalId);
    }

    @Operation(summary = "메인 위젯 수정", description = "메인 위젯 정보를 수정합니다.")
    @PatchMapping()
    public MainWidgetResDto update(@RequestParam Long id, @RequestBody MainWidgetCreateDto mainWidgetCreateDto) {
        return mainWidgetService.update(id, mainWidgetCreateDto);
    }

    @Operation(summary = "메인 위젯 삭제", description = "메인 위젯을 삭제합니다.")
    @DeleteMapping()
    public void delete(@RequestParam Long id) {
        mainWidgetService.delete(id);
    }
}
