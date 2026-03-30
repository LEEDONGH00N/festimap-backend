package com.halo.eventer.domain.program_reservation.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.halo.eventer.domain.program_reservation.entity.slot.ProgramTimePattern;

public interface ProgramTimePatternRepository extends JpaRepository<ProgramTimePattern, Long> {

    List<ProgramTimePattern> findAllByTemplateIdOrderBySortOrder(Long templateId);

    void deleteAllByTemplateId(Long templateId);

    @Modifying
    @Query("delete from ProgramTimePattern p where p.template.program.id = :programId")
    void deleteAllByProgramId(@Param("programId") Long programId);
}
