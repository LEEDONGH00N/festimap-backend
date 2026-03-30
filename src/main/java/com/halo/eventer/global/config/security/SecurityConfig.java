package com.halo.eventer.global.config.security;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

import com.halo.eventer.global.security.exception.CustomAccessDeniedHandler;
import com.halo.eventer.global.security.exception.CustomAuthenticationEntryPoint;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final CorsConfigurationSource corsConfig;
    private final AuthorizationConfig authorizationConfig;
    private final JwtAuthenticationFilterConfig jwtAuthenticationFilterConfig;
    private final SecurityExceptionFilterConfig securityExceptionFilterConfig;
    private final FieldOpsFilterConfig fieldOpsFilterConfig;

    public SecurityConfig(
            @Qualifier("customCorsConfigurationSource") CorsConfigurationSource corsConfig,
            AuthorizationConfig authorizationConfig,
            JwtAuthenticationFilterConfig jwtAuthenticationFilterConfig,
            SecurityExceptionFilterConfig securityExceptionFilterConfig,
            FieldOpsFilterConfig fieldOpsFilterConfig) {
        this.corsConfig = corsConfig;
        this.authorizationConfig = authorizationConfig;
        this.jwtAuthenticationFilterConfig = jwtAuthenticationFilterConfig;
        this.securityExceptionFilterConfig = securityExceptionFilterConfig;
        this.fieldOpsFilterConfig = fieldOpsFilterConfig;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfig))
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(httpBasic -> httpBasic.disable())
                .formLogin(form -> form.disable());

        authorizationConfig.configure(http);
        jwtAuthenticationFilterConfig.configure(http);
        fieldOpsFilterConfig.configure(http);
        securityExceptionFilterConfig.configure(http);

        http.exceptionHandling(ex -> ex.accessDeniedHandler(new CustomAccessDeniedHandler())
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint()));

        return http.build();
    }
}
