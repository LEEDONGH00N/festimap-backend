package com.halo.eventer.domain.member.dto;

import com.halo.eventer.domain.member.Member;
import lombok.Getter;

@Getter
public class CompanyInfoResponse {

    private final String companyName;
    private final String companyEmail;
    private final String companyPhone;

    public CompanyInfoResponse(Member member) {
        this.companyName = member.getCompanyName();
        this.companyEmail = member.getCompanyEmail();
        this.companyPhone = member.getCompanyPhone();
    }
}
