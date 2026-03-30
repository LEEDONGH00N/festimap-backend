package com.halo.eventer.domain.program_reservation.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.halo.eventer.domain.program_reservation.entity.slot.ProgramSlot;
import lombok.Getter;

@Getter
public class ReservationSlotListResponse {
    private Long programId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate date;

    private int maxPersonCount;
    private List<SlotItem> slots;

    public static ReservationSlotListResponse of(
            Long programId, LocalDate date, int maxPersonCount, List<SlotItem> slots) {
        ReservationSlotListResponse r = new ReservationSlotListResponse();
        r.programId = programId;
        r.date = date;
        r.maxPersonCount = maxPersonCount;
        r.slots = slots;
        return r;
    }

    @Getter
    public static class SlotItem {
        private Long slotId;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
        private LocalTime startTime;

        private int remaining;

        @JsonProperty("isReservable")
        private boolean reservable;

        public static SlotItem from(ProgramSlot slot) {
            SlotItem s = new SlotItem();
            s.slotId = slot.getId();
            s.startTime = slot.getStartTime();
            s.remaining = slot.getCapacityRemaining() != null ? slot.getCapacityRemaining() : 0;
            s.reservable = s.remaining > 0;
            return s;
        }
    }
}
