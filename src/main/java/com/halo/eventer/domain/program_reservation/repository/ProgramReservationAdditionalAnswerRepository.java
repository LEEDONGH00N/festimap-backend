package com.halo.eventer.domain.program_reservation.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.halo.eventer.domain.program_reservation.entity.additional.ProgramReservationAdditionalAnswer;

public interface ProgramReservationAdditionalAnswerRepository
        extends JpaRepository<ProgramReservationAdditionalAnswer, Long> {

    boolean existsByFieldId(Long fieldId);

    boolean existsByOptionId(Long optionId);

    List<ProgramReservationAdditionalAnswer> findAllByReservationIdIn(List<Long> reservationIds);

    void deleteAllByReservationIdIn(List<Long> reservationIds);
}
