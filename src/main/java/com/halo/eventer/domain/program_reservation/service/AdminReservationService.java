package com.halo.eventer.domain.program_reservation.service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import jakarta.persistence.criteria.JoinType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.halo.eventer.domain.program_reservation.*;
import com.halo.eventer.domain.program_reservation.dto.request.ScheduleTemplateCreateRequest;
import com.halo.eventer.domain.program_reservation.dto.request.ScheduleTemplateUpdateRequest;
import com.halo.eventer.domain.program_reservation.dto.response.*;
import com.halo.eventer.domain.program_reservation.dto.response.ScheduleTemplateUpdateResponse.*;
import com.halo.eventer.domain.program_reservation.entity.additional.ProgramAdditionalField;
import com.halo.eventer.domain.program_reservation.entity.additional.ProgramReservationAdditionalAnswer;
import com.halo.eventer.domain.program_reservation.entity.reservation.ProgramReservation;
import com.halo.eventer.domain.program_reservation.entity.reservation.ProgramReservationStatus;
import com.halo.eventer.domain.program_reservation.entity.reservation.ReservationSearchField;
import com.halo.eventer.domain.program_reservation.entity.slot.ProgramScheduleTemplate;
import com.halo.eventer.domain.program_reservation.entity.slot.ProgramSlot;
import com.halo.eventer.domain.program_reservation.entity.slot.ProgramSlotType;
import com.halo.eventer.domain.program_reservation.entity.slot.ProgramTimePattern;
import com.halo.eventer.domain.program_reservation.repository.*;
import com.halo.eventer.global.error.ErrorCode;
import com.halo.eventer.global.error.exception.BaseException;
import lombok.RequiredArgsConstructor;

import static com.halo.eventer.domain.program_reservation.entity.reservation.ProgramReservationStatus.*;

@Service
@RequiredArgsConstructor
public class AdminReservationService {
    private final ProgramRepository programRepository;
    private final ProgramScheduleTemplateRepository templateRepository;
    private final ProgramTimePatternRepository patternRepository;
    private final ProgramSlotRepository slotRepository;
    private final ProgramReservationRepository reservationRepository;
    private final ProgramAdditionalFieldRepository additionalFieldRepository;
    private final ProgramReservationAdditionalAnswerRepository additionalAnswerRepository;

