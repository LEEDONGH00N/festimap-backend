package com.halo.eventer.domain.stamp;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;

import com.halo.eventer.domain.festival.Festival;
import com.halo.eventer.domain.stamp.dto.stamp.enums.AuthMethod;
import com.halo.eventer.domain.stamp.dto.stamp.enums.JoinVerificationMethod;
import com.halo.eventer.domain.stamp.dto.stamp.enums.MissionDetailsDesignLayout;
import com.halo.eventer.domain.stamp.dto.stamp.enums.PrizeExchangeImgType;
import com.halo.eventer.domain.stamp.exception.StampClosedException;
import com.halo.eventer.domain.stamp.exception.StampNotInFestivalException;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class Stamp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private Boolean active = true;

    @Enumerated(EnumType.STRING)
    private JoinVerificationMethod joinVerificationMethod = JoinVerificationMethod.NONE;

    private Integer finishCount = 0;

    private String prizeReceiptAuthPassword = "";

    private Boolean showStamp = true;

    @Enumerated(EnumType.STRING)
    private AuthMethod authMethod = AuthMethod.TAG_SCAN;

    private Integer missionCount = 0;

    private Integer maxParticipantCount = 1;

    @Column(columnDefinition = "TEXT")
    private String extraInfoTemplate;

    private String mainColor;
    private String subColor;
    private String backgroundColor;
    private String backgroundSubColor;

    @Enumerated(EnumType.STRING)
    private PrizeExchangeImgType prizeExchangeImgType = PrizeExchangeImgType.DEFAULT;

    private String prizeExchangeCustomImgUrl;

    @Enumerated(EnumType.STRING)
    private MissionDetailsDesignLayout defaultDetailLayout = MissionDetailsDesignLayout.CARD;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "festival_id")
    private Festival festival;

    @OneToMany(mappedBy = "stamp", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StampNotice> stampNotices = new ArrayList<>();

    @OneToMany(mappedBy = "stamp", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PageTemplate> templates = new ArrayList<>();

    @OneToMany(mappedBy = "stamp", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ParticipateGuide> participationGuides = new ArrayList<>();

    @OneToMany(mappedBy = "stamp", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StampUser> stampUsers = new ArrayList<>();

    @OneToMany(mappedBy = "stamp", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Mission> missions = new ArrayList<>();

    @OneToMany(mappedBy = "stamp", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StampMissionPrize> prizes = new ArrayList<>();

    private Stamp(Festival festival) {
        this.festival = festival;
        festival.getStamps().add(this);
    }

    public Stamp(String title, boolean showStamp) {
        this.title = title;
        this.showStamp = showStamp;
    }

    public void assignAllMissionsTo(StampUser stampUser) {
        List<UserMission> userMissions =
                missions.stream().map(m -> UserMission.create(m, stampUser)).toList();
        stampUser.assignUserMissions(userMissions);
    }

    public void switchActivation() {
        active = !active;
    }

    public void defineFinishCnt(int cnt) {
        this.finishCount = cnt;
    }

    public void validateActivation() {
        if (!active) {
            throw new StampClosedException(id);
        }
    }

    private void registerTo(Festival festival) {
        this.festival = festival;
        festival.getStamps().add(this);
    }

    public void changeBasicSettings(
            boolean activation,
            String newTitle,
            AuthMethod authMethod,
            String prizeReceiptAuthPassword,
            String mainColor,
            String subColor,
            String backgroundColor,
            String backgroundSubColor) {
        this.title = newTitle;
        this.active = activation;
        this.authMethod = authMethod;
        this.prizeReceiptAuthPassword = prizeReceiptAuthPassword;
        this.mainColor = mainColor;
        this.subColor = subColor;
        this.backgroundColor = backgroundColor;
        this.backgroundSubColor = backgroundSubColor;
    }

    public void ensureStampInFestival(long festivalId) {
        if (this.festival == null || !this.festival.getId().equals(festivalId)) {
            throw new StampNotInFestivalException(this.getId(), festivalId);
        }
    }

    public void updateJoinCondition(
            JoinVerificationMethod method, Integer maxParticipantCount, String extraInfoTemplate) {
        this.joinVerificationMethod = method;
        this.maxParticipantCount = maxParticipantCount;
        this.extraInfoTemplate = extraInfoTemplate;
    }

    public boolean willShowStamp() {
        return showStamp && active;
    }

    public void updateBasic(int missionCount, MissionDetailsDesignLayout layout) {
        this.missionCount = missionCount;
        this.defaultDetailLayout = layout;
    }

    public void updatePrizeTicketImg(PrizeExchangeImgType prizeExchangeImgType, String prizeExchangeCustomImgUrl) {
        if (prizeExchangeImgType.equals(PrizeExchangeImgType.DEFAULT)) {
            this.prizeExchangeImgType = prizeExchangeImgType;
            return;
        }
        this.prizeExchangeImgType = prizeExchangeImgType;
        this.prizeExchangeCustomImgUrl = prizeExchangeCustomImgUrl;
    }

    public void toggleShowStamp(boolean showStamp) {
        this.showStamp = showStamp;
    }

    public void updateMissionCount() {
        this.missionCount = missions.size();
    }

    public static Stamp create(Festival festival) {
        return new Stamp(festival);
    }

    public static Stamp createWith(Festival festival, String title, boolean show) {
        Stamp stamp = new Stamp(title, show);
        stamp.registerTo(festival);
        return stamp;
    }
}
