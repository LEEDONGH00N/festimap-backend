package com.halo.eventer.domain.lost_item.controller;

import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

import org.springframework.web.bind.annotation.*;

import com.halo.eventer.domain.lost_item.dto.LostItemReqDto;
import com.halo.eventer.domain.lost_item.dto.LostItemResDto;
import com.halo.eventer.domain.lost_item.dto.LostItemSummaryDto;
import com.halo.eventer.domain.lost_item.service.LostItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "분실물", description = "분실물 관리 API")
public class LostItemController {

    private final LostItemService lostItemService;

    @Operation(summary = "분실물 등록", description = "새로운 분실물을 등록합니다.")
    @PostMapping("/lost-items")
    public void create(
            @Min(1) @RequestParam("festivalId") Long festivalId, @Valid @RequestBody LostItemReqDto lostItemReqDto) {
        lostItemService.create(festivalId, lostItemReqDto);
    }

    @Operation(summary = "분실물 수정", description = "분실물 정보를 수정합니다.")
    @PutMapping("/lost-items")
    public LostItemResDto update(
            @Min(1) @RequestParam(name = "id") Long id, @Valid @RequestBody LostItemReqDto lostItemReqDto) {
        return lostItemService.update(id, lostItemReqDto);
    }

    @Operation(summary = "분실물 삭제", description = "분실물을 삭제합니다.")
    @DeleteMapping("/lost-items")
    public void delete(@Min(1) @RequestParam(name = "id") Long id) {
        lostItemService.delete(id);
    }

    @Operation(summary = "분실물 상세 조회", description = "분실물 ID로 상세 정보를 조회합니다.")
    @GetMapping("/lost-items/{id}")
    public LostItemResDto getLostItem(@Min(1) @PathVariable(name = "id") Long id) {
        return lostItemService.getLostItem(id);
    }

    @Operation(summary = "분실물 목록 조회", description = "축제의 분실물 목록을 조회합니다.")
    @GetMapping("/{festivalId}/lost-items")
    public List<LostItemSummaryDto> getLostItems(@Min(1) @PathVariable("festivalId") Long festivalId) {
        return lostItemService.getLostItemsByFestivalId(festivalId);
    }
}
