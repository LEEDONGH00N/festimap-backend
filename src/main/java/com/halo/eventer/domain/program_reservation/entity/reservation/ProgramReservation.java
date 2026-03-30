package com.halo.eventer.domain.program_reservation.entity.reservation;

import java.time.LocalDateTime;
import jakarta.persistence.*;

import com.halo.eventer.domain.member.Member;
import com.halo.eventer.domain.program_reservation.Program;
import com.halo.eventer.domain.program_reservation.entity.slot.ProgramSlot;
import com.halo.eventer.global.common.BaseTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        indexes = {
            @Index(name = "idx_reservation_status_created", columnList = "status, created_at DESC"),
            @Index(name = "idx_reservation_status_hold_expires", columnList = "status, hold_expires_at")
        },
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "uk_reservation_member_idempotency",
                    columnNames = {"member_id", "idempotency_key"})
        })
public class ProgramReservation extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer peopleCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProgramReservationStatus status;

    private LocalDateTime holdExpiresAt;

    @Column(length = 50)
    private String bookerName;

    @Column(length = 20)
    private String bookerPhone;

    @Column(length = 50)
    private String visitorName;

    @Column(length = 20)
    private String visitorPhone;

    @Column(length = 64)
    private String idempotencyKey;

    private LocalDateTime confirmedAt;

    private LocalDateTime cancelledAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "program_id", nullable = false)
    private Program program;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "slot_id", nullable = false)
    private ProgramSlot slot;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    public boolean isHold() {
        return this.status == ProgramReservationStatus.HOLD;
    }

    public boolean isExpired() {
        return this.status == ProgramReservationStatus.EXPIRED;
    }

    public int getHeadcount() {
        return this.peopleCount;
    }

    public static ProgramReservation hold(
            Program program, ProgramSlot slot, Member member, Integer peopleCount, LocalDateTime holdExpiresAt) {
        ProgramReservation r = new ProgramReservation();
        r.program = program;
        r.slot = slot;
        r.member = member;
        r.peopleCount = peopleCount;
        r.status = ProgramReservationStatus.HOLD;
        r.holdExpiresAt = holdExpiresAt;
        return r;
    }

    public void confirm() {
        this.status = ProgramReservationStatus.CONFIRMED;
        this.confirmedAt = LocalDateTime.now();
    }

    public void expire() {
        this.status = ProgramReservationStatus.EXPIRED;
    }

    public void cancel() {
        this.status = ProgramReservationStatus.CANCELED;
        this.cancelledAt = LocalDateTime.now();
    }

    public void changeStatus(ProgramReservationStatus status) {
        this.status = status;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }

    public void setBookerInfo(String bookerName, String bookerPhone) {
        this.bookerName = bookerName;
        this.bookerPhone = bookerPhone;
    }

    public void setVisitorInfo(String visitorName, String visitorPhone) {
        this.visitorName = visitorName;
        this.visitorPhone = visitorPhone;
    }
}
