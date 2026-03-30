package com.halo.eventer.domain.member.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.halo.eventer.domain.member.Member;
import com.halo.eventer.domain.member.MemberRole;
import com.halo.eventer.domain.member.exception.MemberNotFoundException;
import com.halo.eventer.domain.member.repository.MemberRepository;
import com.halo.eventer.domain.stamp.*;
import com.halo.eventer.domain.stamp.dto.mission.response.MissionBoardResDto;
import com.halo.eventer.domain.stamp.dto.mission.response.MissionExtraInfoSummaryResDto;
import com.halo.eventer.domain.stamp.dto.mission.response.MissionTemplateResDto;
import com.halo.eventer.domain.stamp.dto.stamp.response.ButtonResDto;
import com.halo.eventer.domain.stamp.dto.stampUser.response.PrizeClaimQrResDto;
import com.halo.eventer.domain.stamp.exception.*;
import com.halo.eventer.domain.stamp.repository.*;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberStampTourService {

    private final StampUserRepository stampUserRepository;
    private final StampRepository stampRepository;
    private final MemberRepository memberRepository;
    private final MissionRepository missionRepository;
    private final MissionDetailsTemplateRepository templateRepository;
    private final UserMissionRepository userMissionRepository;

    /**
     * 스탬프 투어 참여
     */
    @Transactional
    public void participate(Long memberId, Long festivalId, Long stampId, int participantCount, String extraText) {
        Member member = memberRepository
                .findByIdAndRole(memberId, MemberRole.VISITOR)
                .orElseThrow(MemberNotFoundException::new);

        Stamp stamp = ensureStamp(festivalId, stampId);
        stamp.validateActivation();

        if (stampUserRepository.existsByMemberIdAndStampId(memberId, stampId)) {
            throw new StampUserAlreadyExistsException();
        }

        StampUser stampUser = StampUser.createForMember(member, participantCount, extraText);
        stampUser.addStamp(stamp);
        stamp.assignAllMissionsTo(stampUser);
        stampUserRepository.save(stampUser);
    }

    /**
     * 미션 보드 조회
     */
    public MissionBoardResDto getMissionBoard(Long memberId, Long festivalId, Long stampId) {
        ensureStamp(festivalId, stampId);
        StampUser user = loadStampUserOrThrow(memberId, stampId);
        List<UserMission> userMissions = filterUserMissionOnlyShowing(user);
        return MissionBoardResDto.from(userMissions, user.getFinished());
    }

    /**
     * 미션 상세 조회
     */
    public MissionTemplateResDto getMissionDetails(Long memberId, Long festivalId, Long stampId, Long missionId) {
        ensureStamp(festivalId, stampId);
        Mission mission = loadMissionOrThrow(missionId);
        MissionDetailsTemplate template = loadMissionDetailsTemplate(missionId);
        UserMission userMission = loadUserMissionByMember(memberId, stampId, missionId);

        return new MissionTemplateResDto(
                mission.getId(),
                mission.getShowTitle() ? mission.getTitle() : null,
                template.getMediaSpec(),
                template.getMediaUrl(),
                template.getDesignLayout(),
                userMission.getSuccessCount(),
                mission.getShowRequiredSuccessCount() ? mission.getRequiredSuccessCount() : null,
                MissionExtraInfoSummaryResDto.fromEntities(template.getExtraInfo()),
                template.getButtonLayout(),
                ButtonResDto.fromEntities(template.getButtons()));
    }

    /**
     * QR 미션 인증
     */
    @Transactional
    public void completeMissionByQr(Long memberId, Long festivalId, Long stampId, Long missionId) {
        ensureStamp(festivalId, stampId);
        loadMissionOrThrow(missionId);
        loadStampUserOrThrow(memberId, stampId);
        UserMission um = loadUserMissionByMember(memberId, stampId, missionId);
        um.increaseSuccess(missionId);
    }

    /**
     * 상품 수령 QR 정보 조회
     */
    public PrizeClaimQrResDto getPrizeQrInfo(Long memberId, Long festivalId, Long stampId) {
        ensureStamp(festivalId, stampId);
        StampUser stampUser = loadStampUserOrThrow(memberId, stampId);
        Member member = stampUser.getMember();

        return PrizeClaimQrResDto.from(
                member.getName(), member.getPhone(), stampUser.getParticipantCount(), stampUser.getExtraText());
    }

    /**
     * 내 참여 목록 조회
     */
    public List<StampUser> getMyParticipations(Long memberId) {
        return stampUserRepository.findAllByMemberId(memberId);
    }

    /**
     * 투어 완료 확인 및 처리
     */
    @Transactional
    public boolean checkAndFinishTour(Long memberId, Long festivalId, Long stampId) {
        ensureStamp(festivalId, stampId);
        StampUser stampUser = loadStampUserOrThrow(memberId, stampId);
        return stampUser.canFinishTour();
    }

    /**
     * 상품 수령 처리
     */
    @Transactional
    public void receivePrize(Long memberId, Long festivalId, Long stampId, String prizeName) {
        ensureStamp(festivalId, stampId);
        StampUser stampUser = stampUserRepository
                .findByMemberIdAndStampId(memberId, stampId)
                .orElseThrow(() -> new StampUserNotFoundException(memberId.toString()));
        stampUser.updateReceivedPrizeName(prizeName);
    }

    private List<UserMission> filterUserMissionOnlyShowing(StampUser user) {
        return user.getUserMissions().stream()
                .filter(um -> um.getMission() != null && um.getMission().getShowMission())
                .sorted(Comparator.comparing(um -> um.getMission().getId()))
                .toList();
    }

    private Stamp ensureStamp(Long festivalId, Long stampId) {
        Stamp stamp = loadStampOrThrow(stampId);
        stamp.ensureStampInFestival(festivalId);
        return stamp;
    }

    private Stamp loadStampOrThrow(Long stampId) {
        return stampRepository.findById(stampId).orElseThrow(() -> new StampNotFoundException(stampId));
    }

    private Mission loadMissionOrThrow(Long missionId) {
        return missionRepository.findById(missionId).orElseThrow(() -> new MissionNotFoundException(missionId));
    }

    private StampUser loadStampUserOrThrow(Long memberId, Long stampId) {
        return stampUserRepository
                .findByMemberIdAndStampIdWithMissions(memberId, stampId)
                .orElseThrow(() -> new StampUserNotFoundException(memberId.toString()));
    }

    private MissionDetailsTemplate loadMissionDetailsTemplate(Long missionId) {
        return templateRepository
                .findByMissionId(missionId)
                .orElseThrow(() -> new MissionDetailsTemplateNotFoundException(missionId));
    }

    private UserMission loadUserMissionByMember(Long memberId, Long stampId, Long missionId) {
        return userMissionRepository
                .findByMemberIdAndStampIdAndMissionId(memberId, stampId, missionId)
                .orElseThrow(() -> new IllegalStateException("USER_MISSION_NOT_FOUND"));
    }
}
