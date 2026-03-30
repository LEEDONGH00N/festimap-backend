package com.halo.eventer.domain.sms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import com.halo.eventer.domain.member.MemberRole;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SmsVerifyCodeRequest {

    @NotBlank(message = "전화번호는 필수입니다")
    @Pattern(regexp = "^01[016789]\\d{7,8}$", message = "올바른 전화번호 형식이 아닙니다")
    private String phone;

    @NotBlank(message = "인증코드는 필수입니다")
    @Size(min = 6, max = 6, message = "인증코드는 6자리입니다")
    private String code;

    private MemberRole role;

    public SmsVerifyCodeRequest(String phone, String code) {
        this.phone = phone;
        this.code = code;
    }

    public SmsVerifyCodeRequest(String phone, String code, MemberRole role) {
        this.phone = phone;
        this.code = code;
        this.role = role;
    }
}
