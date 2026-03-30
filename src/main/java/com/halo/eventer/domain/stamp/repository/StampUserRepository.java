package com.halo.eventer.domain.stamp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.halo.eventer.domain.stamp.StampUser;

public interface StampUserRepository extends JpaRepository<StampUser, Long> {
    Optional<StampUser> findByUuid(String uuid);

    boolean existsByStampIdAndPhone(Long stampId, String phone);

    Optional<StampUser> findByStampIdAndPhoneAndName(Long stampId, String phone, String name);

    @Query("select su from StampUser su where su.stamp.id = :stampId")
    List<StampUser> findAllByStampId(Long stampId);

    @Query(
            """
           select su
           from StampUser su
           where su.stamp.id = :stampId
             and (:cleared is null or su.finished = :cleared)
           """)
    Page<StampUser> findByStampId(@Param("stampId") Long stampId, @Param("cleared") Boolean cleared, Pageable pageable);

    @Query(
            value =
                    """
            select su
            from StampUser su
            where su.stamp.id = :stampId
              and ( :q is null or :q = ''
                    or lower(su.name) like lower(concat('%', :q, '%'))
                    or su.phone       like concat('%', :q, '%') )
              and ( :cleared is null or su.finished = :cleared )
            """,
            countQuery =
                    """
            select count(su)
            from StampUser su
            where su.stamp.id = :stampId
              and ( :q is null or :q = ''
                    or lower(su.name) like lower(concat('%', :q, '%'))
                    or su.phone       like concat('%', :q, '%') )
              and ( :cleared is null or su.finished = :cleared )
            """)
    Page<StampUser> searchUsers(
            @Param("stampId") Long stampId, @Param("q") String q, @Param("cleared") Boolean cleared, Pageable pageable);

    @Query(
            """
        select su
        from StampUser su
        join fetch su.stamp s
        left join fetch su.userMissions um
        left join fetch um.mission m
        where su.id = :userId
          and s.id  = :stampId
        """)
    Optional<StampUser> findByIdAndStampIdWithMissions(@Param("userId") Long userId, @Param("stampId") Long stampId);

    @Query(
            """
        select su
        from StampUser su
        join fetch su.stamp s
        left join fetch su.userMissions um
        left join fetch um.mission m
        where su.uuid = :uuid
          and s.id    = :stampId
        """)
    Optional<StampUser> findByUuidAndStampIdWithMissions(@Param("uuid") String uuid, @Param("stampId") Long stampId);

    // Member 기반 조회 메서드
    Optional<StampUser> findByMemberIdAndStampId(Long memberId, Long stampId);

    boolean existsByMemberIdAndStampId(Long memberId, Long stampId);

    List<StampUser> findAllByMemberId(Long memberId);

    @Query(
            """
        select su
        from StampUser su
        join fetch su.stamp s
        left join fetch su.userMissions um
        left join fetch um.mission m
        where su.member.id = :memberId
          and s.id = :stampId
        """)
    Optional<StampUser> findByMemberIdAndStampIdWithMissions(
            @Param("memberId") Long memberId, @Param("stampId") Long stampId);
}
