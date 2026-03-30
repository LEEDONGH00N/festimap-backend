package com.halo.eventer.domain.member.dto;

import com.halo.eventer.domain.member.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "관리자(AGENCY) 회원가입 응답")
public class AgencySignupResponse {

    @Schema(description = "회원 ID")
    private final Long memberId;

    @Schema(description = "아이디")
    private final String loginId;

    @Schema(description = "기업명")
    private final String companyName;

    @Schema(description = "기업 이메일")
    private final String companyEmail;

    @Schema(description = "담당자 성함")
    private final String managerName;

    @Schema(description = "메시지")
    private final String message;

    public AgencySignupResponse(Member member) {
        this.memberId = member.getId();
        this.loginId = member.getLoginId();
        this.companyName = member.getCompanyName();
        this.companyEmail = member.getCompanyEmail();
        this.managerName = member.getName();
        this.message = "회원가입이 완료되었습니다.";
    }
}