    /** 예약 정보 조회 (예약 관리 대시보드) */
    @Transactional(readOnly = true)
    public AdminReservationPageResponse getReservations(
            Long festivalId,
            Long programId,
            ReservationSearchField searchField,
            String keyword,
            ProgramReservationStatus status,
            Pageable pageable) {
        Specification<ProgramReservation> spec = (root, query, cb) -> {
            // count쿼리/중복 방지
            assert query != null;
            query.distinct(true);

            var programJoin = root.join("program", JoinType.INNER);
            var predicate = cb.conjunction();

            // 축제 필터
            predicate = cb.and(predicate, cb.equal(programJoin.get("festival").get("id"), festivalId));

            // 프로그램 필터
            if (programId != null) {
                predicate = cb.and(predicate, cb.equal(programJoin.get("id"), programId));
            }

            // HOLD, EXPIRED 상태는 항상 제외
            predicate = cb.and(predicate, cb.notEqual(root.get("status"), HOLD));
            predicate = cb.and(predicate, cb.notEqual(root.get("status"), EXPIRED));

            // 상태 필터 (HOLD, EXPIRED 제외된 상태에서 추가 필터)
            if (status != null && status != HOLD && status != EXPIRED) {
                predicate = cb.and(predicate, cb.equal(root.get("status"), status));
            }

            // 2) 검색어 필터
            if (keyword != null && !keyword.isBlank()) {
                String kw = keyword.trim();

                // searchField가 없으면 "프로그램명 + 신청자명 + 신청자전화 + 방문자명 + 방문자전화" 통합검색으로 처리
                if (searchField == null) {
                    String like = "%" + kw.toLowerCase() + "%";
                    var p1 = cb.like(cb.lower(programJoin.get("name")), like);
                    var p2 = cb.like(cb.lower(root.get("bookerName")), like);
                    var p3 = cb.like(cb.lower(root.get("bookerPhone")), like);
                    var p4 = cb.like(cb.lower(root.get("visitorName")), like);
                    var p5 = cb.like(cb.lower(root.get("visitorPhone")), like);

                    predicate = cb.and(predicate, cb.or(p1, p2, p3, p4, p5));
                } else {
                    // 지정 필드 검색
                    switch (searchField) {
                        case PROGRAM_NAME -> {
                            String like = "%" + kw.toLowerCase() + "%";
                            predicate = cb.and(predicate, cb.like(cb.lower(programJoin.get("name")), like));
                        }
                        case BOOKER_NAME -> {
                            String like = "%" + kw.toLowerCase() + "%";
                            predicate = cb.and(predicate, cb.like(cb.lower(root.get("bookerName")), like));
                        }
                        case VISITOR_NAME -> {
                            String like = "%" + kw.toLowerCase() + "%";
                            predicate = cb.and(predicate, cb.like(cb.lower(root.get("visitorName")), like));
                        }
                        case BOOKER_PHONE -> {
                            String normalized = kw.replace("-", "");
                            String like1 = "%" + kw.toLowerCase() + "%";
                            String like2 = "%" + normalized.toLowerCase() + "%";
                            var p1 = cb.like(cb.lower(root.get("bookerPhone")), like1);
                            var p2 = cb.like(cb.lower(root.get("bookerPhone")), like2);
                            predicate = cb.and(predicate, cb.or(p1, p2));
                        }
                        case VISITOR_PHONE -> {
                            String normalized = kw.replace("-", "");
                            String like1 = "%" + kw.toLowerCase() + "%";
                            String like2 = "%" + normalized.toLowerCase() + "%";
                            var p1 = cb.like(cb.lower(root.get("visitorPhone")), like1);
                            var p2 = cb.like(cb.lower(root.get("visitorPhone")), like2);
                            predicate = cb.and(predicate, cb.or(p1, p2));
                        }
                    }
                }
            }

            return predicate;
        };

        Page<ProgramReservation> page = reservationRepository.findAll(spec, pageable);

        List<AdminReservationResponse> adminReservations =
                page.getContent().stream().map(AdminReservationResponse::from).collect(Collectors.toList());

        // 추가 정보 답변 매핑
        List<Long> reservationIds =
                page.getContent().stream().map(ProgramReservation::getId).collect(Collectors.toList());
        if (!reservationIds.isEmpty()) {
            List<ProgramReservationAdditionalAnswer> answers =
                    additionalAnswerRepository.findAllByReservationIdIn(reservationIds);

            Map<Long, List<AdditionalAnswerResponse>> answerMap = answers.stream()
                    .collect(Collectors.groupingBy(
                            a -> a.getReservation().getId(),
                            Collectors.mapping(AdditionalAnswerResponse::from, Collectors.toList())));

            adminReservations.forEach(r -> r.setAdditionalAnswers(answerMap.getOrDefault(r.getId(), List.of())));
        }

        return AdminReservationPageResponse.of(adminReservations, page);
    }

    /** 축제 내 전체 프로그램의 추가 정보 항목 요약 조회 */
    @Transactional(readOnly = true)
    public AdminAdditionalFieldSummaryResponse getAdditionalFieldSummary(Long festivalId) {
        List<ProgramAdditionalField> fields = additionalFieldRepository.findAllByFestivalId(festivalId);
        return AdminAdditionalFieldSummaryResponse.from(fields);
    }

