package com.halo.eventer.global.constants;

public class SecurityConstants {

    // 공개 API 경로
    public static final String[] SWAGGER_URLS = {
        "/", "/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**", "/docs/**"
    };

    public static final String[] PUBLIC_GET_URLS = {
        "/concert",
        "/concert/*",
        "/notice/banner",
        "/notice/**",
        "/widget/*",
        "/widget",
        "/stamp",
        "/stamp/users",
        "/stamp/missions",
        "/stamp/mission",
        "/stamp/mission/all",
        "/splash",
        "/missingPerson",
        "/missingPerson/*",
        "/middleBanner",
        "/middleBanner/*",
        "/menu",
        "/maps",
        "/maps/*",
        "/map-categories/*/maps",
        "/*/map-categories",
        "/map-categories",
        "/map-categories/*",
        "/map-categories/*/image",
        "/menuCategory/image",
        "*/map-categories/displayOrder",
        "/manager",
        "/*/lost-items",
        "/lost-items/*",
        "/inquiries/paging",
        "/inquiries/forUser",
        "/festivals",
        "/festivals/**",
        "/duration/*",
        "/duration",
        "/concertInfo",
        "/concertInfo/*",
        "/api/upWidgets/datetime",
        "/univ",
        "/home/*",
        "/home",
        "/notices/*",
        "/notices",
        "/maps/*/menus",
        "/test/festival",
        "/upload/preSigned",
        "/api/v2/admin/parking-managements/*/sub-page-info",
        "/api/v2/user/parking-managements/*",
        "/api/v2/user/parking-notices/*",
        "/api/v2/user/parking-managements/*/parking-zones",
        "/api/v2/template/**",
        "/api/v2/user/parking-managements/{parkingManagementId}/parking-notices",
        "/api/v2/common/festivals/{festivalId}/parkingManagements",
        "/programs",
        "/programs/*",
        // 관리자(AGENCY) 아이디 중복 검사
        "/api/v1/admin/auth/check-login-id",
        "/programs/*",
        "/programs/*/dates",
        "/programs/*/reservation-slots"
    };

    public static final String[] PUBLIC_POST_URLS = {
        "/stamp/user",
        "/stamp/user/signup",
        "/stamp/mission",
        "/stamp/user/login",
        "/stamp/user/custom",
        "/missingPerson",
        "/login",
        "/inquiries",
        "/inquiries/forUser/*",
        "/api/v2/user/*/*/*/*/signup",
        "/api/v2/user/*/*/*/*/login",
        // VISITOR 인증 API
        "/api/v1/auth/social-login",
        "/api/v1/auth/signup",
        // SMS 인증 API
        "/api/v1/auth/sms/send",
        "/api/v1/auth/sms/verify",
        // 관리자(AGENCY) 인증 API
        "/api/v1/admin/auth/login",
        "/api/v1/admin/auth/signup"
    };

    public static final String[] PUBLIC_PATCH_URLS = {"/stamp/user/*/*", "/stamp/user/check/v2/*", "/stamp/mission"};

    public static final String[] ACTUATOR_URL = {"/actuator/prometheus"};

    // FieldOps 공개 경로
    public static final String[] FIELD_OPS_PUBLIC_GET_URLS = {"/api/v1/field-ops/*/status"};

    public static final String[] FIELD_OPS_PUBLIC_POST_URLS = {"/api/v1/field-ops/*/verify"};
}
