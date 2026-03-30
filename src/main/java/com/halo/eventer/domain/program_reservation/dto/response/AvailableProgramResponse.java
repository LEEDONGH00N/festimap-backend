package com.halo.eventer.domain.program_reservation.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.halo.eventer.domain.program_reservation.Program;
import com.halo.eventer.domain.program_reservation.entity.content.ProgramTag;
import lombok.Getter;

@Getter
public class AvailableProgramResponse {
    private Long id;
    private String name;
    private String thumbnailUrl;
    private LocalDateTime bookingOpenAt;
    private LocalDateTime bookingCloseAt;
    private int price;
    private String durationTime;
    private String availableAge;
    private int maxPersonCount;
    private List<ProgramDetailResponse.TagResponse> tags;
    private List<ReservationDateListResponse.DateItem> dates;

    public static AvailableProgramResponse from(
            Program p, List<ProgramTag> programTags, List<ReservationDateListResponse.DateItem> dates) {
        AvailableProgramResponse response = new AvailableProgramResponse();
        response.id = p.getId();
        response.name = p.getName();
        response.thumbnailUrl = p.getThumbnailUrl();
        response.bookingOpenAt = p.getBookingOpenAt();
        response.bookingCloseAt = p.getBookingCloseAt();
        response.price = p.getPriceAmount();
        response.durationTime = p.getDurationTime();
        response.availableAge = p.getAvailableAge();
        response.maxPersonCount = p.getMaxPersonCount();
        response.tags = programTags.stream()
                .map(ProgramDetailResponse.TagResponse::from)
                .collect(Collectors.toList());
        response.dates = dates;
        return response;
    }
}
