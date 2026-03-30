package com.halo.eventer.domain.festival.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.halo.eventer.domain.festival.Festival;
import com.halo.eventer.domain.member.Member;

public interface FestivalRepository extends JpaRepository<Festival, Long> {

    Optional<Festival> findByName(String name);

    Optional<Festival> findBySubAddress(String subAddress);

    @Query("SELECT f FROM Festival f "
            + "JOIN FETCH f.baseWidgets w "
            + "WHERE f.id = :id "
            + "  AND TYPE(w) <> UpWidget ")
    Optional<Festival> findByIdWithWidgetsWithinPeriod(@Param("id") Long id);

    @Query("SELECT f FROM Festival f " + "JOIN FETCH f.mapCategories m " + "WHERE f.id = :id ")
    Optional<Festival> findByIdWithMapCategories(@Param("id") Long id);

    List<Festival> findByOwner(Member owner);

    boolean existsByIdAndOwnerId(Long festivalId, Long ownerId);
}