    /** 관리자 선택 예약 취소 */
    @Transactional
    public AdminCancelResponse cancelReservations(List<Long> reservationIds) {
        List<Long> canceledIds = new ArrayList<>();

        for (Long id : reservationIds) {
            ProgramReservation reservation = reservationRepository
                    .findByIdForUpdate(id)
                    .orElseThrow(() -> new BaseException("예약을 찾을 수 없습니다. id=" + id, ErrorCode.ENTITY_NOT_FOUND));

            // 멱등: 이미 취소면 같은 결과 반환
            if (reservation.getStatus() == CANCELED) {
                canceledIds.add(id);
                continue;
            }

            // 취소 가능 상태 제한: CONFIRMED만
            if (reservation.getStatus() != CONFIRMED) {
                throw new BaseException("취소할 수 없는 예약 상태입니다. id=" + id, ErrorCode.INVALID_INPUT_VALUE);
            }

            reservation.cancel();

            // slot도 FOR UPDATE로 잡고 복구 (정합성)
            ProgramSlot slot = slotRepository
                    .findByIdAndProgramIdForUpdate(
                            reservation.getSlot().getId(),
                            reservation.getProgram().getId())
                    .orElseThrow(() -> new BaseException("슬롯을 찾을 수 없습니다.", ErrorCode.ENTITY_NOT_FOUND));

            slot.increaseCapacity(reservation.getHeadcount());
            canceledIds.add(id);
        }

        return new AdminCancelResponse(canceledIds);
    }

