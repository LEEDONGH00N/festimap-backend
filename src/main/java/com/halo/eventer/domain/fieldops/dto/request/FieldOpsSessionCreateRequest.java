package com.halo.eventer.domain.fieldops.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "FieldOps 세션 생성 요청")
public class FieldOpsSessionCreateRequest {

    @Schema(description = "세션 TTL (시간 단위)", example = "24")
    private Integer sessionTtlHours;
}
