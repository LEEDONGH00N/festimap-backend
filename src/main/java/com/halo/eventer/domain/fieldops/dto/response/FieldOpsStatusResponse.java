package com.halo.eventer.domain.fieldops.dto.response;

import com.halo.eventer.domain.fieldops.FieldOpsSession;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "FieldOps 링크 상태 확인 응답")
public class FieldOpsStatusResponse {

    @Schema(description = "링크 유효 여부")
    private final boolean valid;

    @Schema(description = "축제 이름")
    private final String festivalName;

    @Schema(description = "카테고리 이름")
    private final String categoryName;

    public FieldOpsStatusResponse(FieldOpsSession session) {
        this.valid = session.isValid();
        this.festivalName =
                session.getCategory().getFestivalModule().getFestival().getName();
        this.categoryName = session.getCategory().getName();
    }

    public static FieldOpsStatusResponse invalid() {
        return new FieldOpsStatusResponse();
    }

    private FieldOpsStatusResponse() {
        this.valid = false;
        this.festivalName = null;
        this.categoryName = null;
    }
}
