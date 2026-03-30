package com.halo.eventer.domain.member.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StampParticipateRequest {
    private Integer participantCount;
    private String extraText;

    public StampParticipateRequest(Integer participantCount, String extraText) {
        this.participantCount = participantCount;
        this.extraText = extraText;
    }

    public int getParticipantCountOrDefault() {
        return participantCount != null ? participantCount : 1;
    }
}
