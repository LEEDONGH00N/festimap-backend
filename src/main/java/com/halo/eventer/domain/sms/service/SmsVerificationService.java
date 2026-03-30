package com.halo.eventer.domain.sms.service;

import java.security.SecureRandom;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.halo.eventer.domain.member.MemberRole;
import com.halo.eventer.domain.member.repository.MemberRepository;
import com.halo.eventer.domain.sms.SmsVerification;
import com.halo.eventer.domain.sms.repository.SmsVerificationRepository;
import com.halo.eventer.global.error.ErrorCode;
import com.halo.eventer.global.error.exception.BaseException;
import com.halo.eventer.infra.sms.SmsClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class SmsVerificationService {

    private static final String VERIFICATION_MESSAGE_TEMPLATE = "[Festimap] 인증번호 [%s]를 입력해주세요.";

    private final SmsVerificationRepository smsVerificationRepository;
    private final MemberRepository memberRepository;
    private final SmsClient smsClient;
    private final SecureRandom secureRandom = new SecureRandom();

    @Transactional
    public void sendVerificationCode(String phone) {
        smsVerificationRepository.deleteByPhone(phone);

        String code = generateCode();
        SmsVerification verification = SmsVerification.create(phone, code);
        smsVerificationRepository.save(verification);

        String message = String.format(VERIFICATION_MESSAGE_TEMPLATE, code);
        smsClient.sendOne(phone, message);

        log.info("SMS verification code sent to: {}", maskPhone(phone));
    }

    @Transactional
    public void verifyCode(String phone, String code) {
        verifyCode(phone, code, null);
    }

    @Transactional
    public void verifyCode(String phone, String code, MemberRole role) {
        SmsVerification verification = smsVerificationRepository
                .findByPhone(phone)
                .orElseThrow(() -> new BaseException(ErrorCode.SMS_CODE_EXPIRED));

        if (verification.isMaxAttemptExceeded()) {
            throw new BaseException(ErrorCode.SMS_MAX_ATTEMPT_EXCEEDED);
        }

        verification.incrementAttemptCount();

        if (verification.isExpired()) {
            throw new BaseException(ErrorCode.SMS_CODE_EXPIRED);
        }

        if (!verification.matches(code)) {
            throw new BaseException(ErrorCode.SMS_CODE_INVALID);
        }

        if (role != null && memberRepository.existsByPhoneAndRole(phone, role)) {
            throw new BaseException(ErrorCode.PHONE_ALREADY_REGISTERED);
        }

        verification.markVerified();
        log.info("SMS verification successful for: {}", maskPhone(phone));
    }

    public boolean isVerified(String phone) {
        return smsVerificationRepository
                .findByPhone(phone)
                .map(SmsVerification::isVerificationValid)
                .orElse(false);
    }

    public void validateVerified(String phone) {
        if (!isVerified(phone)) {
            throw new BaseException(ErrorCode.PHONE_NOT_VERIFIED);
        }
    }

    @Transactional
    public void consumeVerification(String phone) {
        smsVerificationRepository.deleteByPhone(phone);
    }

    private String generateCode() {
        int code = secureRandom.nextInt(900000) + 100000;
        return String.valueOf(code);
    }

    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 4) {
            return "****";
        }
        return phone.substring(0, phone.length() - 4) + "****";
    }
}
