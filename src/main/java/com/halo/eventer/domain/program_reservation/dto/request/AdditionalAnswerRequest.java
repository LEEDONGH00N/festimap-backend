package com.halo.eventer.domain.program_reservation.dto.request;

import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AdditionalAnswerRequest {

    @NotNull
    private Long fieldId;

    private Long optionId;

    private String valueText;
}
