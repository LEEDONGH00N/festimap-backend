package com.halo.eventer.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.halo.eventer.domain.member.SocialProvider;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SocialTokenLoginRequest {

    @NotNull(message = "provider는 필수입니다")
    private SocialProvider provider;

    @NotBlank(message = "accessToken은 필수입니다")
    private String accessToken;

    public SocialTokenLoginRequest(SocialProvider provider, String accessToken) {
        this.provider = provider;
        this.accessToken = accessToken;
    }
}
