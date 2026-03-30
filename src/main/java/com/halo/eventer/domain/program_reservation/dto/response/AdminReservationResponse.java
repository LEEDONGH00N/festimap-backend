package com.halo.eventer.domain.program_reservation.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.halo.eventer.domain.program_reservation.entity.reservation.ProgramReservation;
import com.halo.eventer.domain.program_reservation.entity.reservation.ProgramReservationStatus;
import com.halo.eventer.domain.program_reservation.entity.slot.ProgramSlotType;
import lombok.Getter;

@Getter
public class AdminReservationResponse {
    private Long id;
    private String programName;

    private String bookerName;
    private String bookerPhone;
    private String visitorName;
    private String visitorPhone;

    private LocalDate slotDate;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime slotStartTime;

    @JsonFormat(pattern = "yy.MM.dd HH:mm")
    private LocalDateTime confirmedAt;

    private int durationMinutes;
    private int peopleCount;
    private int fee; // 일단 0원으로 통일
    private ProgramReservationStatus status;
    private boolean past;
    private List<AdditionalAnswerResponse> additionalAnswers;

    public void setAdditionalAnswers(List<AdditionalAnswerResponse> additionalAnswers) {
        this.additionalAnswers = additionalAnswers;
    }

    public static AdminReservationResponse from(ProgramReservation r) {
        String visitorName = r.getVisitorName();
        String visitorPhone = r.getVisitorPhone();
        if (visitorName == null || visitorPhone == null) {
            visitorName = r.getBookerName();
            visitorPhone = r.getBookerPhone();
        }

        boolean past;
        if (r.getSlot().getTemplate().getSlotType() == ProgramSlotType.DATE) {
            LocalDate today = LocalDate.now();
            past = r.getSlot().getSlotDate().isBefore(today);
        } else {
            LocalDateTime endDateTime =
                    LocalDateTime.of(r.getSlot().getSlotDate(), r.getSlot().getStartTime());
            past = !endDateTime.isAfter(LocalDateTime.now());
        }

        AdminReservationResponse dto = new AdminReservationResponse();
        dto.id = r.getId();
        dto.programName = r.getProgram().getName();
        dto.bookerName = r.getBookerName();
        dto.bookerPhone = r.getBookerPhone();
        dto.visitorName = visitorName;
        dto.visitorPhone = visitorPhone;
        dto.slotDate = r.getSlot().getSlotDate();
        dto.slotStartTime = r.getSlot().getStartTime();
        dto.confirmedAt = r.getConfirmedAt();
        dto.durationMinutes = r.getSlot().getDurationMinutes();
        dto.peopleCount = r.getPeopleCount();
        dto.fee = 0;
        dto.status = r.getStatus();
        dto.past = past;
        dto.additionalAnswers = List.of();
        return dto;
    }
}
