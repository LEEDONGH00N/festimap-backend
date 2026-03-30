package com.halo.eventer.domain.program_reservation.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.halo.eventer.domain.program_reservation.entity.content.ProgramTag;

public interface ProgramTagRepository extends JpaRepository<ProgramTag, Long> {

    List<ProgramTag> findAllByProgramIdOrderBySortOrder(Long programId);

    void deleteAllByProgramId(Long programId);
}
