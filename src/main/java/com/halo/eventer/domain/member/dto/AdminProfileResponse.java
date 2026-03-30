package com.halo.eventer.domain.member.dto;

import com.halo.eventer.domain.member.Member;
import lombok.Getter;

@Getter
public class AdminProfileResponse {

    private final String loginId;
    private final String phone;
    private final String managerName;
    private final String managerPosition;
    private final String email;

    public AdminProfileResponse(Member member) {
        this.loginId = member.getLoginId();
        this.phone = member.getPhone();
        this.managerName = member.getName();
        this.managerPosition = member.getManagerPosition();
        this.email = member.getCompanyEmail();
    }
}
