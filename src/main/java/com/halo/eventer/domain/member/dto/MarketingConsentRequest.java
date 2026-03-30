package com.halo.eventer.domain.member.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MarketingConsentRequest {
    private Boolean marketingAgreed;

    public MarketingConsentRequest(Boolean marketingAgreed) {
        this.marketingAgreed = marketingAgreed;
    }

    public boolean isMarketingAgreed() {
        return Boolean.TRUE.equals(marketingAgreed);
    }
}
