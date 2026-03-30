package com.halo.eventer.domain.module.enums;

import lombok.Getter;

@Getter
public enum ServiceType {
    STAMP_TOUR("스탬프투어"),
    PARKING("실시간 주차장"),
    DIGITAL_GUIDE("디지털 가이드");

    private final String displayName;

    ServiceType(String displayName) {
        this.displayName = displayName;
    }
}
