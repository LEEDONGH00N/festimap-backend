package com.halo.eventer.global.security.fieldops;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.halo.eventer.domain.fieldops.FieldOpsSession;
import com.halo.eventer.domain.fieldops.service.FieldOpsAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class FieldOpsAuthenticationFilter extends OncePerRequestFilter {

    public static final String COOKIE_NAME = "FIELDOPS_SESSION";
    private static final String PATH_PREFIX = "/api/v1/field-ops/";
    private static final Set<String> PUBLIC_SUFFIXES = Set.of("/status", "/verify");

    private final FieldOpsAuthService fieldOpsAuthService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getRequestURI();

        if (!path.startsWith(PATH_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        if (isPublicPath(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String sessionToken = extractTokenFromCookie(request);
        if (sessionToken == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Missing authentication cookie\"}");
            return;
        }

        try {
            FieldOpsSession session = fieldOpsAuthService.validateTokenAndCheckStatus(sessionToken);
            FieldOpsUserDetails userDetails = new FieldOpsUserDetails(session);
            FieldOpsAuthenticationToken authentication =
                    new FieldOpsAuthenticationToken(userDetails, userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.warn("FieldOps authentication failed: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    private boolean isPublicPath(String path) {
        for (String suffix : PUBLIC_SUFFIXES) {
            if (path.endsWith(suffix)) {
                return true;
            }
        }
        return false;
    }

    private String extractTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }

        return Arrays.stream(cookies)
                .filter(cookie -> COOKIE_NAME.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}
