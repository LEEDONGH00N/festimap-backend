package com.halo.eventer.domain.program_reservation.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ScheduleTemplateUpdateResponse {

    private Long templateId;
    private String result;
    private List<UpdatedPattern> updated;
    private List<RejectedPattern> rejected;

    public static ScheduleTemplateUpdateResponse success(Long templateId) {
        return new ScheduleTemplateUpdateResponse(templateId, "SUCCESS", null, null);
    }

    public static ScheduleTemplateUpdateResponse partial(
            Long templateId, List<UpdatedPattern> updated, List<RejectedPattern> rejected) {
        String result = (rejected == null || rejected.isEmpty()) ? "SUCCESS" : "PARTIAL_SUCCESS";
        return new ScheduleTemplateUpdateResponse(templateId, result, updated, rejected);
    }

    @Getter
    @AllArgsConstructor
    public static class UpdatedPattern {
        private Long patternId;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
        private LocalTime startTime;

        private Integer appliedCapacity;
        private Integer updatedSlotCount;
    }

    @Getter
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class RejectedPattern {
        private Long patternId;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
        private LocalTime startTime;

        private String reason;
        private String message;
        private List<FailedSlot> failedSlots;
    }

    @Getter
    @AllArgsConstructor
    public static class FailedSlot {
        private LocalDate slotDate;
        private Integer booked;
        private Integer requestedCapacity;
    }
}
