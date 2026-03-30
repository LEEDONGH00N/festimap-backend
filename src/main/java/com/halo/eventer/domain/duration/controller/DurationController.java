package com.halo.eventer.domain.duration.controller;

import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

import org.springframework.web.bind.annotation.*;

import com.halo.eventer.domain.duration.dto.DurationCreateDto;
import com.halo.eventer.domain.duration.dto.DurationResDto;
import com.halo.eventer.domain.duration.service.DurationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/duration")
@Tag(name = "축제 기간", description = "축제 운영 기간 관리 API")
public class DurationController {
    private final DurationService durationService;

    @Operation(summary = "축제 기간 생성", description = "축제의 운영 기간을 생성합니다.")
    @PostMapping()
    public void createDurations(
            @Min(1) @RequestParam("festivalId") long festivalId,
            @RequestBody List<@Valid DurationCreateDto> durationCreateDtos) {
        durationService.createDurations(festivalId, durationCreateDtos);
    }

    @Operation(summary = "축제 기간 조회", description = "축제의 운영 기간 목록을 조회합니다.")
    @GetMapping()
    public List<DurationResDto> getDurations(@Min(1) @RequestParam("festivalId") Long festivalId) {
        return durationService.getDurations(festivalId);
    }
}
