package com.halo.eventer.domain.member.oauth;

import com.halo.eventer.domain.member.SocialProvider;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SocialUserInfo {
    private final SocialProvider provider;
    private final String providerId;
}
