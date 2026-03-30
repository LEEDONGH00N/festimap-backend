package com.halo.eventer.domain.member;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;

import com.halo.eventer.domain.stamp.StampUser;
import com.halo.eventer.global.common.BaseTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Member extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50)
    private String loginId;

    private String password;

    @Column(length = 20)
    private String phone;

    @Column(length = 50)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberStatus status = MemberStatus.ACTIVE;

    @Column(name = "terms_agreed", nullable = false)
    private Boolean termsAgreed = false;

    @Column(name = "privacy_agreed", nullable = false)
    private Boolean privacyAgreed = false;

    @Column(name = "marketing_agreed")
    private Boolean marketingAgreed = false;

    // 설문 정보 (회원가입 시 선택)
    @Column(name = "residence_type", length = 30)
    private String residenceType;

    @Column(name = "residence_region", length = 50)
    private String residenceRegion;

    @Column(name = "residence_district", length = 50)
    private String residenceDistrict;

    @Column(name = "visit_type", length = 30)
    private String visitType;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 10)
    private Gender gender;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    // 소셜 로그인 정보
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private SocialProvider provider;

    @Column(name = "provider_id")
    private String providerId;

    // 기업 정보 (AGENCY)
    @Column(name = "company_name", length = 100)
    private String companyName;

    @Column(name = "company_email", length = 100)
    private String companyEmail;

    @Column(name = "company_phone", length = 20)
    private String companyPhone;

    @Column(name = "manager_position", length = 50)
    private String managerPosition;

    @OneToMany(mappedBy = "member", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Authority> authorities = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<StampUser> stampUsers = new ArrayList<>();

    public Member(String loginId, String password) {
        this.loginId = loginId;
        this.password = password;
        this.role = MemberRole.SUPER_ADMIN;
        this.status = MemberStatus.ACTIVE;
    }

    public static Member createVisitor(String phone, String name, SocialProvider provider, String providerId) {
        Member member = new Member();
        member.phone = phone;
        member.name = name;
        member.provider = provider;
        member.providerId = providerId;
        member.role = MemberRole.VISITOR;
        member.status = MemberStatus.ACTIVE;
        return member;
    }

    public static Member createVisitor(
            String phone,
            String name,
            SocialProvider provider,
            String providerId,
            boolean termsAgreed,
            boolean privacyAgreed,
            boolean marketingAgreed) {
        Member member = createVisitor(phone, name, provider, providerId);
        member.termsAgreed = termsAgreed;
        member.privacyAgreed = privacyAgreed;
        member.marketingAgreed = marketingAgreed;
        return member;
    }

    public static Member createAgency(
            String loginId,
            String password,
            String companyEmail,
            String companyName,
            String managerName,
            String managerPosition,
            String managerPhone) {
        Member member = new Member();
        member.loginId = loginId;
        member.password = password;
        member.companyEmail = companyEmail;
        member.companyName = companyName;
        member.name = managerName;
        member.managerPosition = managerPosition;
        member.phone = managerPhone;
        member.role = MemberRole.AGENCY;
        member.status = MemberStatus.ACTIVE;
        return member;
    }

    public void setRoles(List<Authority> roles) {
        this.authorities = roles;
        roles.forEach(o -> o.setMember(this));
    }

    public void addAuthority(Authority authority) {
        this.authorities.add(authority);
        authority.setMember(this);
    }

    public List<String> getRoleNames() {
        return List.of("ROLE_" + this.role.name());
    }

    public boolean isVisitor() {
        return this.role == MemberRole.VISITOR;
    }

    public boolean isActive() {
        return this.status == MemberStatus.ACTIVE;
    }

    public void updateStatus(MemberStatus status) {
        this.status = status;
    }

    public void updateMarketingConsent(boolean marketingAgreed) {
        this.marketingAgreed = marketingAgreed;
    }

    public void updateSurveyInfo(
            String residenceType,
            String residenceRegion,
            String residenceDistrict,
            String visitType,
            Gender gender,
            LocalDate birthDate) {
        this.residenceType = residenceType;
        this.residenceRegion = residenceRegion;
        this.residenceDistrict = residenceDistrict;
        this.visitType = visitType;
        this.gender = gender;
        this.birthDate = birthDate;
    }

    public void addStampUser(StampUser stampUser) {
        this.stampUsers.add(stampUser);
    }

    public void updateCompanyInfo(
            String companyEmail, String companyName, String managerName, String managerPosition, String managerPhone) {
        this.companyEmail = companyEmail;
        this.companyName = companyName;
        this.name = managerName;
        this.managerPosition = managerPosition;
        this.phone = managerPhone;
    }

    public void updateProfile(String managerName, String managerPosition, String email) {
        this.name = managerName;
        this.managerPosition = managerPosition;
        this.companyEmail = email;
    }

    public void updateCompany(String companyName, String companyEmail, String companyPhone) {
        this.companyName = companyName;
        this.companyEmail = companyEmail;
        this.companyPhone = companyPhone;
    }

    public void changePassword(String newEncodedPassword) {
        this.password = newEncodedPassword;
    }
}
