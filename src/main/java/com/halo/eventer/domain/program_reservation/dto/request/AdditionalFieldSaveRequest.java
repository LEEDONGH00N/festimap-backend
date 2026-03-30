package com.halo.eventer.domain.program_reservation.dto.request;

import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.halo.eventer.domain.program_reservation.entity.additional.AdditionalFieldType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AdditionalFieldSaveRequest {

    @NotNull
    @Valid
    private List<FieldRequest> fields;

    @Getter
    @NoArgsConstructor
    public static class FieldRequest {
        private Long id;

        @NotNull
        private AdditionalFieldType type;

        @NotBlank
        private String label;

        @JsonProperty("active")
        private boolean isActive;

        @Valid
        private List<OptionRequest> options;
    }

    @Getter
    @NoArgsConstructor
    public static class OptionRequest {
        private Long id;

        @NotBlank
        private String label;

        @JsonProperty("active")
        private boolean isActive;

        @JsonProperty("freeText")
        private boolean isFreeText;
    }
}
