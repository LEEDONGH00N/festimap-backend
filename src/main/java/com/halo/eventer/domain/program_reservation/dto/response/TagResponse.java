package com.halo.eventer.domain.program_reservation.dto.response;

import com.halo.eventer.domain.program_reservation.entity.content.Tag;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TagResponse {
    private String name;
    private String bgColorHex;
    private String mainColorHex;
    private String iconUrl;

    public static TagResponse from(Tag tag) {
        return new TagResponse(tag.getName(), tag.getBgColorHex(), tag.getMainColorHex(), tag.getIconUrl());
    }
}
