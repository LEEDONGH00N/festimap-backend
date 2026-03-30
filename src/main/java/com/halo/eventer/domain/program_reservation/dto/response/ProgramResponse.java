package com.halo.eventer.domain.program_reservation.dto.response;

import com.halo.eventer.domain.program_reservation.Program;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProgramResponse {
    private Long id;
    private String name;
    private boolean isActive;

    public static ProgramResponse from(Program program) {
        ProgramResponse dto = new ProgramResponse();
        dto.id = program.getId();
        dto.name = program.getName();
        dto.isActive = program.isActive();
        return dto;
    }
}
