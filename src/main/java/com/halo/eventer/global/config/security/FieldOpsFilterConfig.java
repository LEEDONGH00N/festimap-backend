package com.halo.eventer.global.config.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.halo.eventer.domain.fieldops.service.FieldOpsAuthService;
import com.halo.eventer.global.security.fieldops.FieldOpsAuthenticationFilter;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class FieldOpsFilterConfig {

    private final FieldOpsAuthService fieldOpsAuthService;

    public void configure(HttpSecurity http) {
        FieldOpsAuthenticationFilter filter = new FieldOpsAuthenticationFilter(fieldOpsAuthService);
        http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
    }
}
