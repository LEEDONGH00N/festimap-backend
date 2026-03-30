package com.halo.eventer.domain.program_reservation.dto.response;

import java.util.List;

import org.springframework.data.domain.Page;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminReservationPageResponse {
    private List<AdminReservationResponse> adminReservations;
    private PageInfo pageInfo;

    public static AdminReservationPageResponse of(List<AdminReservationResponse> adminReservations, Page<?> page) {
        return new AdminReservationPageResponse(
                adminReservations,
                new PageInfo(
                        page.getNumber(),
                        page.getSize(),
                        page.getTotalElements(),
                        page.getTotalPages(),
                        page.isFirst(),
                        page.isLast()));
    }

    @Getter
    @AllArgsConstructor
    public static class PageInfo {
        private int page; // 0-based
        private int size;
        private long totalElements;
        private int totalPages;
        private boolean first;
        private boolean last;
    }
}
