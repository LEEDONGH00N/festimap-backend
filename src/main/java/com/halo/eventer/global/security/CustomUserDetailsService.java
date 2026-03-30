package com.halo.eventer.global.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import com.halo.eventer.domain.member.Member;
import com.halo.eventer.domain.member.exception.MemberNotFoundException;
import com.halo.eventer.domain.member.repository.MemberRepository;
import com.halo.eventer.domain.stamp.exception.StampUserNotFoundException;
import com.halo.eventer.domain.stamp.repository.StampUserRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;
    private final StampUserRepository stampUserRepository;

    @Override
    public UserDetails loadUserByUsername(String loginId) {
        return new CustomUserDetails(memberRepository.findByLoginId(loginId).orElseThrow(MemberNotFoundException::new));
    }

    public UserDetails loadUserByUuid(String uuid) {
        return new CustomStampUserDetails(
                stampUserRepository.findByUuid(uuid).orElseThrow(StampUserNotFoundException::new));
    }

    public UserDetails loadMemberById(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
        return new CustomUserDetails(member);
    }
}
