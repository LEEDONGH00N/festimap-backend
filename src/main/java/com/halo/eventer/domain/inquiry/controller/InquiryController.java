package com.halo.eventer.domain.inquiry.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

import org.springframework.web.bind.annotation.*;

import com.halo.eventer.domain.inquiry.dto.*;
import com.halo.eventer.domain.inquiry.service.InquiryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/inquiries")
@Tag(name = "문의", description = "문의사항 관리 API")
public class InquiryController {

    private final InquiryService inquiryService;

    @Operation(summary = "문의 생성", description = "새로운 문의를 생성합니다.")
    @PostMapping
    public void create(
            @Min(1) @RequestParam("festivalId") Long festivalId,
            @Valid @RequestBody InquiryCreateReqDto inquiryCreateReqDto) {
        inquiryService.create(festivalId, inquiryCreateReqDto);
    }

    @Operation(summary = "문의 목록 조회 (관리자)", description = "관리자용 문의 목록을 No-Offset 방식으로 조회합니다.")
    @GetMapping("/forAdmin")
    public InquiryNoOffsetPageDto getInquiriesForAdmin(
            @Min(1) @RequestParam("festivalId") Long festivalId, @Min(0) @RequestParam("lastId") Long lastId) {
        return inquiryService.getAllInquiryForAdmin(festivalId, lastId);
    }

    @Operation(summary = "문의 상세 조회 (관리자)", description = "관리자용 문의 상세 정보를 조회합니다.")
    @GetMapping("/forAdmin/{inquiryId}")
    public InquiryResDto getInquiryForAdmin(@Min(1) @PathVariable("inquiryId") Long id) {
        return inquiryService.findInquiryForAdmin(id);
    }

    @Operation(summary = "문의 답변 수정", description = "문의에 대한 답변을 수정합니다.")
    @PatchMapping("/forAdmin")
    public InquiryResDto updateInquiryAnswer(
            @Min(1) @RequestParam("inquiryId") Long inquiryId, @Valid @RequestBody InquiryAnswerReqDto answerReqDto) {
        return inquiryService.updateInquiryAnswer(inquiryId, answerReqDto);
    }

    @Operation(summary = "문의 삭제", description = "문의를 삭제합니다.")
    @DeleteMapping("/forAdmin")
    public void delete(@Min(1) @RequestParam("inquiryId") Long id) {
        inquiryService.delete(id);
    }

    @Operation(summary = "문의 목록 조회 (사용자)", description = "사용자용 문의 목록을 No-Offset 방식으로 조회합니다.")
    @GetMapping("/forUser")
    public InquiryNoOffsetPageDto getAllInquiresForUser(
            @Min(1) @RequestParam("festivalId") Long festivalId, @Min(0) @RequestParam("lastId") Long lastId) {
        return inquiryService.getAllInquiryForUser(festivalId, lastId);
    }

    @Operation(summary = "문의 상세 조회 (사용자)", description = "사용자용 문의 상세 정보를 조회합니다. 비밀번호 검증이 필요합니다.")
    @PostMapping("/forUser/{inquiryId}")
    public InquiryResDto getInquiryForUser(
            @Min(1) @PathVariable("inquiryId") Long id, @Valid @RequestBody InquiryUserReqDto dto) {
        return inquiryService.getInquiryForUser(id, dto);
    }
}
