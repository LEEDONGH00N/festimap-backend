package com.halo.eventer.domain.program_reservation.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProgramListResponse {
    private final List<ProgramResponse> responses;

    public static ProgramListResponse of(List<ProgramResponse> responses) {
        return new ProgramListResponse(responses);
    }
}
