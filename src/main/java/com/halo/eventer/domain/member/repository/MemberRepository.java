package com.halo.eventer.domain.member.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.halo.eventer.domain.member.Member;
import com.halo.eventer.domain.member.MemberRole;
import com.halo.eventer.domain.member.SocialProvider;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByLoginId(String loginId);

    Optional<Member> findByPhone(String phone);

    Optional<Member> findByPhoneAndRole(String phone, MemberRole role);

    boolean existsByPhoneAndRole(String phone, MemberRole role);

    Optional<Member> findByIdAndRole(Long id, MemberRole role);

    Optional<Member> findByProviderAndProviderId(SocialProvider provider, String providerId);

    boolean existsByLoginId(String loginId);

    boolean existsByCompanyEmail(String companyEmail);
}
