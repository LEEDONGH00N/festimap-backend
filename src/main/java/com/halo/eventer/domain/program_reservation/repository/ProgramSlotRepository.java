package com.halo.eventer.domain.program_reservation.repository;

import java.util.List;
import java.util.Optional;
import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.halo.eventer.domain.program_reservation.entity.slot.ProgramSlot;

public interface ProgramSlotRepository extends JpaRepository<ProgramSlot, Long> {
    void deleteAllByTemplateId(Long templateId);

    @Modifying
    @Query("delete from ProgramSlot s where s.program.id = :programId")
    void deleteAllByProgramId(@Param("programId") Long programId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(
            """
        select s
        from ProgramSlot s
        where s.pattern.id = :patternId
        order by s.id
    """)
    List<ProgramSlot> findAllByPatternIdForUpdate(@Param("patternId") Long patternId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from ProgramSlot s where s.id = :slotId")
    Optional<ProgramSlot> findByIdForUpdate(@Param("slotId") Long slotId);

    List<ProgramSlot> findAllByProgramIdOrderBySlotDateAscStartTimeAsc(Long programId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(
            """
        select s from ProgramSlot s
        join fetch s.program p
        where s.id = :slotId and p.id = :programId
    """)
    Optional<ProgramSlot> findByIdAndProgramIdForUpdate(
            @Param("slotId") Long slotId, @Param("programId") Long programId);
}
