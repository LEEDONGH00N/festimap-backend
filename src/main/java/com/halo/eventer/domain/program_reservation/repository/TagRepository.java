package com.halo.eventer.domain.program_reservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.halo.eventer.domain.program_reservation.entity.content.Tag;

public interface TagRepository extends JpaRepository<Tag, Long> {}
