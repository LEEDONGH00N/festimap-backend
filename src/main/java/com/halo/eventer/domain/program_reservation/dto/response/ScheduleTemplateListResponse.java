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
public class ScheduleTemplateListResponse {
    private Long templateId;
    private ProgramSlotType slotType;
    private LocalDate startDate;
    private LocalDate endDate;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer durationMinutes;

    private int patternCount;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<PatternSummary> patterns;

    public static ScheduleTemplateListResponse of(ProgramScheduleTemplate template, List<ProgramTimePattern> patterns) {
        ScheduleTemplateListResponse r = new ScheduleTemplateListResponse();
        r.templateId = template.getId();
        r.slotType = template.getSlotType();
        r.startDate = template.getStartDate();
        r.endDate = template.getEndDate();
        r.durationMinutes = template.getDurationMinutes();
        r.patternCount = patterns.size();
        r.patterns = patterns.stream().map(PatternSummary::from).toList();
        return r;
    }

    @Getter
    public static class PatternSummary {
        private Long patternId;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
        private LocalTime startTime;

        private Integer durationMinutes;
        private Integer capacity;

        static PatternSummary from(ProgramTimePattern p) {
            PatternSummary s = new PatternSummary();
            s.patternId = p.getId();
            s.startTime = p.getStartTime();
            s.durationMinutes = p.getDurationMinutes();
            s.capacity = p.getCapacity();
            return s;
        }
    }
}
