package com.halo.eventer.global.config.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import com.halo.eventer.global.constants.SecurityConstants;

@Configuration
public class AuthorizationConfig {

    public void configure(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth.requestMatchers(SecurityConstants.SWAGGER_URLS)
                .permitAll()
                .requestMatchers(HttpMethod.GET, SecurityConstants.PUBLIC_GET_URLS)
                .permitAll()
                .requestMatchers(HttpMethod.POST, SecurityConstants.PUBLIC_POST_URLS)
                .permitAll()
                .requestMatchers(HttpMethod.PATCH, SecurityConstants.PUBLIC_PATCH_URLS)
                .permitAll()
                .requestMatchers(HttpMethod.GET, SecurityConstants.ACTUATOR_URL)
                .permitAll() // 내부 모니터링 + 포트번호로 제어
                // FieldOps 공개 경로
                .requestMatchers(HttpMethod.GET, SecurityConstants.FIELD_OPS_PUBLIC_GET_URLS)
                .permitAll()
                .requestMatchers(HttpMethod.POST, SecurityConstants.FIELD_OPS_PUBLIC_POST_URLS)
                .permitAll()
                // FieldOps 보호 경로 (필터에서 쿠키 인증)
                .requestMatchers("/api/v1/field-ops/**")
                .permitAll()
                // STAMP 레거시
                .requestMatchers("/api/v2/user/**")
                .hasRole("STAMP")
                // Visitor 프로그램 예약
                .requestMatchers("/programs/**")
                .hasAnyRole("VISITOR", "SUPER_ADMIN")
                // 프로그램 예약 (관리자)
                .requestMatchers("/admin/programs/**")
                .hasAnyRole("SUPER_ADMIN", "AGENCY")
                .requestMatchers("/admin/reservations/**")
                .hasAnyRole("SUPER_ADMIN", "AGENCY")
                // VISITOR 전용 API
                .requestMatchers("/api/v*/visitor/**")
                .hasRole("VISITOR")
                // VISITOR 프로필 API
                .requestMatchers("/api/v*/member/profile/**")
                .hasRole("VISITOR")
                // 백오피스: SUPER_ADMIN, AGENCY 모두 접근 가능
                .requestMatchers("/api/v*/backoffice/**")
                .hasAnyRole("SUPER_ADMIN", "AGENCY")
                // 기존 admin 경로 호환
                .anyRequest()
                .hasAnyRole("SUPER_ADMIN", "AGENCY", "ADMIN"));
    }
}
