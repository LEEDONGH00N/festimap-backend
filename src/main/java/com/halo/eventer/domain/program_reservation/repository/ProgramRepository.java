package com.halo.eventer.domain.program_reservation.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.halo.eventer.domain.program_reservation.Program;

public interface ProgramRepository extends JpaRepository<Program, Long> {

    List<Program> findAllByFestivalId(Long festivalId);

    List<Program> findAllByFestivalIdAndNameContaining(Long festivalId, String name);

    @Query(
            """
            select p
            from Program p
            where p.festival.id = :festivalId
              and p.isActive = true
              and (p.activeStartAt is null or p.activeStartAt <= :now)
              and (p.activeEndAt is null or p.activeEndAt >= :now)
            order by p.name asc
            """)
    List<Program> findAllVisibleForUser(@Param("festivalId") Long festivalId, @Param("now") LocalDateTime now);
}
