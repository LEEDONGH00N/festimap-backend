package com.halo.eventer.domain.member.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.halo.eventer.domain.member.Authority;
import com.halo.eventer.domain.member.Member;
import com.halo.eventer.domain.member.MemberRole;
import com.halo.eventer.domain.member.SocialProvider;
import com.halo.eventer.domain.member.dto.SocialLoginResult;
import com.halo.eventer.domain.member.dto.TokenDto;
import com.halo.eventer.domain.member.dto.VisitorSignupRequest;
import com.halo.eventer.domain.member.exception.MemberNotActiveException;
import com.halo.eventer.domain.member.exception.PhoneAlreadyRegisteredException;
import com.halo.eventer.domain.member.oauth.SocialOAuthService;
import com.halo.eventer.domain.member.oauth.SocialUserInfo;
import com.halo.eventer.domain.member.repository.MemberRepository;
import com.halo.eventer.domain.sms.service.SmsVerificationService;
import com.halo.eventer.global.security.provider.JwtProvider;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VisitorAuthService {

    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;
    private final SmsVerificationService smsVerificationService;
    private final SocialOAuthService socialOAuthService;

    /**
     * 소셜 로그인 (accessToken 검증 방식)
     * - 프론트에서 받은 accessToken으로 소셜 API 호출하여 사용자 정보 조회
     * - 기존 회원: JWT 발급
     * - 신규: 회원가입 필요 응답
     */
    public SocialLoginResult socialLogin(SocialProvider provider, String accessToken) {
        SocialUserInfo userInfo = socialOAuthService.getUserInfo(provider, accessToken);

        Optional<Member> existingMember =
                memberRepository.findByProviderAndProviderId(userInfo.getProvider(), userInfo.getProviderId());

        if (existingMember.isPresent()) {
            Member member = existingMember.get();
            if (!member.isActive()) {
                throw new MemberNotActiveException();
            }
            String token = jwtProvider.createToken(member.getId().toString(), getRoleNames(member));
            return SocialLoginResult.loggedIn(token);
        }

        return SocialLoginResult.needSignup(userInfo.getProvider(), userInfo.getProviderId());
    }

    /**
     * 회원가입 완료
     * - 기본 정보 + 마케팅 동의 + 설문 정보를 한번에 처리
     */
    @Transactional
    public TokenDto completeSignup(VisitorSignupRequest request) {
        smsVerificationService.validateVerified(request.getPhone());

        if (memberRepository.existsByPhoneAndRole(request.getPhone(), MemberRole.VISITOR)) {
            throw new PhoneAlreadyRegisteredException();
        }

        Member member = Member.createVisitor(
                request.getPhone(),
                request.getName(),
                request.getProvider(),
                request.getProviderId(),
                request.isTermsAgreed(),
                request.isPrivacyAgreed(),
                request.isMarketingAgreed());
        member.addAuthority(Authority.builder().roleName("ROLE_VISITOR").build());

        // 설문 정보 설정
        member.updateSurveyInfo(
                request.getResidenceType(),
                request.getResidenceRegion(),
                request.getResidenceDistrict(),
                request.getVisitType(),
                request.getGender(),
                request.getBirthDate());

        memberRepository.save(member);
        smsVerificationService.consumeVerification(request.getPhone());

        return new TokenDto(jwtProvider.createToken(member.getId().toString(), getRoleNames(member)));
    }

    private List<String> getRoleNames(Member member) {
        List<String> roles = member.getRoleNames();
        if (roles.isEmpty()) {
            return Collections.singletonList("ROLE_VISITOR");
        }
        return roles;
    }
}
