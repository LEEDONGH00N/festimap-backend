package com.halo.eventer.domain.alimtalk.button;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public final class AlimtalkButton {
    private final AlimtalkButtonType type;
    private final String name;
    private final String linkMobile;

    @JsonCreator
    public AlimtalkButton(
            @JsonProperty("type") AlimtalkButtonType type,
            @JsonProperty("name") String name,
            @JsonProperty("linkMobile") String linkMobile) {
        this.type = type;
        this.name = name;
        this.linkMobile = linkMobile;
    }

    public static AlimtalkButton wl(String name, String linkMobile) {
        return new AlimtalkButton(AlimtalkButtonType.WL, name, linkMobile);
    }
}
