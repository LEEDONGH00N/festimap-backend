package com.halo.eventer.domain.sms;

import java.time.LocalDateTime;
import jakarta.persistence.*;

import com.halo.eventer.global.common.BaseTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sms_verification")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SmsVerification extends BaseTime {

    private static final int CODE_EXPIRY_MINUTES = 3;
    private static final int VERIFICATION_VALID_MINUTES = 30;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String phone;

    @Column(nullable = false, length = 6)
    private String code;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Column(name = "attempt_count", nullable = false)
    private int attemptCount = 0;

    private SmsVerification(String phone, String code) {
        this.phone = phone;
        this.code = code;
        this.expiresAt = LocalDateTime.now().plusMinutes(CODE_EXPIRY_MINUTES);
    }

    public static SmsVerification create(String phone, String code) {
        return new SmsVerification(phone, code);
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public boolean isVerified() {
        return verifiedAt != null;
    }

    public boolean isVerificationValid() {
        if (verifiedAt == null) {
            return false;
        }
        return LocalDateTime.now().isBefore(verifiedAt.plusMinutes(VERIFICATION_VALID_MINUTES));
    }

    public boolean matches(String inputCode) {
        return this.code.equals(inputCode);
    }

    public void incrementAttemptCount() {
        this.attemptCount++;
    }

    public void markVerified() {
        this.verifiedAt = LocalDateTime.now();
    }

    public boolean isMaxAttemptExceeded() {
        return this.attemptCount >= 5;
    }
}
