package com.halo.eventer.domain.stamp;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import jakarta.persistence.*;

import com.halo.eventer.domain.member.Authority;
import com.halo.eventer.domain.member.Member;
import com.halo.eventer.domain.stamp.exception.UserMissionNotFoundException;
import com.halo.eventer.global.common.BaseTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(
        name = "stamp_user",
        indexes = {
            @Index(name = "idx_uuid", columnList = "uuid"),
            @Index(name = "idx_phone_name_stamp", columnList = "phone, name, stamp_id"),
            @Index(name = "idx_member_stamp", columnList = "member_id, stamp_id")
        })
public class StampUser extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String uuid;

    // Legacy fields - nullable for Member-based users
    private String phone;

    private String name;

    // New: Member 연관관계 (null이면 레거시 사용자)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private Boolean finished = false;

    private Integer participantCount = 1;

    private String extraText;

    private String receivedPrizeName;

    @Column(nullable = true)
    private String schoolNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stamp_id")
    private Stamp stamp;

    @OneToMany(mappedBy = "stampUser", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Authority> authorities;

    @OneToMany(mappedBy = "stampUser", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserMission> userMissions = new ArrayList<>();

    // TODO : Custom 엔티티를 없애고 schoolNo라는 필드를 StampUser에 정의하고 null을 하용하는 방식의 설계 고려 가능
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = true)
    private Custom custom;

    // Legacy constructor
    public StampUser(String encryptedPhone, String encryptedName, int participantCount) {
        this.uuid = UUID.randomUUID().toString();
        this.phone = encryptedPhone;
        this.name = encryptedName;
        this.finished = false;
        this.participantCount = participantCount;
    }

    // Legacy constructor with extraText
    public StampUser(String encryptedPhone, String encryptedName, int participantCount, String extraText) {
        this.uuid = UUID.randomUUID().toString();
        this.phone = encryptedPhone;
        this.name = encryptedName;
        this.finished = false;
        this.participantCount = participantCount;
        this.extraText = extraText;
    }

    // Member 기반 생성
    public static StampUser createForMember(Member member, int participantCount, String extraText) {
        StampUser stampUser = new StampUser();
        stampUser.member = member;
        stampUser.participantCount = participantCount;
        stampUser.extraText = extraText;
        stampUser.finished = false;
        return stampUser;
    }

    //    public StampUser(String encryptedPhone, String encryptedName, int participantCount, String schoolNo) {
    //        this.phone = encryptedPhone;
    //        this.name = encryptedName;
    //        this.isFinished = false;
    //        this.participantCount = participantCount;
    //        this.schoolNo = schoolNo;
    //    }

    public List<String> getRoleNames() {
        return authorities.stream().map(Authority::getRoleName).collect(Collectors.toList());
    }

    public void addStamp(Stamp stamp) {
        this.stamp = stamp;
        stamp.getStampUsers().add(this);
    }

    public void updateReceivedPrizeName(String receivedPrizeName) {
        this.receivedPrizeName = receivedPrizeName;
    }

    public void markAsFinished(boolean finished) {
        this.finished = finished;
    }

    public void setCustom(Custom custom) {
        this.custom = custom;
    }

    public void assignUserMissions(List<UserMission> userMissions) {
        this.userMissions = userMissions;
    }

    public boolean isMissionsAllCompleted() {
        return userMissions.stream().allMatch(UserMission::getComplete);
    }

    public void completeUserMission(Long userMissionId) {
        this.userMissions.stream()
                .filter(userMission -> userMission.getId().equals(userMissionId))
                .findFirst()
                .orElseThrow(() -> new UserMissionNotFoundException(userMissionId))
                .markAsComplete();
    }

    public boolean canFinishTour() {
        long completed = userMissions.stream().filter(UserMission::getComplete).count();
        if (completed >= stamp.getFinishCount()) {
            finished = true;
            return true;
        }
        return false;
    }

    public void updateInfo(String encodedName, String encodedPhone, String extraText, int participantCount) {
        this.name = encodedName;
        this.phone = encodedPhone;
        this.extraText = extraText;
        this.participantCount = participantCount;
    }
}
