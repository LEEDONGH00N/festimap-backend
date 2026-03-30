package com.halo.eventer.domain.program_reservation.dto.request;

import jakarta.validation.constraints.NotNull;

import lombok.Getter;

@Getter
public class ProgramThumbnailRequest {
    @NotNull
    private String thumbnail;
}
