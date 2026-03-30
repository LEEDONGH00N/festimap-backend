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
public class UserAdditionalFieldListResponse {

    private List<FieldResponse> fields;

    public static UserAdditionalFieldListResponse of(
            List<ProgramAdditionalField> fields, Map<Long, List<ProgramAdditionalFieldOption>> optionsMap) {
        UserAdditionalFieldListResponse response = new UserAdditionalFieldListResponse();
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
        private List<OptionResponse> options;

        public static FieldResponse from(ProgramAdditionalField field, List<ProgramAdditionalFieldOption> options) {
            FieldResponse dto = new FieldResponse();
            dto.id = field.getId();
            dto.type = field.getType();
            dto.label = field.getLabel();
            dto.required = field.isRequired();
            dto.options = options.stream().map(OptionResponse::from).collect(Collectors.toList());
            return dto;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class OptionResponse {
        private Long id;
        private String label;
        private boolean isFreeText;

        public static OptionResponse from(ProgramAdditionalFieldOption option) {
            OptionResponse dto = new OptionResponse();
            dto.id = option.getId();
            dto.label = option.getLabel();
            dto.isFreeText = option.isFreeText();
            return dto;
        }
    }
}
