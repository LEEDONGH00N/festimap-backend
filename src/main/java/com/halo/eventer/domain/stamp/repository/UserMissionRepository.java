package com.halo.eventer.domain.stamp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.halo.eventer.domain.stamp.UserMission;

public interface UserMissionRepository extends JpaRepository<UserMission, Long> {

    @Query(
            """
        select um
        from UserMission um
        join um.stampUser su
        join um.mission m
        where su.uuid = :uuid
          and su.stamp.id = :stampId
          and m.id = :missionId
    """)
    Optional<UserMission> findByUserUuidAndStampIdAndMissionId(
            @Param("uuid") String uuid, @Param("stampId") Long stampId, @Param("missionId") Long missionId);

    @Query(
            """
        select um
        from UserMission um
        join um.stampUser su
        join um.mission m
        where su.member.id = :memberId
          and su.stamp.id = :stampId
          and m.id = :missionId
    """)
    Optional<UserMission> findByMemberIdAndStampIdAndMissionId(
            @Param("memberId") Long memberId, @Param("stampId") Long stampId, @Param("missionId") Long missionId);
}
