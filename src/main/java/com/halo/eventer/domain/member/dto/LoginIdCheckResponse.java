package com.halo.eventer.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "아이디 중복 검사 응답")
public class LoginIdCheckResponse {

    @Schema(description = "사용 가능 여부")
    private final boolean available;

    public LoginIdCheckResponse(boolean available) {
        this.available = available;
    }
}
