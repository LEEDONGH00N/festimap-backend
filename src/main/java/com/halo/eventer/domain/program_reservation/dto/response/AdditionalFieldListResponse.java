package com.halo.eventer.domain.program_reservation.dto.response;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.halo.eventer.domain.program_reservation.entity.additional.AdditionalFieldType;
import com.halo.eventer.domain.program_reservation.entity.additional.ProgramAdditionalField;
import com.halo.eventer.domain.program_reservation.entity.additional.ProgramAdditionalFieldOption;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AdditionalFieldListResponse {

    private List<FieldResponse> fields;

    public static AdditionalFieldListResponse of(
            List<ProgramAdditionalField> fields, Map<Long, List<ProgramAdditionalFieldOption>> optionsMap) {
        AdditionalFieldListResponse response = new AdditionalFieldListResponse();
        response.fields = fields.stream()
                .map(f -> FieldResponse.from(f, optionsMap.getOrDefault(f.getId(), List.of())))
                .collect(Collectors.toList());
        return response;
    }

    @Getter
    @NoArgsConstructor
    public static class FieldResponse {
        private Long id;
        private AdditionalFieldType type;
        private String label;
        private boolean required;
        private boolean isActive;
        private List<OptionResponse> options;

        public static FieldResponse from(ProgramAdditionalField field, List<ProgramAdditionalFieldOption> options) {
            FieldResponse dto = new FieldResponse();
            dto.id = field.getId();
            dto.type = field.getType();
            dto.label = field.getLabel();
            dto.required = field.isRequired();
            dto.isActive = field.isActive();
            dto.options = options.stream().map(OptionResponse::from).collect(Collectors.toList());
            return dto;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class OptionResponse {
        private Long id;
        private String label;
        private boolean isActive;
        private boolean isFreeText;

        public static OptionResponse from(ProgramAdditionalFieldOption option) {
            OptionResponse dto = new OptionResponse();
            dto.id = option.getId();
            dto.label = option.getLabel();
            dto.isActive = option.isActive();
            dto.isFreeText = option.isFreeText();
            return dto;
        }
    }
}
