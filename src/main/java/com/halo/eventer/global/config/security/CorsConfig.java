package com.halo.eventer.global.config.security;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {

    private static final List<String> ALLOWED_ORIGINS = List.of(
            "http://localhost:3000",
            "https://localhost:3000",
            "http://khucore.localhost:3000",
            "http://business.localhost:3000",
            "http://business.localhost:3001",
            "https://khucore.com",
            "https://wabaa.kr",
            "https://business.wabaa.kr",
            "https://adelante.wabauniv.com",
            "http://m.localhost:3000",
            "https://firefestivaljeju.com",
            "https://m.firefestivaljeju.com",
            "https://business.festiv.kr",
            "https://*.stamp.festiv.kr",
            "https://*.parking.festiv.kr",
            "https://*.program.festiv.kr",
            "https://*.mypage.festiv.kr",
            "https://mypage.festiv.kr",
            "https://account.festiv.kr",
            "https://*.account.festiv.kr");

    @Bean
    public CorsConfigurationSource customCorsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOriginPatterns(ALLOWED_ORIGINS);
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
