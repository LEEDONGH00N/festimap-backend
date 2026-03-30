package com.halo.eventer.domain.program_reservation.dto.request;

import java.util.List;
import jakarta.validation.constraints.NotEmpty;

import lombok.Getter;

@Getter
public class AdminCancelRequest {
    @NotEmpty
    private List<Long> reservationIds;
}