    /** 다건 예약 상태 변경 (선택 승인, 선택 거절) */
    @Transactional(readOnly = true)
    public AdminSlotCalendarResponse getAdminSlotCalendar(Long programId) {
        List<ProgramSlot> slots = slotRepository.findAllByProgramIdOrderBySlotDateAscStartTimeAsc(programId);

        if (slots.isEmpty()) return AdminSlotCalendarResponse.empty();

        LocalDate rangeStart = slots.get(0).getSlotDate();
        LocalDate rangeEnd = slots.get(slots.size() - 1).getSlotDate();

        // TIME 슬롯의 patternId 수집
        List<Long> patternIds = slots.stream()
                .filter(s -> s.getSlotType() == ProgramSlotType.TIME)
                .map(s -> s.getPattern().getId())
                .distinct()
                .toList();

        Map<Long, Integer> patternCapacityMap = patternIds.isEmpty()
                ? java.util.Collections.emptyMap()
                : patternRepository.findAllById(patternIds).stream()
                        .collect(java.util.stream.Collectors.toMap(
                                ProgramTimePattern::getId, ProgramTimePattern::getCapacity));

        Map<LocalDate, List<ProgramSlot>> byDate = slots.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        ProgramSlot::getSlotDate, java.util.LinkedHashMap::new, java.util.stream.Collectors.toList()));

        List<LocalDate> dates = new ArrayList<>(byDate.keySet());

        List<AdminSlotCalendarResponse.Day> days = byDate.entrySet().stream()
                .map(e -> AdminSlotCalendarResponse.Day.from(e.getKey(), e.getValue(), patternCapacityMap))
                .toList();

        return AdminSlotCalendarResponse.of(rangeStart, rangeEnd, dates, days);
    }

    @Transactional(readOnly = true)
    public List<ScheduleTemplateListResponse> getTemplates(Long programId) {
        List<ProgramScheduleTemplate> templates = templateRepository.findAllByProgramIdOrderByStartDate(programId);

        return templates.stream()
                .map(template -> {
                    List<ProgramTimePattern> patterns =
                            patternRepository.findAllByTemplateIdOrderBySortOrder(template.getId());
                    return ScheduleTemplateListResponse.of(template, patterns);
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public ScheduleTemplateDetailResponse getTemplateDetail(Long templateId) {
        ProgramScheduleTemplate template = templateRepository
                .findById(templateId)
                .orElseThrow(() -> new BaseException("존재하지 않는 템플릿입니다.", ErrorCode.ENTITY_NOT_FOUND));

        List<ProgramTimePattern> patterns = patternRepository.findAllByTemplateIdOrderBySortOrder(template.getId());
        boolean hasReservation = reservationRepository.existsAnyByTemplateId(template.getId());

        return ScheduleTemplateDetailResponse.of(template, patterns, hasReservation);
    }

    @Transactional
    public void createTemplate(Long programId, ScheduleTemplateCreateRequest request) {
        Program program = programRepository
                .findById(programId)
                .orElseThrow(() -> new BaseException("존재하지 않는 프로그램입니다.", ErrorCode.ENTITY_NOT_FOUND));
        validateSlotTypeFields(
                request.getSlotType(),
                request.getDurationMinutes(),
                request.getPatterns() != null && !request.getPatterns().isEmpty());

        Integer templateDuration = request.getSlotType() == ProgramSlotType.DATE ? request.getDurationMinutes() : null;
        ProgramScheduleTemplate template = ProgramScheduleTemplate.of(
                program, request.getStartDate(), request.getEndDate(), request.getSlotType(), templateDuration);
        templateRepository.save(template);

        if (request.getSlotType() == ProgramSlotType.DATE) {
            createDateSlots(template, request.getStartDate(), request.getEndDate(), request.getDurationMinutes());
        } else {
            createTimeSlotsFromCreateRequest(template, request);
        }
    }

    @Transactional
    public ScheduleTemplateUpdateResponse updateTemplate(Long templateId, ScheduleTemplateUpdateRequest request) {
        ProgramScheduleTemplate template = templateRepository
                .findById(templateId)
                .orElseThrow(() -> new BaseException("존재하지 않는 템플릿입니다.", ErrorCode.ENTITY_NOT_FOUND));

        boolean hasAny = reservationRepository.existsAnyByTemplateId(templateId); // status 무관
        boolean hasActive =
                reservationRepository.countByTemplateIdAndStatusIn(templateId, List.of(HOLD, CONFIRMED)) > 0;
        if (!hasAny) {
            return replaceTemplate(template, request);
        }
        if (hasActive) {
            return updateTemplateWithReservation(template, request);
        }
        // active 예약은 없지만 히스토리(expired/canceled) 존재
        return updateTemplateWithReservation(template, request);
    }

    @Transactional
    public void deleteTemplate(Long templateId) {
        ProgramScheduleTemplate template = templateRepository
                .findById(templateId)
                .orElseThrow(() -> new BaseException("존재하지 않는 템플릿입니다.", ErrorCode.ENTITY_NOT_FOUND));
        if (reservationRepository.existsAnyByTemplateId(templateId))
            throw new BaseException("예약 이력이 있는 템플릿은 삭제할 수 없습니다.", ErrorCode.ACTIVE_RESERVATION_EXISTS);

        slotRepository.deleteAllByTemplateId(templateId);
        patternRepository.deleteAllByTemplateId(templateId);
        templateRepository.delete(template);
    }

    /**
     * A) 예약 없음 → 전체 교체(replace)
     */
    private ScheduleTemplateUpdateResponse replaceTemplate(
            ProgramScheduleTemplate template, ScheduleTemplateUpdateRequest request) {
        validateSlotTypeFields(
                request.getSlotType(),
                request.getDurationMinutes(),
                request.getPatterns() != null && !request.getPatterns().isEmpty());

        slotRepository.deleteAllByTemplateId(template.getId());
        slotRepository.flush();
        patternRepository.deleteAllByTemplateId(template.getId());
        patternRepository.flush();

        Integer templateDuration = request.getSlotType() == ProgramSlotType.DATE ? request.getDurationMinutes() : null;
        template.update(request.getStartDate(), request.getEndDate(), request.getSlotType(), templateDuration);

        if (request.getSlotType() == ProgramSlotType.DATE) {
            createDateSlots(template, request.getStartDate(), request.getEndDate(), request.getDurationMinutes());
        } else {
            createTimeSlotsWithPatterns(template, request);
        }

        return ScheduleTemplateUpdateResponse.success(template.getId());
    }

    /**
     * B) 예약 있음 → 제한적 변경
     */
    private ScheduleTemplateUpdateResponse updateTemplateWithReservation(
            ProgramScheduleTemplate template, ScheduleTemplateUpdateRequest request) {
        if (!template.getStartDate().equals(request.getStartDate())
                || !template.getEndDate().equals(request.getEndDate())) {
            throw new BaseException("예약이 있는 템플릿은 기간을 변경할 수 없습니다.", ErrorCode.INVALID_INPUT_VALUE);
        }

        if (template.getSlotType() != request.getSlotType()) {
            throw new BaseException("예약이 있는 템플릿은 타입을 변경할 수 없습니다.", ErrorCode.INVALID_INPUT_VALUE);
        }

        if (template.getSlotType() == ProgramSlotType.DATE) {
            return updateDateTemplateWithReservation(template, request);
        } else {
            return updateTimeTemplateWithReservation(template, request);
        }
    }

    /**
     * B-DATE) 예약 있으면 durationMinutes 변경 불가
     */
    private ScheduleTemplateUpdateResponse updateDateTemplateWithReservation(
            ProgramScheduleTemplate template, ScheduleTemplateUpdateRequest request) {
        if (!Objects.equals(template.getDurationMinutes(), request.getDurationMinutes())) {
            throw new BaseException("예약이 있는 DATE 템플릿은 소요시간을 변경할 수 없습니다.", ErrorCode.INVALID_INPUT_VALUE);
        }

        return ScheduleTemplateUpdateResponse.success(template.getId());
    }

    /**
     * B-TIME) capacity만 부분 변경 허용
     */
    private ScheduleTemplateUpdateResponse updateTimeTemplateWithReservation(
            ProgramScheduleTemplate template, ScheduleTemplateUpdateRequest request) {
        List<ProgramTimePattern> existingPatterns =
                patternRepository.findAllByTemplateIdOrderBySortOrder(template.getId());
        Map<Long, ProgramTimePattern> existingMap =
                existingPatterns.stream().collect(Collectors.toMap(ProgramTimePattern::getId, p -> p));

        List<ScheduleTemplateUpdateRequest.PatternRequest> requestPatterns =
                request.getPatterns() == null ? List.of() : request.getPatterns();

        if (requestPatterns.size() != existingPatterns.size()) {
            throw new BaseException("예약이 있는 회차는 패턴을 추가/삭제할 수 없습니다.", ErrorCode.INVALID_INPUT_VALUE);
        }
        Set<Long> reqIds = requestPatterns.stream()
                .map(ScheduleTemplateUpdateRequest.PatternRequest::getPatternId)
                .collect(Collectors.toSet());
        Set<Long> existIds =
                existingPatterns.stream().map(ProgramTimePattern::getId).collect(Collectors.toSet());
        if (!reqIds.equals(existIds)) {
            throw new BaseException("예약이 있는 회차는 패턴을 추가/삭제할 수 없습니다.", ErrorCode.INVALID_INPUT_VALUE);
        }

        List<UpdatedPattern> updated = new ArrayList<>();
        List<RejectedPattern> rejected = new ArrayList<>();

        for (ScheduleTemplateUpdateRequest.PatternRequest pr : requestPatterns) {
            ProgramTimePattern existing = existingMap.get(pr.getPatternId());
            if (existing == null) {
                throw new BaseException("예약이 있는 회차는 패턴을 추가/삭제할 수 없습니다.", ErrorCode.INVALID_INPUT_VALUE);
            }

            boolean fieldChanged = !existing.getStartTime().equals(pr.getStartTime())
                    || !existing.getDurationMinutes().equals(pr.getDurationMinutes());

            if (fieldChanged) {
                rejected.add(new RejectedPattern(
                        existing.getId(),
                        existing.getStartTime(),
                        "FIELD_CHANGE_NOT_ALLOWED",
                        "예약이 있는 회차는 시간/소요시간/추가/삭제를 변경할 수 없습니다.",
                        null));
                continue;
            }

            if (!existing.getCapacity().equals(pr.getCapacity())) {
                CapacityUpdateResult result = updateSlotCapacities(existing, pr.getCapacity());
                if (result.updatedCount > 0) {
                    updated.add(new UpdatedPattern(
                            existing.getId(), existing.getStartTime(), pr.getCapacity(), result.updatedCount));
                }
                if (!result.failedSlots.isEmpty()) {
                    rejected.add(new RejectedPattern(
                            existing.getId(),
                            existing.getStartTime(),
                            "BOOKED_EXCEEDS_NEW_CAPACITY",
                            "예약 인원보다 작은 정원으로는 변경할 수 없습니다.",
                            result.failedSlots));
                }
            }
        }

        return ScheduleTemplateUpdateResponse.partial(template.getId(), updated, rejected);
    }

    private CapacityUpdateResult updateSlotCapacities(ProgramTimePattern pattern, Integer newCapacity) {
        List<ProgramSlot> slots = slotRepository.findAllByPatternIdForUpdate(pattern.getId());
        int updatedCount = 0;
        List<FailedSlot> failedSlots = new ArrayList<>();

        for (ProgramSlot slot : slots) {
            int booked = slot.getBooked(); // capacity - remaining
            if (newCapacity < booked) {
                failedSlots.add(new FailedSlot(slot.getSlotDate(), booked, newCapacity));
                continue;
            }

            boolean changed = slot.updateCapacity(newCapacity);
            if (changed) updatedCount++;
        }

        pattern.updateCapacity(newCapacity);
        return new CapacityUpdateResult(updatedCount, failedSlots);
    }

    private void createTimeSlotsFromCreateRequest(
            ProgramScheduleTemplate template, ScheduleTemplateCreateRequest request) {
        Program program = template.getProgram();
        List<ScheduleTemplateCreateRequest.PatternRequest> patterns = request.getPatterns();
        for (int i = 0; i < patterns.size(); i++) {
            ScheduleTemplateCreateRequest.PatternRequest pr = patterns.get(i);
            ProgramTimePattern pattern =
                    ProgramTimePattern.of(template, pr.getStartTime(), pr.getDurationMinutes(), pr.getCapacity(), i);
            patternRepository.save(pattern);

            for (LocalDate date = request.getStartDate();
                    !date.isAfter(request.getEndDate());
                    date = date.plusDays(1)) {
                slotRepository.save(ProgramSlot.ofTime(
                        program,
                        template,
                        pattern,
                        date,
                        pr.getStartTime(),
                        pr.getDurationMinutes(),
                        pr.getCapacity()));
            }
        }
    }

    private void createDateSlots(
            ProgramScheduleTemplate template, LocalDate start, LocalDate end, Integer durationMinutes) {
        Program program = template.getProgram();
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            slotRepository.save(ProgramSlot.ofDate(program, template, date, durationMinutes));
        }
    }

    private void createTimeSlotsWithPatterns(ProgramScheduleTemplate template, ScheduleTemplateUpdateRequest request) {
        Program program = template.getProgram();
        List<ScheduleTemplateUpdateRequest.PatternRequest> patterns = request.getPatterns();
        for (int i = 0; i < patterns.size(); i++) {
            ScheduleTemplateUpdateRequest.PatternRequest pr = patterns.get(i);
            ProgramTimePattern pattern =
                    ProgramTimePattern.of(template, pr.getStartTime(), pr.getDurationMinutes(), pr.getCapacity(), i);
            patternRepository.save(pattern);

            for (LocalDate date = request.getStartDate();
                    !date.isAfter(request.getEndDate());
                    date = date.plusDays(1)) {
                slotRepository.save(ProgramSlot.ofTime(
                        program,
                        template,
                        pattern,
                        date,
                        pr.getStartTime(),
                        pr.getDurationMinutes(),
                        pr.getCapacity()));
            }
        }
    }

    private void validateSlotTypeFields(ProgramSlotType slotType, Integer durationMinutes, boolean hasPatterns) {
        if (slotType == ProgramSlotType.DATE && hasPatterns) {
            throw new BaseException("DATE 타입은 패턴을 지정할 수 없습니다.", ErrorCode.INVALID_INPUT_VALUE);
        }
        if (slotType == ProgramSlotType.TIME && durationMinutes != null) {
            throw new BaseException("TIME 타입은 소요시간을 직접 지정할 수 없습니다. 각 패턴에서 지정해주세요.", ErrorCode.INVALID_INPUT_VALUE);
        }
        if (slotType == ProgramSlotType.TIME && !hasPatterns) {
            throw new BaseException("TIME 타입은 최소 1개 이상의 패턴을 지정해야 합니다.", ErrorCode.INVALID_INPUT_VALUE);
        }
    }

    private boolean hasActiveReservation(Long templateId) {
        return reservationRepository.countByTemplateIdAndStatusIn(templateId, List.of(HOLD, CONFIRMED)) > 0;
    }

    private static class CapacityUpdateResult {
        final int updatedCount;
        final List<FailedSlot> failedSlots;

        CapacityUpdateResult(int updatedCount, List<FailedSlot> failedSlots) {
            this.updatedCount = updatedCount;
            this.failedSlots = failedSlots;
        }
    }
}
