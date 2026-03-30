package com.halo.eventer.domain.fieldops.controller;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.halo.eventer.domain.festival.service.FestivalAccessService;
import com.halo.eventer.domain.fieldops.FieldOpsSession;
import com.halo.eventer.domain.fieldops.dto.response.FieldOpsLinkResponse;
import com.halo.eventer.domain.fieldops.dto.response.FieldOpsSessionResponse;
import com.halo.eventer.domain.fieldops.service.FieldOpsSessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/backoffice")
@RequiredArgsConstructor
@Tag(name = "FieldOps Admin", description = "Agency용 FieldOps 관리 API")
public class FieldOpsAdminController {

    private final FieldOpsSessionService fieldOpsSessionService;
    private final FestivalAccessService festivalAccessService;

    @PostMapping("/categories/{categoryId}/field-ops")
    @Operation(summary = "FieldOps 세션 생성", description = "카테고리에 대한 FieldOps 세션을 생성하고 접근 링크/비밀번호를 발급합니다")
    public ResponseEntity<FieldOpsLinkResponse> createSession(
            @PathVariable Long categoryId, HttpServletRequest request) {
        festivalAccessService.validateCategoryAccess(categoryId);

        FieldOpsSessionService.FieldOpsSessionCreateResult result = fieldOpsSessionService.createSession(categoryId);

        String baseUrl = getBaseUrl(request);
        FieldOpsLinkResponse response = new FieldOpsLinkResponse(result.session(), result.rawPassword(), baseUrl);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/categories/{categoryId}/field-ops")
    @Operation(summary = "FieldOps 세션 조회", description = "카테고리의 FieldOps 세션 정보를 조회합니다")
    public ResponseEntity<FieldOpsSessionResponse> getSession(@PathVariable Long categoryId) {
        festivalAccessService.validateCategoryAccess(categoryId);

        FieldOpsSession session = fieldOpsSessionService.getSessionByCategory(categoryId);
        return ResponseEntity.ok(new FieldOpsSessionResponse(session));
    }

    @PatchMapping("/field-ops/{sessionId}/password")
    @Operation(summary = "비밀번호 재발급", description = "FieldOps 세션의 비밀번호를 재발급합니다")
    public ResponseEntity<FieldOpsLinkResponse> resetPassword(
            @PathVariable Long sessionId, HttpServletRequest request) {
        festivalAccessService.validateFieldOpsSessionAccess(sessionId);

        String rawPassword = fieldOpsSessionService.resetPassword(sessionId);
        FieldOpsSession session = fieldOpsSessionService.getSession(sessionId);

        String baseUrl = getBaseUrl(request);
        FieldOpsLinkResponse response = new FieldOpsLinkResponse(session, rawPassword, baseUrl);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/field-ops/{sessionId}")
    @Operation(summary = "세션 비활성화", description = "FieldOps 세션을 비활성화합니다")
    public ResponseEntity<Void> disableSession(@PathVariable Long sessionId) {
        festivalAccessService.validateFieldOpsSessionAccess(sessionId);

        fieldOpsSessionService.disableSession(sessionId);
        return ResponseEntity.noContent().build();
    }

    private String getBaseUrl(HttpServletRequest request) {
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();

        if ((scheme.equals("http") && serverPort == 80) || (scheme.equals("https") && serverPort == 443)) {
            return scheme + "://" + serverName;
        }
        return scheme + "://" + serverName + ":" + serverPort;
    }
}
