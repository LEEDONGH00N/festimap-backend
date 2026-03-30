package com.halo.eventer.domain.fieldops.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.halo.eventer.domain.category.Category;
import com.halo.eventer.domain.fieldops.FieldOpsSession;

public interface FieldOpsSessionRepository extends JpaRepository<FieldOpsSession, Long> {

    Optional<FieldOpsSession> findByToken(String token);

    @Query("SELECT fs FROM FieldOpsSession fs " + "JOIN FETCH fs.category c " + "JOIN FETCH c.festivalModule m "
            + "JOIN FETCH m.festival f " + "WHERE fs.token = :token")
    Optional<FieldOpsSession> findByTokenWithFestival(@Param("token") String token);

    @Query("SELECT fs FROM FieldOpsSession fs " + "JOIN FETCH fs.category c " + "JOIN FETCH c.festivalModule m "
            + "JOIN FETCH m.festival f " + "WHERE fs.id = :sessionId")
    Optional<FieldOpsSession> findByIdWithFestival(@Param("sessionId") Long sessionId);

    Optional<FieldOpsSession> findByCategory(Category category);

    boolean existsByCategory(Category category);
}
