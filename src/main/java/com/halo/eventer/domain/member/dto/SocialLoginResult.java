package com.halo.eventer.domain.member.dto;

import com.halo.eventer.domain.member.SocialProvider;
import lombok.Getter;

@Getter
public class SocialLoginResult {

    private final boolean isMember;
    private final String token;
    private final SocialProvider provider;
    private final String providerId;

    private SocialLoginResult(boolean isMember, String token, SocialProvider provider, String providerId) {
        this.isMember = isMember;
        this.token = token;
        this.provider = provider;
        this.providerId = providerId;
    }

    public static SocialLoginResult loggedIn(String token) {
        return new SocialLoginResult(true, token, null, null);
    }

    public static SocialLoginResult needSignup(SocialProvider provider, String providerId) {
        return new SocialLoginResult(false, null, provider, providerId);
    }
}
