package com.halo.eventer.domain.program_reservation.controller;

import java.time.LocalDate;
import jakarta.validation.Valid;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.halo.eventer.domain.program_reservation.dto.request.ReservationConfirmRequest;
import com.halo.eventer.domain.program_reservation.dto.request.ReservationHoldRequest;
import com.halo.eventer.domain.program_reservation.dto.response.*;
import com.halo.eventer.domain.program_reservation.service.AdditionalFieldService;
import com.halo.eventer.domain.program_reservation.service.ProgramReservationService;
import com.halo.eventer.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/programs")
@RequiredArgsConstructor
@Validated
@Tag(name = "사용자 - 프로그램 예약 관리", description = "프로그램 예약 API")
public class ProgramReservationController {
    private final ProgramReservationService programReservationService;
    private final AdditionalFieldService additionalFieldService;

    @GetMapping
    @Operation(summary = "프로그램 리스트 조회", description = "사용자에게 노출 가능한 프로그램 목록을 조회합니다.")
    public AvailableProgramListResponse getPrograms(
            @AuthenticationPrincipal CustomUserDetails userDetails, @RequestParam("festivalId") Long festivalId) {
        Long memberId = userDetails != null ? userDetails.getMemberId() : null;
        return programReservationService.getVisiblePrograms(memberId, festivalId);
    }

    @GetMapping("/{programId}")
    @Operation(summary = "프로그램 단건 조회", description = "특정 프로그램의 상세 정보를 조회합니다.")
    public UserProgramDetailResponse getProgram(
            @AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long programId) {
        Long memberId = userDetails != null ? userDetails.getMemberId() : null;
        return programReservationService.getVisibleProgram(memberId, programId);
    }

    @GetMapping("/{programId}/dates")
    @Operation(summary = "날짜 목록 조회 (모달 1단계)", description = "프로그램의 예약 가능 날짜 목록을 조회합니다.")
    public ReservationDateListResponse getReservationDates(
            @AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long programId) {
        Long memberId = userDetails != null ? userDetails.getMemberId() : null;
        return programReservationService.getReservationDates(memberId, programId);
    }

    @GetMapping("/{programId}/reservation-slots")
    @Operation(summary = "특정 날짜의 슬롯(회차) 목록 조회 (모달 2단계)", description = "선택한 날짜의 예약 가능 회차 목록을 조회합니다.")
    public ReservationSlotListResponse getReservationSlots(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long programId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Long memberId = userDetails != null ? userDetails.getMemberId() : null;
        return programReservationService.getReservationSlots(memberId, programId, date);
    }

    @GetMapping("/{programId}/additional-fields")
    @Operation(summary = "추가 정보 항목 조회", description = "프로그램의 활성화된 추가 정보 항목을 조회합니다.")
    public UserAdditionalFieldListResponse getAdditionalFields(@PathVariable Long programId) {
        return additionalFieldService.getActiveFields(programId);
    }

    @PostMapping("/reservations/holds")
    @Operation(
            summary = "예약 HOLD (모달 3단계)",
            description = "재고 선점 + HOLD 생성. 멱등성을 위해 같은 예약 의도(재시도, 중복 클릭)라면 동일 `Idempotency-Key`를 재사용하세요. "
                    + "새로운 예약 시도마다 새 UUID를 생성하고, 네트워크 재전송/페이지 새로고침 시에는 같은 키를 다시 전송하면 동일 응답을 받습니다.")
    public ReservationHoldResponse holdReservation(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(
                            name = "Idempotency-Key",
                            in = ParameterIn.HEADER,
                            required = true,
                            description = "같은 예약 의도의 재시도에 재사용되는 키. 새로운 시도 시 새 UUID, 재시도 시 동일 키 사용",
                            example = "550e8400-e29b-41d4-a716-446655440000")
                    @RequestHeader("Idempotency-Key")
                    String idempotencyKey,
            @RequestBody @Valid ReservationHoldRequest request) {
        return programReservationService.holdReservation(userDetails.getMemberId(), idempotencyKey, request);
    }

    @PostMapping("/reservations/{reservationId}/checkout")
    @Operation(
            summary = "체크아웃 렌더링(만료 처리 포함)",
            description = "체크아웃 화면 렌더링에 필요한 CAUTION/TEMPLATE를 조회합니다. "
                    + "HOLD가 만료된 경우 서버가 즉시 EXPIRED 처리 및 재고 복구를 수행할 수 있으며, "
                    + "만료 상태에서는 RESERVATION_EXPIRED 에러를 반환합니다. "
                    + "동일 reservationId 재시도 시 결과는 일관되게 반환됩니다.")
    public ReservationCheckoutResponse renderReservationInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable("reservationId") Long reservationId) {
        return programReservationService.getCheckout(userDetails.getMemberId(), reservationId);
    }

    @PostMapping("/reservations/{reservationId}/confirm")
    @Operation(summary = "프로그램 예약 확정", description = "예약 확정하기 버튼")
    public ReservationConfirmResponse confirmReservation(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("reservationId") Long reservationId,
            @RequestBody ReservationConfirmRequest request) {
        return programReservationService.confirmReservation(userDetails.getMemberId(), reservationId, request);
    }

    @GetMapping("/reservations/{reservationId}")
    @Operation(summary = "단건 예약 조회")
    public ReservationResponse getReservationDetail(
            @AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable("reservationId") Long reservationId) {
        return programReservationService.getReservationDetail(userDetails.getMemberId(), reservationId);
    }

    @GetMapping("/{festivalId}/reservations")
    @Operation(summary = "다건 예약 조회")
    public ReservationListResponse getReservations(
            @AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable("festivalId") Long festivalId) {
        return programReservationService.getReservations(userDetails.getMemberId(), festivalId);
    }

    @PostMapping("/reservations/{reservationId}/cancel")
    @Operation(summary = "프로그램 예약 취소")
    public ReservationCancelResponse cancel(
            @AuthenticationPrincipal CustomUserDetails user, @PathVariable Long reservationId) {
        return programReservationService.cancelReservation(user.getMemberId(), reservationId);
    }
}
