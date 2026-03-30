package com.halo.eventer.domain.program_reservation.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.halo.eventer.domain.program_reservation.Program;
import com.halo.eventer.domain.program_reservation.entity.content.FestivalCommonTemplate;
import com.halo.eventer.domain.program_reservation.entity.content.ProgramBlock;
import com.halo.eventer.domain.program_reservation.entity.reservation.ProgramReservation;
import com.halo.eventer.domain.program_reservation.entity.reservation.ProgramReservationStatus;
import com.halo.eventer.domain.program_reservation.entity.slot.ProgramSlot;
import lombok.Getter;

@Getter
public class ReservationHoldResponse {
    private Long reservationId;
    private ProgramReservationStatus status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime expiresAt;

    private Integer holdMinutes;
    private Summary summary;

    public static ReservationHoldResponse from(
            ProgramReservation reservation,
            Integer holdMinutes,
            List<ProgramBlock> cautionBlocks,
            List<FestivalCommonTemplate> templates) {
        ReservationHoldResponse r = new ReservationHoldResponse();
        r.reservationId = reservation.getId();
        r.status = reservation.getStatus();
        r.expiresAt = reservation.getHoldExpiresAt();
        r.holdMinutes = holdMinutes;

        ProgramSlot slot = reservation.getSlot();
        Program program = reservation.getProgram();
        r.summary = Summary.of(
                program.getId(),
                slot.getId(),
                program.getName(),
                slot.getSlotDate(),
                slot.getStartTime(),
                program.getDurationTime(),
                reservation.getPeopleCount(),
                program.getPriceAmount());
        return r;
    }

    @Getter
    public static class Summary {
        private Long programId;
        private Long slotId;
        private String name;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        private LocalDate date;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
        private LocalTime startTime;

        private String durationTime;

        private int headcount;

        private int priceAmount;

        public static Summary of(
                Long programId,
                Long slotId,
                String name,
                LocalDate date,
                LocalTime startTime,
                String durationTime,
                int headcount,
                int priceAmount) {
            Summary s = new Summary();
            s.programId = programId;
            s.slotId = slotId;
            s.name = name;
            s.date = date;
            s.startTime = startTime;
            s.durationTime = durationTime;
            s.headcount = headcount;
            s.priceAmount = priceAmount;
            return s;
        }
    }
}
