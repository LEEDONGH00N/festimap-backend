package com.halo.eventer.domain.fieldops.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "FieldOps 비밀번호 검증 응답")
public class FieldOpsVerifyResponse {

    @Schema(description = "인증 성공 여부")
    private final boolean authenticated;

    @Schema(description = "메시지")
    private final String message;

    public FieldOpsVerifyResponse(boolean authenticated, String message) {
        this.authenticated = authenticated;
        this.message = message;
    }

    public static FieldOpsVerifyResponse success() {
        return new FieldOpsVerifyResponse(true, "Authentication successful");
    }
}
