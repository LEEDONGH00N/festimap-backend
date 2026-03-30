package com.halo.eventer.domain.member.oauth;

import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.halo.eventer.domain.member.SocialProvider;
import com.halo.eventer.global.error.ErrorCode;
import com.halo.eventer.global.error.exception.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class KakaoOAuthClient implements SocialOAuthClient {

    private static final String USER_INFO_URL = "https://kapi.kakao.com/v2/user/me";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public SocialProvider provider() {
        return SocialProvider.KAKAO;
    }

    @Override
    public SocialUserInfo getUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        try {
            ResponseEntity<String> response =
                    restTemplate.exchange(USER_INFO_URL, HttpMethod.GET, new HttpEntity<>(headers), String.class);

            JsonNode root = objectMapper.readTree(response.getBody());
            String id = root.get("id").asText();

            return SocialUserInfo.builder()
                    .provider(SocialProvider.KAKAO)
                    .providerId(id)
                    .build();
        } catch (HttpClientErrorException e) {
            log.error("Kakao token verification failed: status={}", e.getStatusCode());
            throw new BaseException(ErrorCode.INVALID_SOCIAL_TOKEN);
        } catch (Exception e) {
            log.error("Kakao login error: {}", e.getMessage());
            throw new BaseException(ErrorCode.SOCIAL_LOGIN_FAILED);
        }
    }
}
