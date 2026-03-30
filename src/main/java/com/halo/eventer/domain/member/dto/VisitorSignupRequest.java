package com.halo.eventer.domain.member.dto;

import java.time.LocalDate;

import com.halo.eventer.domain.member.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class VisitorSignupRequest {
    // 필수 - 소셜 로그인 정보
    private SocialProvider provider;
    private String providerId;

    // 필수 - 기본 정보
    private String phone;
    private String name;

    // 필수 - 약관 동의
    private Boolean termsAgreed;
    private Boolean privacyAgreed;

    // 선택 - 마케팅 동의
    private Boolean marketingAgreed;

    // 선택 - 설문 정보
    private String residenceType;
    private String residenceRegion;
    private String residenceDistrict;
    private String visitType;
    private Gender gender;
    private LocalDate birthDate;

    public boolean isTermsAgreed() {
        return Boolean.TRUE.equals(termsAgreed);
    }

    public boolean isPrivacyAgreed() {
        return Boolean.TRUE.equals(privacyAgreed);
    }

    public boolean isMarketingAgreed() {
        return Boolean.TRUE.equals(marketingAgreed);
    }
}
