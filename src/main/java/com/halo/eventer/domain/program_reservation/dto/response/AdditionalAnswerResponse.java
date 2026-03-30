package com.halo.eventer.domain.program_reservation.dto.response;

import com.halo.eventer.domain.program_reservation.entity.additional.AdditionalFieldType;
import com.halo.eventer.domain.program_reservation.entity.additional.ProgramReservationAdditionalAnswer;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdditionalAnswerResponse {
    private Long fieldId;
    private String label;
    private String value;

    public static AdditionalAnswerResponse from(ProgramReservationAdditionalAnswer answer) {
        String value;
        if (answer.getTypeSnapshot() == AdditionalFieldType.SELECT) {
            value = answer.getValueText() != null
                    ? answer.getOptionLabelSnapshot() + " (" + answer.getValueText() + ")"
                    : answer.getOptionLabelSnapshot();
        } else {
            value = answer.getValueText();
        }
        return new AdditionalAnswerResponse(answer.getField().getId(), answer.getFieldLabelSnapshot(), value);
    }
}
