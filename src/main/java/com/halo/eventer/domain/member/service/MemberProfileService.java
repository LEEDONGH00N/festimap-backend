package com.halo.eventer.domain.member.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.halo.eventer.domain.member.Member;
import com.halo.eventer.domain.member.MemberRole;
import com.halo.eventer.domain.member.dto.MarketingConsentRequest;
import com.halo.eventer.domain.member.dto.MemberInfoResponse;
import com.halo.eventer.domain.member.dto.MemberSurveyRequest;
import com.halo.eventer.domain.member.exception.MemberNotFoundException;
import com.halo.eventer.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberProfileService {

    private final MemberRepository memberRepository;

    /**
     * 내 정보 조회
     */
    public MemberInfoResponse getMyInfo(Long memberId) {
        Member member = memberRepository
                .findByIdAndRole(memberId, MemberRole.VISITOR)
                .orElseThrow(MemberNotFoundException::new);

        return MemberInfoResponse.from(member);
    }

    /**
     * 마케팅 동의 설정 변경
     */
    @Transactional
    public void updateMarketingConsent(Long memberId, MarketingConsentRequest request) {
        Member member = memberRepository
                .findByIdAndRole(memberId, MemberRole.VISITOR)
                .orElseThrow(MemberNotFoundException::new);

        member.updateMarketingConsent(request.isMarketingAgreed());
    }

    /**
     * 설문 정보 업데이트
     */
    @Transactional
    public void updateSurvey(Long memberId, MemberSurveyRequest request) {
        Member member = memberRepository
                .findByIdAndRole(memberId, MemberRole.VISITOR)
                .orElseThrow(MemberNotFoundException::new);

        member.updateSurveyInfo(
                request.getResidenceType(),
                request.getResidenceRegion(),
                request.getResidenceDistrict(),
                request.getVisitType(),
                request.getGender(),
                request.getBirthDate());
    }
}
