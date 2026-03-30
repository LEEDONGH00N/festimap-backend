package com.halo.eventer.domain.program_reservation.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReservationHoldRequest {
    @NotNull
    private Long programId;

    @NotNull
    private Long slotId;

    @NotNull
    @Min(1)
    private Integer headcount;
}
