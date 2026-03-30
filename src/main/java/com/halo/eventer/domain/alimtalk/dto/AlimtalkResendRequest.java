package com.halo.eventer.domain.alimtalk.dto;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AlimtalkResendRequest {
    private List<Long> ids;
}
