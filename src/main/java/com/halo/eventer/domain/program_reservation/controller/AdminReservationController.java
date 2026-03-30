package com.halo.eventer.domain.program_reservation.controller;

import java.time.LocalDate;
import java.util.List;
import jakarta.validation.Valid;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.halo.eventer.domain.program_reservation.dto.request.AdminCancelRequest;
import com.halo.eventer.domain.program_reservation.dto.request.ScheduleTemplateCreateRequest;
import com.halo.eventer.domain.program_reservation.dto.request.ScheduleTemplateUpdateRequest;
import com.halo.eventer.domain.program_reservation.dto.response.*;
import com.halo.eventer.domain.program_reservation.dto.response.AdminAdditionalFieldSummaryResponse;
import com.halo.eventer.domain.program_reservation.entity.reservation.ProgramReservationStatus;
import com.halo.eventer.domain.program_reservation.entity.reservation.ReservationSearchField;
import com.halo.eventer.domain.program_reservation.service.AdminReservationService;
import com.halo.eventer.domain.program_reservation.service.ReservationExcelExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/reservations")
@RequiredArgsConstructor
@Validated
@Tag(name = "관리자 - 프로그램 예약 관리", description = "예약 회차 관리 API")
public class AdminReservationController {
    private final AdminReservationService adminReservationService;
    private final ReservationExcelExportService reservationExcelExportService;

    /** GET 전체 예약 리스트 (8개씩 페이징처리) */
    @GetMapping()
    @Operation(
            summary = "예약 목록 조회 (대시보드)",
            description = "예약 관리 대시보드용 예약 목록을 조회합니다. " + "상태: CONFIRMED(확정), CANCELED(취소)")
    public AdminReservationPageResponse getReservations(
            @Parameter(description = "축제 ID") @RequestParam Long festivalId,
            @Parameter(description = "프로그램 ID (선택)") @RequestParam(required = false) Long programId,
            @Parameter(description = "검색 대상 (선택)") @RequestParam(required = false) ReservationSearchField searchField,
            @Parameter(description = "검색어 (선택)") @RequestParam(required = false) String keyword,
            @Parameter(description = "예약 상태 필터 (선택)") @RequestParam(required = false) ProgramReservationStatus status,
            @ParameterObject @PageableDefault(size = 8, sort = "createdAt", direction = Sort.Direction.DESC)
                    Pageable pageable) {
        return adminReservationService.getReservations(festivalId, programId, searchField, keyword, status, pageable);
    }

    /** GET 축제 내 추가 정보 항목 요약 */
    @GetMapping("/additional-fields")
    @Operation(summary = "축제 내 추가 정보 항목 요약 조회", description = "축제에 속한 모든 프로그램의 활성 추가 정보 항목(컬럼 헤더)을 조회합니다.")
    public AdminAdditionalFieldSummaryResponse getAdditionalFieldSummary(
            @Parameter(description = "축제 ID") @RequestParam Long festivalId) {
        return adminReservationService.getAdditionalFieldSummary(festivalId);
    }

    /** POST 선택 예약 취소 */
    @PostMapping("/cancel")
    @Operation(summary = "선택 예약 취소", description = "관리자가 선택한 예약들을 일괄 취소합니다.")
    public AdminCancelResponse cancelReservations(@RequestBody @Valid AdminCancelRequest request) {
        return adminReservationService.cancelReservations(request.getReservationIds());
    }

    /** GET 예약 회차 캘린더 조회*/
    @GetMapping("/programs/{programId}/slots/calendar")
    @Operation(summary = "예약 회차 캘린더 조회(관리자)", description = "관리자 예약 회차 관리 화면에서 사용하는 날짜별 슬롯 목록을 조회합니다.")
    public AdminSlotCalendarResponse getAdminSlotCalendar(@PathVariable Long programId) {
        return adminReservationService.getAdminSlotCalendar(programId);
    }

    /** GET 기간 카드 목록 조회 */
    @GetMapping("/programs/{programId}/schedule-templates")
    @Operation(summary = "기간 카드 목록 조회", description = "특정 프로그램의 스케줄 템플릿 카드 리스트를 조회합니다.")
    public List<ScheduleTemplateListResponse> getTemplates(@PathVariable("programId") Long programId) {
        return adminReservationService.getTemplates(programId);
    }

    /** GET 상세 조회 (편집 로딩) */
    @GetMapping("/schedule-templates/{templateId}")
    @Operation(summary = "템플릿 상세 조회", description = "편집 모달에서 사용할 템플릿 설정값 전체를 조회합니다.")
    public ScheduleTemplateDetailResponse getTemplateDetail(@PathVariable("templateId") Long templateId) {
        return adminReservationService.getTemplateDetail(templateId);
    }

    /** POST 템플릿 생성 (기간 카드 추가) */
    @PostMapping("/programs/{programId}/schedule-templates")
    @Operation(summary = "템플릿 생성", description = "프로그램에 새 기간 카드(스케줄 템플릿)를 추가합니다.")
    public void createTemplate(
            @PathVariable("programId") Long programId, @RequestBody @Valid ScheduleTemplateCreateRequest request) {
        adminReservationService.createTemplate(programId, request);
    }

    /** PUT 템플릿 저장 및 수정 */
    @PutMapping("/schedule-templates/{templateId}")
    @Operation(summary = "템플릿 저장/수정", description = "모달에서 기간/회차/정원 변경 저장. 예약 존재 시 부분 성공 정책 적용")
    public ScheduleTemplateUpdateResponse updateTemplate(
            @PathVariable("templateId") Long templateId, @RequestBody @Valid ScheduleTemplateUpdateRequest request) {
        return adminReservationService.updateTemplate(templateId, request);
    }

    /** DELETE 템플릿 삭제; 전체 실패 */
    @DeleteMapping("/schedule-templates/{templateId}")
    @Operation(summary = "템플릿 삭제", description = "예약 이력이 있으면 409, 없으면 템플릿/패턴/슬롯을 일괄 삭제")
    public void deleteTemplate(@PathVariable("templateId") Long templateId) {
        adminReservationService.deleteTemplate(templateId);
    }

    /** 엑셀 다운 */
    @GetMapping(value = "/excel", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    @Operation(summary = "지정 범위 내 예약 내역 엑셀 다운로드")
    public ResponseEntity<Resource> download(
            @Parameter(description = "축제 ID") @RequestParam Long festivalId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return reservationExcelExportService.export(new ReservationExcelExportService.Condition(festivalId, from, to));
    }
}
