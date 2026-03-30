package com.halo.eventer.domain.program_reservation.controller;

import jakarta.validation.Valid;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.halo.eventer.domain.program_reservation.dto.request.*;
import com.halo.eventer.domain.program_reservation.dto.response.*;
import com.halo.eventer.domain.program_reservation.service.AdditionalFieldService;
import com.halo.eventer.domain.program_reservation.service.AdminProgramService;
import com.halo.eventer.domain.program_reservation.service.FestivalCommonTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/programs")
@RequiredArgsConstructor
@Validated
@Tag(name = "관리자 - 프로그램 예약 관리", description = "프로그램 및 공통 템플릿 관리 API")
public class AdminProgramController {
    private final AdminProgramService adminProgramService;
    private final FestivalCommonTemplateService templateService;
    private final AdditionalFieldService additionalFieldService;

    @PostMapping()
    @Operation(summary = "프로그램 생성", description = "프로그램 생성시 기본으로 미노출 설정됨")
    public ProgramCreateResponse create(
            @RequestParam("festivalId") Long festivalId, @RequestBody @Valid ProgramCreateRequest request) {
        return adminProgramService.create(festivalId, request);
    }

    @PatchMapping("/{programId}/name")
    @Operation(summary = "프로그램 이름 변경", description = "body로 새 이름(name)만 전달")
    public void rename(@PathVariable("programId") Long programId, @RequestBody @Valid ProgramRenameRequest request) {
        adminProgramService.rename(programId, request);
    }

    @DeleteMapping("/{programId}")
    @Operation(summary = "프로그램 삭제")
    public void deleteProgram(@PathVariable("programId") Long programId) {
        adminProgramService.delete(programId);
    }

    @GetMapping()
    @Operation(summary = "프로그램 리스트 조회", description = "프로그램명으로 검색 가능 (선택)")
    public ProgramListResponse getPrograms(
            @RequestParam("festivalId") Long festivalId, @RequestParam(value = "name", required = false) String name) {
        return ProgramListResponse.of(adminProgramService.getList(festivalId, name));
    }

    @GetMapping("/{programId}")
    @Operation(summary = "프로그램 상세 조회", description = "프로그램 정보, 태그/블록, 축제 공통 템플릿 포함")
    public ProgramDetailResponse getProgramDetail(@PathVariable("programId") Long programId) {
        return adminProgramService.getDetail(programId);
    }

    @PatchMapping("/{programId}")
    @Operation(summary = "프로그램 상세 정보 수정", description = "썸네일, 태그, 이용료, 소요시간 등 상세 정보 저장. 태그와 블록은 전체 교체")
    public void updateProgramDetail(
            @PathVariable("programId") Long programId, @RequestBody @Valid ProgramUpdateRequest request) {
        adminProgramService.updateDetail(programId, request);
    }

    @PatchMapping("/{programId}/active")
    @Operation(summary = "프로그램 노출/미노출 토글", description = "toggleActive()를 호출해 isActive 상태를 반전시킵니다.")
    public void toggleProgramActive(@PathVariable("programId") Long programId) {
        adminProgramService.toggleActive(programId);
    }

    @GetMapping("/{programId}/active-info")
    @Operation(summary = "프로그램 노출 일정 조회")
    public ProgramActiveResponse getActiveInfo(@PathVariable("programId") Long programId) {
        return adminProgramService.getActiveInfo(programId);
    }

    @PostMapping("/{programId}/active-info")
    @Operation(
            summary = "프로그램 노출 일정 설정",
            description =
                    "시작/종료 일시를 설정합니다. 예시) activeStartDate=2026-02-01, activeStartTime=10:00, activeEndDate=2026-02-28, activeEndTime=10:00 (HH:mm)")
    public void updateActiveInfo(
            @PathVariable("programId") Long programId, @RequestBody @Valid ProgramActiveInfoRequest request) {
        adminProgramService.updateActiveInfo(programId, request);
    }

    @GetMapping("/templates")
    @Operation(summary = "공통 템플릿 목록 조회 + 대표 썸네일", description = "축제에 등록된 공통 템플릿을 sortOrder 순으로 조회합니다.")
    public TemplateManagementResponse getTemplates(@RequestParam("festivalId") Long festivalId) {
        return templateService.getList(festivalId);
    }

    @PutMapping("/templates")
    @Operation(summary = "공통 템플릿 전체 저장", description = "요청 배열대로 순서를 저장합니다.")
    public void saveAllTemplates(
            @RequestParam("festivalId") Long festivalId, @RequestBody @Valid TemplateSaveAllRequest request) {
        templateService.saveAll(festivalId, request);
    }

    @DeleteMapping("/templates/{templateId}")
    @Operation(summary = "공통 템플릿 삭제")
    public void deleteTemplate(@PathVariable("templateId") Long templateId) {
        templateService.delete(templateId);
    }

    @PostMapping("/{festivalId}/thumbnail")
    @Operation(summary = "행사 대표 썸네일 저장")
    public void saveProgramThumbnail(
            @PathVariable("festivalId") Long festivalId, @RequestBody ProgramThumbnailRequest request) {
        adminProgramService.saveThumbnail(festivalId, request);
    }

    @PostMapping("/{programId}/booking")
    @Operation(summary = "예약 오픈/마감 설정")
    public void updateBookingInfo(
            @PathVariable("programId") Long programId, @RequestBody @Valid ProgramBookingInfoRequest request) {
        adminProgramService.updateBookingInfo(programId, request);
    }

    @GetMapping("/{programId}/booking")
    @Operation(summary = "예약 오픈/마감 조회")
    public ProgramBookingResponse getBookingInfo(@PathVariable("programId") Long programId) {
        return adminProgramService.getBookingInfo(programId);
    }

    @GetMapping("/tags/{tagId}")
    @Operation(summary = "태그 상세 조회", description = "태그 ID로 태그 정보를 조회합니다.")
    public TagResponse getTagInfo(@PathVariable("tagId") Long tagId) {
        return adminProgramService.getTagInfo(tagId);
    }

    @GetMapping("/tags")
    @Operation(summary = "태그 목록 조회", description = "드롭다운용 태그 id, name 목록을 조회합니다.")
    public TagNameListResponse getTagNames() {
        return adminProgramService.getAllTagNames();
    }

    @GetMapping("/{programId}/additional-fields")
    @Operation(summary = "추가 정보 항목 조회", description = "프로그램에 설정된 추가 정보 수집 항목을 조회합니다.")
    public AdditionalFieldListResponse getAdditionalFields(@PathVariable("programId") Long programId) {
        return additionalFieldService.getFields(programId);
    }

    @PutMapping("/{programId}/additional-fields")
    @Operation(summary = "추가 정보 항목 전체 저장", description = "추가 정보 수집 항목을 전체 교체(upsert) 방식으로 저장합니다.")
    public void saveAdditionalFields(
            @PathVariable("programId") Long programId, @RequestBody @Valid AdditionalFieldSaveRequest request) {
        additionalFieldService.saveFields(programId, request);
    }
}
