package com.halo.eventer.domain.program_reservation.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import jakarta.persistence.LockModeType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import com.halo.eventer.domain.program_reservation.entity.reservation.ProgramReservation;
import com.halo.eventer.domain.program_reservation.entity.reservation.ProgramReservationStatus;
import io.micrometer.common.lang.Nullable;

public interface ProgramReservationRepository
        extends JpaRepository<ProgramReservation, Long>, JpaSpecificationExecutor<ProgramReservation> {
    @Query("SELECT COUNT(r) FROM ProgramReservation r WHERE r.slot.template.id = :templateId AND r.status IN :statuses")
    long countByTemplateIdAndStatusIn(
            @Param("templateId") Long templateId, @Param("statuses") List<ProgramReservationStatus> statuses);

    @Query(
            """
                        select (count(r) > 0)
                        from ProgramReservation r
                        join r.slot s
                        where s.template.id = :templateId
                    """)
    boolean existsAnyByTemplateId(@Param("templateId") Long templateId);

    boolean existsByProgramId(Long programId);

    boolean existsByProgramIdAndStatusNot(Long programId, ProgramReservationStatus status);

    List<ProgramReservation> findAllByProgramIdAndStatus(Long programId, ProgramReservationStatus status);

    @Override
    @EntityGraph(attributePaths = {"program", "slot"})
    Page<ProgramReservation> findAll(@Nullable Specification<ProgramReservation> spec, Pageable pageable);

    @EntityGraph(attributePaths = {"program", "program.festival", "slot"})
    Optional<ProgramReservation> findByMemberIdAndIdempotencyKey(Long memberId, String idempotencyKey);

    @EntityGraph(attributePaths = {"program", "slot"})
    Optional<ProgramReservation> findWithProgramByMemberIdAndIdempotencyKey(Long memberId, String idempotencyKey);

    @Query(
            """
                select r
                from ProgramReservation r
                join fetch r.program p
                join fetch p.festival
                join fetch r.slot
                where r.id = :reservationId
                and r.member.id = :memberId
            """)
    Optional<ProgramReservation> findByIdAndMemberId(
            @Param("reservationId") Long reservationId, @Param("memberId") Long memberId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(
            """
        select r from ProgramReservation r
        join fetch r.slot s
        join fetch r.program p
        where r.id = :reservationId
          and r.member.id = :memberId
    """)
    Optional<ProgramReservation> findByIdAndMemberIdForUpdate(
            @Param("reservationId") Long reservationId, @Param("memberId") Long memberId);

    @Query(
            """
        select r.id
        from ProgramReservation r
        where r.status = com.halo.eventer.domain.program_reservation.entity.reservation.ProgramReservationStatus.HOLD
          and r.holdExpiresAt < :now
        order by r.holdExpiresAt asc
    """)
    List<Long> findExpiredHoldIds(@Param("now") LocalDateTime now, Pageable pageable);

    @Query(
            """
                select coalesce(sum(r.peopleCount), 0)
                from ProgramReservation r
                where r.member.id = :memberId
                  and r.program.id = :programId
                  and (
                        (r.status = com.halo.eventer.domain.program_reservation.entity.reservation.ProgramReservationStatus.HOLD
                         and r.holdExpiresAt is not null
                         and r.holdExpiresAt >= :now)
                     or r.status = com.halo.eventer.domain.program_reservation.entity.reservation.ProgramReservationStatus.CONFIRMED
                  )
            """)
    int sumActiveHeadcountByMemberAndProgram(
            @Param("memberId") Long memberId, @Param("programId") Long programId, @Param("now") LocalDateTime now);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(
            """
                select r
                from ProgramReservation r
                join fetch r.slot s
                where r.id = :id
            """)
    Optional<ProgramReservation> findByIdForUpdate(@Param("id") Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(
            """
        select r from ProgramReservation r
        join fetch r.slot s
        join fetch r.program p
        join fetch p.festival f
        where r.id = :reservationId
          and r.member.id = :memberId
    """)
    Optional<ProgramReservation> findCheckoutByIdAndMemberIdForUpdate(Long reservationId, Long memberId);

    List<ProgramReservation> findAllByMemberIdAndStatusInOrderByConfirmedAtDescIdDesc(
            Long memberId, List<ProgramReservationStatus> statuses);

    @EntityGraph(attributePaths = {"program", "program.festival", "slot"})
    List<ProgramReservation> findAllByMemberIdAndProgramFestivalIdAndStatusInOrderByConfirmedAtDescIdDesc(
            Long memberId, Long festivalId, List<ProgramReservationStatus> statuses);
}
