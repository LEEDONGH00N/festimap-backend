package com.halo.eventer.domain.alimtalk.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.halo.eventer.domain.alimtalk.AlimtalkOutbox;
import com.halo.eventer.domain.alimtalk.AlimtalkOutboxStatus;

public interface AlimtalkRepository extends JpaRepository<AlimtalkOutbox, Long> {

    Page<AlimtalkOutbox> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<AlimtalkOutbox> findByStatusOrderByCreatedAtDesc(AlimtalkOutboxStatus status, Pageable pageable);
}
