package com.halo.eventer.domain.fieldops.dto.response;

import com.halo.eventer.domain.fieldops.FieldOpsSession;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "FieldOps 링크 응답")
public class FieldOpsLinkResponse {

    @Schema(description = "세션 ID")
    private final Long sessionId;

    @Schema(description = "링크 토큰 (UUID)")
    private final String token;

    @Schema(description = "초기 비밀번호 (4자리, 최초 생성/재발급 시에만 반환)")
    private final String password;

    @Schema(description = "세션 TTL (시간)")
    private final int sessionTtlHours;

    @Schema(description = "접근 URL")
    private final String accessUrl;

    public FieldOpsLinkResponse(FieldOpsSession session, String password, String baseUrl) {
        this.sessionId = session.getId();
        this.token = session.getToken();
        this.password = password;
        this.sessionTtlHours = session.getSessionTtlHours();
        this.accessUrl = baseUrl + "/api/v1/field-ops/" + session.getToken();
    }
}
