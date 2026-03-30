package com.halo.eventer.domain.festival;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;

import com.halo.eventer.domain.common.Address;
import com.halo.eventer.domain.duration.Duration;
import com.halo.eventer.domain.festival.dto.*;
import com.halo.eventer.domain.festival.enums.FestivalStatus;
import com.halo.eventer.domain.image.dto.FileDto;
import com.halo.eventer.domain.inquiry.Inquiry;
import com.halo.eventer.domain.lost_item.LostItem;
import com.halo.eventer.domain.manager.Manager;
import com.halo.eventer.domain.map.MapCategory;
import com.halo.eventer.domain.member.Member;
import com.halo.eventer.domain.missing_person.MissingPerson;
import com.halo.eventer.domain.notice.Notice;
import com.halo.eventer.domain.parking.ParkingManagement;
import com.halo.eventer.domain.splash.Splash;
import com.halo.eventer.domain.stamp.Stamp;
import com.halo.eventer.domain.widget.BaseWidget;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Festival {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String subAddress;

    private String mainColor;
    private String subColor;
    private String fontColor;
    private String backgroundColor;

    private String logo;

    @Embedded
    private Address address;

    private double latitude; // 위도
    private double longitude; // 경도

    private String programThumbnail; // 행사 대표 썸네일

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FestivalStatus status = FestivalStatus.DRAFT;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private Member owner;

    @OneToMany(mappedBy = "festival", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Notice> notices = new ArrayList<>();

    @OneToMany(
            mappedBy = "festival",
            fetch = FetchType.LAZY,
            cascade = {CascadeType.REMOVE, CascadeType.PERSIST})
    private List<MapCategory> mapCategories = new ArrayList<>();

    @OneToMany(mappedBy = "festival", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Duration> durations = new ArrayList<>();

    @OneToMany(mappedBy = "festival", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<BaseWidget> baseWidgets = new ArrayList<>();

    @OneToMany(mappedBy = "festival", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Inquiry> inquiries = new ArrayList<>();

    @OneToMany(mappedBy = "festival", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Stamp> stamps = new ArrayList<>();

    @OneToMany(mappedBy = "festival", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<LostItem> lostItems = new ArrayList<>();

    @OneToMany(mappedBy = "festival", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Manager> managers = new ArrayList<>();

    @OneToMany(mappedBy = "festival", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Splash> splashes = new ArrayList<>();

    @OneToMany(mappedBy = "festival", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<MissingPerson> missingPersons = new ArrayList<>();

    @OneToMany(mappedBy = "festival", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ParkingManagement> parkingManagements = new ArrayList<>();

    private Festival(String name, String subAddress) {
        this.name = name;
        this.subAddress = subAddress;
    }

    public static Festival from(FestivalCreateDto festivalCreateDto) {
        return new Festival(festivalCreateDto.getName(), festivalCreateDto.getSubDomain());
    }

    public void applyDefaultMapCategory() {
        MapCategory mapCategory = MapCategory.createFixedBooth();
        this.mapCategories.add(mapCategory);
        mapCategory.assignFestival(this);
    }

    public void updateFestival(FestivalCreateDto festivalCreateDto) {
        this.name = festivalCreateDto.getName();
        this.subAddress = festivalCreateDto.getSubDomain();
    }

    public void updateColor(ColorDto colorDto) {
        this.mainColor = colorDto.getMainColor();
        this.subColor = colorDto.getSubColor();
        this.fontColor = colorDto.getFontColor();
        this.backgroundColor = colorDto.getBackgroundColor();
    }

    public void updateLogo(FileDto fileDto) {
        this.logo = fileDto.getUrl();
    }

    public void updateLocation(FestivalLocationDto festivalLocationDto) {
        this.address = new Address(
                festivalLocationDto.getSido(),
                festivalLocationDto.getSigungu(),
                festivalLocationDto.getDongmyun(),
                festivalLocationDto.getRoadName(),
                festivalLocationDto.getRoadNumber(),
                festivalLocationDto.getBuildingName());
        this.longitude = festivalLocationDto.getLongitude();
        this.latitude = festivalLocationDto.getLatitude();
    }

    public void applyMapCategory(MapCategory mapCategory) {
        this.mapCategories.add(mapCategory);
    }

    public void applyMissingPerson(MissingPerson missingPerson) {
        this.missingPersons.add(missingPerson);
    }

    public void applyBaseWidget(BaseWidget baseWidget) {
        this.baseWidgets.add(baseWidget);
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateSubdomain(String subdomain) {
        this.subAddress = subdomain;
    }

    public boolean isActive() {
        return this.status == FestivalStatus.ACTIVE;
    }

    public void updateStatus(FestivalStatus status) {
        this.status = status;
    }

    public void assignOwner(Member owner) {
        this.owner = owner;
    }

    public boolean isOwnedBy(Long memberId) {
        return this.owner != null && this.owner.getId().equals(memberId);
    }

    public void setProgramThumbnail(String thumbnail) {
        this.programThumbnail = thumbnail;
    }
}
