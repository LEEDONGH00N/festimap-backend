package com.halo.eventer.domain.program_reservation.entity.reservation;

import lombok.Getter;

@Getter
public enum ProgramReservationStatus {
    HOLD("예약대기"),
    CONFIRMED("예약확정"),
    EXPIRED("예약만료"),
    CANCELED("예약취소");

    private final String label;

    ProgramReservationStatus(String label) {
        this.label = label;
    }
}
