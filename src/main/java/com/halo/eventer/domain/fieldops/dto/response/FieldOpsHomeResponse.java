package com.halo.eventer.domain.fieldops.dto.response;

import com.halo.eventer.domain.fieldops.FieldOpsSession;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "FieldOps 홈 화면 데이터 응답")
public class FieldOpsHomeResponse {

    @Schema(description = "축제 ID")
    private final Long festivalId;

    @Schema(description = "축제 이름")
    private final String festivalName;

    @Schema(description = "카테고리 ID")
    private final Long categoryId;

    @Schema(description = "카테고리 이름")
    private final String categoryName;

    @Schema(description = "서비스 타입")
    private final String serviceType;

    public FieldOpsHomeResponse(FieldOpsSession session) {
        this.festivalId =
                session.getCategory().getFestivalModule().getFestival().getId();
        this.festivalName =
                session.getCategory().getFestivalModule().getFestival().getName();
        this.categoryId = session.getCategory().getId();
        this.categoryName = session.getCategory().getName();
        this.serviceType =
                session.getCategory().getFestivalModule().getServiceType().name();
    }
}
