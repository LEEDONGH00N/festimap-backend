package com.halo.eventer.domain.program_reservation.dto.request;

import jakarta.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProgramCreateRequest {
    @NotBlank
    private String name;
}
