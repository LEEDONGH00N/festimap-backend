package com.halo.eventer.domain.fieldops.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "FieldOps 비밀번호 검증 요청")
public class FieldOpsPasswordVerifyRequest {

    @NotBlank(message = "비밀번호는 필수입니다")
    @Pattern(regexp = "^\\d{4}$", message = "비밀번호는 4자리 숫자여야 합니다")
    @Schema(description = "4자리 비밀번호", example = "1234")
    private String password;
}
