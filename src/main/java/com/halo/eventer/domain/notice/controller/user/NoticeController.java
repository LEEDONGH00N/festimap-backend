package com.halo.eventer.domain.notice.controller.user;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.halo.eventer.domain.notice.ArticleType;
import com.halo.eventer.domain.notice.dto.user.UserNoticeNoOffsetPageDto;
import com.halo.eventer.domain.notice.dto.user.UserNoticePageReqDto;
import com.halo.eventer.domain.notice.dto.user.UserNoticeResDto;
import com.halo.eventer.domain.notice.service.NoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "공지사항 (사용자)", description = "사용자용 공지사항/이벤트 조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/notices")
public class NoticeController {

    private final NoticeService noticeService;

    @Operation(summary = "공지사항 상세 조회", description = "공지사항 ID로 상세 정보를 조회합니다.")
    @GetMapping("/{noticeId}")
    public UserNoticeResDto getNoticeForUser(@PathVariable("noticeId") Long noticeId) {
        return noticeService.getNoticeByIdForUser(noticeId);
    }

    @Operation(summary = "공지사항 목록 조회", description = "타입별 공지사항 목록을 No-Offset 방식으로 페이징하여 조회합니다.")
    @GetMapping()
    public UserNoticeNoOffsetPageDto getNotices(
            @RequestParam("festivalId") Long festivalId,
            @RequestParam("type") ArticleType type,
            @Validated @ModelAttribute UserNoticePageReqDto userNoticePageReqDto) {
        return noticeService.getNoticesByTypeWithNoOffsetPaging(
                festivalId,
                type,
                userNoticePageReqDto.getLastUpdatedAt(),
                userNoticePageReqDto.getLastId(),
                userNoticePageReqDto.getSize());
    }
}
