package com.halo.eventer.domain.sms.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.halo.eventer.domain.sms.SmsVerification;

public interface SmsVerificationRepository extends JpaRepository<SmsVerification, Long> {

    Optional<SmsVerification> findByPhone(String phone);

    void deleteByPhone(String phone);

    @Modifying
    @Query("DELETE FROM SmsVerification s WHERE s.expiresAt < :now AND s.verifiedAt IS NULL")
    int deleteExpiredUnverified(@Param("now") LocalDateTime now);

    @Modifying
    @Query("DELETE FROM SmsVerification s WHERE s.verifiedAt IS NOT NULL AND s.verifiedAt < :cutoff")
    int deleteOldVerified(@Param("cutoff") LocalDateTime cutoff);
}
