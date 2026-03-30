package com.halo.eventer.domain.program_reservation.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.halo.eventer.domain.program_reservation.entity.slot.ProgramScheduleTemplate;
import com.halo.eventer.domain.program_reservation.entity.slot.ProgramSlotType;
import com.halo.eventer.domain.program_reservation.entity.slot.ProgramTimePattern;
import lombok.Getter;

@Getter
public class ScheduleTemplateDetailResponse {
    private Long templateId;
    private ProgramSlotType slotType;
    private LocalDate startDate;
    private LocalDate endDate;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer durationMinutes;

    private boolean hasReservation;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<PatternDetail> patterns;

    public static ScheduleTemplateDetailResponse of(
            ProgramScheduleTemplate template, List<ProgramTimePattern> patterns, boolean hasReservation) {
        ScheduleTemplateDetailResponse r = new ScheduleTemplateDetailResponse();
        r.templateId = template.getId();
        r.slotType = template.getSlotType();
        r.startDate = template.getStartDate();
        r.endDate = template.getEndDate();
        r.durationMinutes = template.getDurationMinutes();
        r.hasReservation = hasReservation;
        r.patterns = patterns.stream().map(PatternDetail::from).toList();
        return r;
    }

    @Getter
    public static class PatternDetail {
        private Long patternId;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
        private LocalTime startTime;

        private Integer durationMinutes;
        private Integer capacity;

        static PatternDetail from(ProgramTimePattern p) {
            PatternDetail d = new PatternDetail();
            d.patternId = p.getId();
            d.startTime = p.getStartTime();
            d.durationMinutes = p.getDurationMinutes();
            d.capacity = p.getCapacity();
            return d;
        }
    }
}
