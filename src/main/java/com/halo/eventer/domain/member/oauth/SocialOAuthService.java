package com.halo.eventer.domain.member.oauth;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.halo.eventer.domain.member.SocialProvider;
import com.halo.eventer.global.error.ErrorCode;
import com.halo.eventer.global.error.exception.BaseException;

@Service
public class SocialOAuthService {

    private final Map<SocialProvider, SocialOAuthClient> clients;

    public SocialOAuthService(List<SocialOAuthClient> clientList) {
        this.clients = clientList.stream().collect(Collectors.toMap(SocialOAuthClient::provider, Function.identity()));
    }

    public SocialUserInfo getUserInfo(SocialProvider provider, String accessToken) {
        SocialOAuthClient client = clients.get(provider);
        if (client == null) {
            throw new BaseException(ErrorCode.INVALID_SOCIAL_PROVIDER);
        }
        return client.getUserInfo(accessToken);
    }
}
