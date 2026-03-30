package com.halo.eventer.domain.program_reservation.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.halo.eventer.domain.program_reservation.entity.additional.ProgramAdditionalFieldOption;

public interface ProgramAdditionalFieldOptionRepository extends JpaRepository<ProgramAdditionalFieldOption, Long> {

    List<ProgramAdditionalFieldOption> findAllByFieldId(Long fieldId);

    List<ProgramAdditionalFieldOption> findAllByFieldIdInOrderBySortOrder(List<Long> fieldIds);

    void deleteAllByFieldId(Long fieldId);

    @Modifying
    @Query("delete from ProgramAdditionalFieldOption o where o.field.program.id = :programId")
    void deleteAllByProgramId(@Param("programId") Long programId);

    @Query("select min(o.sortOrder) from ProgramAdditionalFieldOption o where o.field.id = :fieldId")
    Optional<Integer> findMinSortOrderByFieldId(@Param("fieldId") Long fieldId);

    List<ProgramAdditionalFieldOption> findAllByFieldIdInAndIsActiveTrueOrderBySortOrder(List<Long> fieldIds);
}
