package com.halo.eventer.domain.program_reservation.dto.response;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.halo.eventer.domain.program_reservation.entity.additional.AdditionalFieldType;
import com.halo.eventer.domain.program_reservation.entity.additional.ProgramAdditionalField;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminAdditionalFieldSummaryResponse {
    private List<FieldSummary> fields;

    public static AdminAdditionalFieldSummaryResponse from(List<ProgramAdditionalField> fields) {
        Map<Long, FieldSummary> map = new LinkedHashMap<>();
        for (ProgramAdditionalField f : fields) {
            map.computeIfAbsent(
                            f.getId(),
                            id -> new FieldSummary(
                                    f.getId(), f.getLabel(), f.getType(), f.isActive(), new ArrayList<>()))
                    .getProgramIds()
                    .add(f.getProgram().getId());
        }
        return new AdminAdditionalFieldSummaryResponse(new ArrayList<>(map.values()));
    }

    @Getter
    @AllArgsConstructor
    public static class FieldSummary {
        private Long fieldId;
        private String label;
        private AdditionalFieldType type;
        private boolean isActive;
        private List<Long> programIds;
    }
}
