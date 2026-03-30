package com.halo.eventer.domain.notice.controller.admin;

import java.util.List;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import org.springframework.web.bind.annotation.*;

import com.halo.eventer.domain.notice.ArticleType;
import com.halo.eventer.domain.notice.dto.*;
import com.halo.eventer.domain.notice.service.NoticeService;
import com.halo.eventer.global.common.page.PagedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/notices")
@Tag(name = "공지사항 관리 (관리자)", description = "관리자용 공지사항/이벤트 관리 API")
public class AdminNoticeController {

    private final NoticeService noticeService;

    @Operation(summary = "공지사항 생성", description = "새로운 공지사항 또는 이벤트를 생성합니다.")
    @PostMapping
    public NoticeResDto create(
            @RequestBody NoticeCreateReqDto noticeCreateReqDto, @RequestParam("festivalId") Long id) {
        return noticeService.create(id, noticeCreateReqDto);
    }

    @Operation(summary = "공지사항 상세 조회", description = "공지사항 ID로 상세 정보를 조회합니다.")
    @GetMapping("/{noticeId}")
    public NoticeResDto getNotice(@PathVariable("noticeId") Long noticeId) {
        return noticeService.getNoticeById(noticeId);
    }

    @Operation(summary = "공지사항 목록 조회", description = "축제의 공지사항 목록을 타입별로 페이징하여 조회합니다.")
    @GetMapping()
    public PagedResponse<NoticeSummaryDto> inquireNoticeList(
            @RequestParam Long festivalId,
            @RequestParam("type") ArticleType type,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam @Min(1) @Max(50) int size) {
        return noticeService.getNoticesByType(festivalId, type, page, size);
    }

    @Operation(summary = "배너 설정", description = "공지사항을 배너로 설정하거나 해제합니다.")
    @PostMapping("/banner")
    public NoticeResDto updateBanner(@RequestParam("noticeId") Long noticeId, @RequestParam("picked") Boolean picked) {
        return noticeService.updatePicked(noticeId, picked);
    }

    @Operation(summary = "배너 목록 조회", description = "배너로 설정된 공지사항 목록을 조회합니다.")
    @GetMapping("/banner")
    public List<PickedNoticeResDto> getPickedBanner(@RequestParam("festivalId") Long festivalId) {
        return noticeService.getPickedNotice(festivalId);
    }

    @Operation(summary = "배너 표시 순서 수정", description = "배너의 표시 순서를 수정합니다.")
    @PatchMapping("/banner")
    public List<PickedNoticeResDto> editDisplayOrder(@RequestBody List<PickedNoticeUpdateDto> noticeUpdateDtos) {
        return noticeService.updateDisplayOrder(noticeUpdateDtos);
    }

    @Operation(summary = "공지사항 수정", description = "공지사항 내용을 수정합니다.")
    @PatchMapping("/{noticeId}")
    public NoticeResDto updateNotice(
            @PathVariable("noticeId") Long id, @RequestBody NoticeUpdateReqDto noticeUpdateReqDto) {
        return noticeService.updateNotice(id, noticeUpdateReqDto);
    }

    @Operation(summary = "공지사항 삭제", description = "공지사항을 삭제합니다.")
    @DeleteMapping("/{noticeId}")
    public void deleteNotice(@PathVariable("noticeId") Long id) {
        noticeService.delete(id);
    }
}
