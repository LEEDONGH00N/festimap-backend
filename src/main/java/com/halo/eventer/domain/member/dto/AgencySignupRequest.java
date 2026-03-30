package com.halo.eventer.domain.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "관리자(AGENCY) 회원가입 요청")
public class AgencySignupRequest {

    @NotBlank(message = "아이디는 필수입니다")
    @Size(min = 4, max = 50, message = "아이디는 4~50자 사이여야 합니다")
    @Schema(description = "아이디", example = "agency_user")
    private String loginId;

    @NotBlank(message = "비밀번호는 필수입니다")
    @Schema(description = "비밀번호", example = "password123!")
    private String password;

    @NotBlank(message = "기업 이메일은 필수입니다")
    @Email(message = "유효한 이메일 형식이어야 합니다")
    @Schema(description = "기업 이메일", example = "contact@company.com")
    private String companyEmail;

    @NotBlank(message = "기업명은 필수입니다")
    @Size(max = 100, message = "기업명은 100자 이내여야 합니다")
    @Schema(description = "기업명", example = "페스티맵")
    private String companyName;

    @NotBlank(message = "담당자 성함은 필수입니다")
    @Size(max = 50, message = "담당자 성함은 50자 이내여야 합니다")
    @Schema(description = "담당자 성함", example = "홍길동")
    private String managerName;

    @NotBlank(message = "담당자 직책은 필수입니다")
    @Size(max = 50, message = "담당자 직책은 50자 이내여야 합니다")
    @Schema(description = "담당자 직책", example = "대리")
    private String managerPosition;

    @NotBlank(message = "담당자 핸드폰 번호는 필수입니다")
    @Pattern(regexp = "^01[0-9]{8,9}$", message = "유효한 핸드폰 번호 형식이어야 합니다")
    @Schema(description = "담당자 핸드폰 번호", example = "01012345678")
    private String managerPhone;
}
