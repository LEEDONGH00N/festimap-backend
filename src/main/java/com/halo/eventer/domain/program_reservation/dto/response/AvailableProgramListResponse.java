package com.halo.eventer.domain.program_reservation.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AvailableProgramListResponse {
    private String thumbnail;
    private List<AvailableProgramResponse> responses;

    public static AvailableProgramListResponse of(String thumbnail, List<AvailableProgramResponse> responses) {
        return new AvailableProgramListResponse(thumbnail, responses);
    }
}
