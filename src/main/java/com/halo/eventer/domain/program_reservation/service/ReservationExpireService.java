package com.halo.eventer.domain.program_reservation.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.halo.eventer.domain.program_reservation.entity.reservation.ProgramReservation;
import com.halo.eventer.domain.program_reservation.entity.reservation.ProgramReservationStatus;
import com.halo.eventer.domain.program_reservation.entity.slot.ProgramSlot;
import com.halo.eventer.domain.program_reservation.repository.ProgramReservationRepository;
import com.halo.eventer.domain.program_reservation.repository.ProgramSlotRepository;
import com.halo.eventer.global.error.ErrorCode;
import com.halo.eventer.global.error.exception.BaseException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ReservationExpireService {

    private final ProgramReservationRepository reservationRepository;
    private final ProgramSlotRepository programSlotRepository;
    private final ReservationExpireService self;

    public ReservationExpireService(
            ProgramReservationRepository reservationRepository,
            ProgramSlotRepository programSlotRepository,
            @Lazy ReservationExpireService self) {
        this.reservationRepository = reservationRepository;
        this.programSlotRepository = programSlotRepository;
        this.self = self;
    }

    /**
     * 만료된 HOLD 예약을 최대 batchSize개 처리.
     * 각 건은 별도 트랜잭션에서 처리되어 한 건 실패가 다른 건에 영향을 주지 않음.
     */
    public int expireExpiredHoldsBatch(int batchSize) {
        LocalDateTime now = LocalDateTime.now();

        List<Long> ids = reservationRepository.findExpiredHoldIds(now, PageRequest.of(0, batchSize));
        if (ids.size() == batchSize) {
            log.info("[ReservationExpireService] batch full ({}), may have more expired holds", batchSize);
        }

        int processed = 0;
        for (Long id : ids) {
            try {
                boolean ok = self.expireOneInNewTransaction(id, now);
                if (ok) processed++;
            } catch (Exception e) {
                log.warn("[ReservationExpireService] expireOne failed. reservationId={}", id, e);
            }
        }
        return processed;
    }

    /**
     * 단건 만료 처리 (별도 트랜잭션)
     * - 비관락으로 예약 row를 잡고
     * - HOLD && expiresAt < now면 EXPIRED 처리 + capacity 복구
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean expireOneInNewTransaction(Long reservationId, LocalDateTime now) {
        ProgramReservation r =
                reservationRepository.findByIdForUpdate(reservationId).orElse(null);
        if (r == null) return false;
        if (r.getStatus() != ProgramReservationStatus.HOLD) return false;

        LocalDateTime expiresAt = r.getHoldExpiresAt();
        if (expiresAt == null || !expiresAt.isBefore(now)) return false;

        r.expire();

        ProgramSlot slot = programSlotRepository
                .findByIdAndProgramIdForUpdate(
                        r.getSlot().getId(), r.getProgram().getId())
                .orElseThrow(() -> new BaseException("존재하지 않는 슬롯입니다.", ErrorCode.ENTITY_NOT_FOUND));
        slot.increaseCapacity(r.getHeadcount());
        return true;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean expireIfNeededForCheckout(Long memberId, Long reservationId, LocalDateTime now) {
        ProgramReservation r = reservationRepository
                .findCheckoutByIdAndMemberIdForUpdate(reservationId, memberId)
                .orElseThrow(() -> new BaseException("예약을 찾을 수 없습니다.", ErrorCode.ENTITY_NOT_FOUND));

        if (!r.isHold()) return false;

        LocalDateTime expiresAt = r.getHoldExpiresAt();
        if (expiresAt == null || !expiresAt.isBefore(now)) return false;

        r.expire();
        ProgramSlot slot = programSlotRepository
                .findByIdAndProgramIdForUpdate(
                        r.getSlot().getId(), r.getProgram().getId())
                .orElseThrow(() -> new BaseException("존재하지 않는 슬롯입니다.", ErrorCode.ENTITY_NOT_FOUND));
        slot.increaseCapacity(r.getHeadcount());
        return true;
    }
}
