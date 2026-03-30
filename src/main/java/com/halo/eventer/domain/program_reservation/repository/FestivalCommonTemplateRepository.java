package com.halo.eventer.domain.program_reservation.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.halo.eventer.domain.program_reservation.entity.content.FestivalCommonTemplate;

public interface FestivalCommonTemplateRepository extends JpaRepository<FestivalCommonTemplate, Long> {

    List<FestivalCommonTemplate> findAllByFestivalIdOrderBySortOrder(Long festivalId);

    void deleteAllByFestivalId(Long festivalId);
}
