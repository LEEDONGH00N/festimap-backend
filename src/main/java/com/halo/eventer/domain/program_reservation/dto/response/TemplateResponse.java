package com.halo.eventer.domain.program_reservation.dto.response;

import com.halo.eventer.domain.program_reservation.entity.content.FestivalCommonTemplate;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TemplateResponse {
    private Long id;
    private int sortOrder;
    private String title;
    private String content;

    public static TemplateResponse from(FestivalCommonTemplate template) {
        TemplateResponse dto = new TemplateResponse();
        dto.id = template.getId();
        dto.sortOrder = template.getSortOrder();
        dto.title = template.getTitle();
        dto.content = template.getContent();
        return dto;
    }
}
