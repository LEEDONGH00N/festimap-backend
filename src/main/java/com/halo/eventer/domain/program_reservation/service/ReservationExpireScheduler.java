package com.halo.eventer.domain.program_reservation.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationExpireScheduler {
    private static final int BATCH_SIZE = 50;
    private static final int MAX_ITERATIONS = 20; // 최대 1000건 처리

    private final ReservationExpireService reservationExpireService;

    /**
     * 만료된 HOLD 예약을 정리(EXPIRED)하고 슬롯 capacity를 복구.
     * 밀린 작업이 있으면 남은 것이 없을 때까지 반복 처리.
     */
    @Scheduled(cron = "0 */5 * * * *", zone = "Asia/Seoul")
    public void expireHolds() {
        int totalProcessed = 0;
        int iteration = 0;
        int processed;

        do {
            try {
                processed = reservationExpireService.expireExpiredHoldsBatch(BATCH_SIZE);
                totalProcessed += processed;
                iteration++;
            } catch (Exception e) {
                log.error("[ReservationExpireScheduler] expireHolds failed at iteration={}", iteration, e);
                break;
            }
        } while (processed == BATCH_SIZE && iteration < MAX_ITERATIONS);

        if (totalProcessed > 0) {
            log.info("[ReservationExpireScheduler] expired holds total={}, iterations={}", totalProcessed, iteration);
        }

        if (iteration == MAX_ITERATIONS) {
            log.warn(
                    "[ReservationExpireScheduler] reached max iterations ({}), may have more expired holds",
                    MAX_ITERATIONS);
        }
    }
}
