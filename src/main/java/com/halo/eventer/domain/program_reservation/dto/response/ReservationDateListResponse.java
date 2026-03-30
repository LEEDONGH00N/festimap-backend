package com.halo.eventer.domain.program_reservation.dto.response;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class ReservationDateListResponse {
    private Long programId;
    private int maxPersonCount;
    private Integer holdMinutes;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate selectedDate;

    private List<DateItem> dates;

    public static ReservationDateListResponse of(
            Long programId, int maxPersonCount, Integer holdMinutes, List<DateItem> dates) {
        ReservationDateListResponse r = new ReservationDateListResponse();
        r.programId = programId;
        r.maxPersonCount = maxPersonCount;
        r.holdMinutes = holdMinutes;
        r.selectedDate = dates.isEmpty() ? null : dates.get(0).getDate();
        r.dates = dates;
        return r;
    }

    @Getter
    public static class DateItem {
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        private LocalDate date;

        @JsonProperty("isReservable")
        private boolean reservable;

        public static DateItem of(LocalDate date, boolean isReservable) {
            DateItem d = new DateItem();
            d.date = date;
            d.reservable = isReservable;
            return d;
        }
    }
}
