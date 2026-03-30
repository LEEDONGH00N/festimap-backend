package com.halo.eventer.domain.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CompanyInfoUpdateRequest {

    @NotBlank(message = "회사명은 필수입니다")
    private String companyName;

    @NotBlank(message = "회사 이메일은 필수입니다")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    private String companyEmail;

    @NotBlank(message = "회사 연락처는 필수입니다")
    private String companyPhone;
}
