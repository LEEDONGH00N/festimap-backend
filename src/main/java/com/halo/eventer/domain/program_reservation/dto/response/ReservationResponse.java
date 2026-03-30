package com.halo.eventer.domain.program_reservation.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.halo.eventer.domain.program_reservation.entity.content.ProgramTag;
import com.halo.eventer.domain.program_reservation.entity.reservation.ProgramReservation;
import com.halo.eventer.domain.program_reservation.entity.reservation.ProgramReservationStatus;
import com.halo.eventer.domain.program_reservation.entity.slot.ProgramSlotType;
import lombok.Getter;

@Getter
public class ReservationResponse {
    private Long id;
    private Long programId;
    private String name;
    private List<ProgramDetailResponse.TagResponse> tags;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate date;

    private String durationTime;
    private int headCount;
    private int priceAmount;

    private ProgramReservationStatus status;
    private boolean past;

    public static ReservationResponse from(ProgramReservation reservation, List<ProgramTag> programTags) {
        boolean past;
        if (reservation.getSlot().getTemplate().getSlotType() == ProgramSlotType.DATE) {
            LocalDate today = LocalDate.now();
            past = reservation.getSlot().getSlotDate().isBefore(today);
        } else {
            LocalDateTime endDateTime = LocalDateTime.of(
                    reservation.getSlot().getSlotDate(), reservation.getSlot().getStartTime());
            past = !endDateTime.isAfter(LocalDateTime.now());
        }

        ReservationResponse response = new ReservationResponse();
        response.id = reservation.getId();
        response.programId = reservation.getProgram().getId();
        response.name = reservation.getProgram().getName();
        response.tags = programTags.stream()
                .map(ProgramDetailResponse.TagResponse::from)
                .collect(Collectors.toList());
        response.date = reservation.getSlot().getSlotDate();
        response.durationTime = reservation.getProgram().getDurationTime();
        response.headCount = reservation.getPeopleCount();
        response.priceAmount = reservation.getProgram().getPriceAmount() * reservation.getPeopleCount();
        response.status = reservation.getStatus();
        response.past = past;
        return response;
    }
}
