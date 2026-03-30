package com.halo.eventer.global.error;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Getter
public enum ErrorCode {

    // Common Error
    INTERNAL_SERVER_ERROR("C001", "Server Error", 500),
    INVALID_INPUT_VALUE("C002", "Invalid Input Value", 400),
    METHOD_NOT_ALLOWED("C003", "Invalid HTTP Method", 405),
    ENTITY_NOT_FOUND("C004", "Entity Not Found", 400),
    CONFLICT("C005", "Conflict Occurred", 400),
    UNACCEPTABLE_EXTENSION("C007", "Unacceptable Extension", 400),
    INVALID_JSON_FORMAT("C008", "Invalid JSON Format", 400),
    MISSING_PARAMETER("C009", "Missing Parameter", 400),
    INVALID_PARAMETER_TYPE("C010", "Invalid Parameter Type", 400),
    MISSING_PATH_VARIABLE("C011", "Missing Path Variable", 400),
    FORBIDDEN("C012", "Forbidden", 400),
    ERR_DATA_INTEGRITY_VIOLATION("E001", "Data integrity violation", 409),
    VALIDATION_FAILED("C013", "Validation Failed", 400),
    MAX_COUNT_EXCEEDED("C014", "Exceeded maximum count (limit: 20)", 400),

    // Program Reservation
    ACTIVE_RESERVATION_EXISTS("PR001", "Active reservation exists", 409),
    CAPACITY_NOT_ENOUGH("PR002", "Capacity not enough", 409),
    RESERVATION_EXPIRED("PR003", "Reservation expired", 401),
    TOO_MANY_REQUESTS("PR004", "Too many request", 429),
    IDEMPOTENCY_KEY_REUSED("PR005", "Idempotency key reused", 409),
    CANCEL_NOT_ALLOWED("PR006", "Cancel not allowed", 400),
    RESERVATION_NOT_FOUND("PR007", "Reservation not found", 404),

    // Festival
    SUB_ADDRESS_ALREADY_EXISTS("F001", "subAddress Already Exists", 400),
    FESTIVAL_NOT_FOUND("F002", "Festival not found", 404),
    FESTIVAL_ACCESS_DENIED("F003", "You do not have access to this festival", 403),

    // DownWidget Error
    PERMIT_THREE_ELEMENT("DW001", "Only Permit Three Element", 400),

    // Vote Error
    ALREADY_LIKE("C006", "Already Like Element", 400),

    // Auth
    LOGIN_FAILED("A001", "Login Failed", 401),
    UN_AUTHENTICATED("A002", "Unauthenticated", 401),
    UN_AUTHORIZED("A003", "Unauthorized", 403),
    LOGIN_ID_ALREADY_EXISTS("A004", "Login ID already exists", 409),
    COMPANY_EMAIL_ALREADY_EXISTS("A005", "Company email already exists", 409),
    PASSWORD_NOT_MATCH("A006", "Password confirmation does not match", 400),

    // StampUser
    STAMP_USER_ALREADY_EXISTS("SU001", "StampUser Already Exists", 400),

    // Visitor
    PHONE_ALREADY_REGISTERED("V001", "Phone Already Registered", 400),
    MEMBER_NOT_ACTIVE("V003", "Member Not Active", 403),
    MEMBER_NOT_FOUND("V004", "Member Not found", 404),

    // Stamp
    INVALID_AUTH_METHOD("ST001", "Stamp Authentication Method Invalid", 400),
    STAMP_NOT_IN_FESTIVAL("ST002", "Stamp Not In Festival", 400),

    // Infra
    SMS_SEND_FAILED("I001", "SMS Send Failed", 500),

    // SMS Verification
    SMS_CODE_EXPIRED("S001", "SMS verification code expired", 400),
    SMS_CODE_INVALID("S002", "SMS verification code invalid", 400),
    SMS_MAX_ATTEMPT_EXCEEDED("S003", "SMS verification max attempt exceeded", 429),
    PHONE_NOT_VERIFIED("S004", "Phone number not verified", 400),

    // Social Login
    INVALID_SOCIAL_TOKEN("SO001", "Invalid social access token", 401),
    INVALID_SOCIAL_PROVIDER("SO002", "Invalid social provider", 400),
    SOCIAL_LOGIN_FAILED("SO003", "Social login failed", 500),
    SIGNUP_REQUIRED("SO004", "Signup required", 200),

    // FieldOps
    FIELD_OPS_SESSION_NOT_FOUND("FO001", "FieldOps session not found", 404),
    FIELD_OPS_SESSION_EXPIRED("FO002", "FieldOps session expired", 401),
    FIELD_OPS_PASSWORD_INVALID("FO003", "Invalid password", 401),
    FESTIVAL_NOT_ACTIVE("FO004", "Festival is not active", 403),
    CATEGORY_NOT_FOUND("FO005", "Category not found", 404),
    FIELD_OPS_SESSION_ALREADY_EXISTS("FO006", "FieldOps session already exists for this category", 409),
    ;

    private final String code;
    private final String message;
    private final int status;

    ErrorCode(final String code, final String message, final int status) {
        this.message = message;
        this.code = code;
        this.status = status;
    }
}
