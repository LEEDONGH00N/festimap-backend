package com.halo.eventer.domain.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AdminProfileUpdateRequest {

    @NotBlank(message = "담당자명은 필수입니다")
    private String managerName;

    @NotBlank(message = "직함은 필수입니다")
    private String managerPosition;

    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    private String email;
}
