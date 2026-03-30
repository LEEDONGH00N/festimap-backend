package com.halo.eventer.domain.program_reservation.dto.request;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.halo.eventer.domain.program_reservation.entity.slot.ProgramSlotType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ScheduleTemplateCreateRequest {

    @NotNull
    private ProgramSlotType slotType;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    private Integer durationMinutes;

    @Valid
    private List<PatternRequest> patterns;

    @Getter
    @NoArgsConstructor
    public static class PatternRequest {

        @NotNull
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
        private LocalTime startTime;

        @NotNull
        private Integer durationMinutes;

        @NotNull
        private Integer capacity;
    }
}
