package com.halo.eventer.domain.program_reservation.dto.response;

import java.util.List;

import lombok.Getter;

@Getter
public class AdminCancelResponse {
    private final List<Long> canceledIds;

    public AdminCancelResponse(List<Long> canceledIds) {
        this.canceledIds = canceledIds;
    }
}
