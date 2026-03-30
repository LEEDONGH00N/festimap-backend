package com.halo.eventer.domain.program_reservation.dto.request;

import java.util.List;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import com.halo.eventer.domain.program_reservation.entity.reservation.ProgramReservationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class AdminStatusUpdateRequest {
    @NotEmpty
    private List<Long> reservationIds;

    @Schema(type = "string", example = "CANCELED")
    @NotNull
    private ProgramReservationStatus status;
}
