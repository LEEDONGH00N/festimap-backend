package com.halo.eventer.domain.program_reservation.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.halo.eventer.domain.program_reservation.entity.additional.ProgramAdditionalField;

public interface ProgramAdditionalFieldRepository extends JpaRepository<ProgramAdditionalField, Long> {

    List<ProgramAdditionalField> findAllByProgramIdOrderBySortOrder(Long programId);

    List<ProgramAdditionalField> findAllByProgramId(Long programId);

    @Query("select min(f.sortOrder) from ProgramAdditionalField f where f.program.id = :programId")
    Optional<Integer> findMinSortOrderByProgramId(@Param("programId") Long programId);

    List<ProgramAdditionalField> findAllByProgramIdAndIsActiveTrueOrderBySortOrder(Long programId);

    @Query("select f from ProgramAdditionalField f"
            + " where f.program.festival.id = :festivalId"
            + " order by f.program.id, f.sortOrder")
    List<ProgramAdditionalField> findAllByFestivalId(@Param("festivalId") Long festivalId);

    void deleteAllByProgramId(Long programId);
}
