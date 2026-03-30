package com.halo.eventer.domain.member.oauth;

import com.halo.eventer.domain.member.SocialProvider;

public interface SocialOAuthClient {
    SocialProvider provider();

    SocialUserInfo getUserInfo(String accessToken);
}
