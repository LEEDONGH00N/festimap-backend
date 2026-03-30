package com.halo.eventer.domain.member.dto;

import java.time.LocalDate;

import com.halo.eventer.domain.member.Gender;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberSurveyRequest {
    private String residenceType;
    private String residenceRegion;
    private String residenceDistrict;
    private String visitType;
    private Gender gender;
    private LocalDate birthDate;

    public MemberSurveyRequest(
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
}
