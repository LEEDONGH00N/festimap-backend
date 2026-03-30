package com.halo.eventer.domain.program_reservation.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.halo.eventer.domain.program_reservation.entity.slot.ProgramSlot;
import com.halo.eventer.domain.program_reservation.entity.slot.ProgramSlotType;
import lombok.Getter;

@Getter
public class AdminSlotCalendarResponse {
    private LocalDate rangeStartDate;
    private LocalDate rangeEndDate;
    private List<LocalDate> dates;
    private List<Day> days;

    public static AdminSlotCalendarResponse empty() {
        AdminSlotCalendarResponse r = new AdminSlotCalendarResponse();
        r.dates = List.of();
        r.days = List.of();
        return r;
    }

    public static AdminSlotCalendarResponse of(LocalDate start, LocalDate end, List<LocalDate> dates, List<Day> days) {
        AdminSlotCalendarResponse r = new AdminSlotCalendarResponse();
        r.rangeStartDate = start;
        r.rangeEndDate = end;
        r.dates = dates;
        r.days = days;
        return r;
    }

    @Getter
    public static class Day {
        private LocalDate date;
        private List<Slot> slots;

        public static Day from(LocalDate date, List<ProgramSlot> slots, Map<Long, Integer> patternCapacityMap) {
            Day d = new Day();
            d.date = date;
            d.slots = slots.stream().map(s -> Slot.from(s, patternCapacityMap)).toList();
            return d;
        }
    }

    @Getter
    public static class Slot {
        private Long slotId;
        private ProgramSlotType slotType;
        private LocalDate slotDate;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
        private LocalTime startTime;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Integer durationMinutes;

        // TIME만 내려줌 (pattern.capacity). DATE는 null.
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Integer capacity;

        static Slot from(ProgramSlot s, Map<Long, Integer> patternCapacityMap) {
            Slot dto = new Slot();
            dto.slotId = s.getId();
            dto.slotType = s.getSlotType();
            dto.slotDate = s.getSlotDate();
            dto.startTime = s.getStartTime();
            dto.durationMinutes = s.getDurationMinutes();

            if (dto.slotType == ProgramSlotType.TIME) {
                Long patternId = s.getPattern().getId();
                dto.capacity = patternCapacityMap.get(patternId); // 없으면 null
            } else {
                dto.capacity = null; // DATE: 무제한 → UI에서 안 보여주게
            }
            return dto;
        }
    }
}
