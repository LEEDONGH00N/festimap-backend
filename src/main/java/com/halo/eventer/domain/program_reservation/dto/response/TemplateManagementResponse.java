package com.halo.eventer.domain.program_reservation.dto.response;

import java.util.List;

import lombok.Getter;

@Getter
public class TemplateManagementResponse {
    private final String thumbnail;
    private final List<TemplateResponse> templates;

    private TemplateManagementResponse(String thumbnail, List<TemplateResponse> templates) {
        this.thumbnail = thumbnail;
        this.templates = templates;
    }

    public static TemplateManagementResponse of(String thumbnail, List<TemplateResponse> templates) {
        return new TemplateManagementResponse(thumbnail, templates);
    }
}
