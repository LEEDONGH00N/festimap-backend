package com.halo.eventer.domain.fieldops.dto.response;

import com.halo.eventer.domain.fieldops.FieldOpsSession;
import com.halo.eventer.domain.fieldops.enums.FieldOpsSessionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "FieldOps 세션 정보 응답")
public class FieldOpsSessionResponse {

    @Schema(description = "세션 ID")
    private final Long sessionId;

    @Schema(description = "링크 토큰")
    private final String token;

    @Schema(description = "카테고리 ID")
    private final Long categoryId;

    @Schema(description = "카테고리 이름")
    private final String categoryName;

    @Schema(description = "세션 상태")
    private final FieldOpsSessionStatus status;

    @Schema(description = "세션 TTL (시간)")
    private final int sessionTtlHours;

    public FieldOpsSessionResponse(FieldOpsSession session) {
        this.sessionId = session.getId();
        this.token = session.getToken();
        this.categoryId = session.getCategory().getId();
        this.categoryName = session.getCategory().getName();
        this.status = session.getStatus();
        this.sessionTtlHours = session.getSessionTtlHours();
    }
}
