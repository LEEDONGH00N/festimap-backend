package com.halo.eventer.domain.member.dto;

import jakarta.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NotificationRecipientRequest {

    @NotBlank(message = "이름은 필수입니다")
    private String name;

    @NotBlank(message = "연락처는 필수입니다")
    private String phone;
}
