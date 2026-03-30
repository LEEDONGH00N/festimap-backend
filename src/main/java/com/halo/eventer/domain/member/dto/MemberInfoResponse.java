package com.halo.eventer.domain.member.dto;

import com.halo.eventer.domain.member.Member;
import lombok.Getter;

@Getter
public class MemberInfoResponse {
    private final String name;
    private final String phone;

    public MemberInfoResponse(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public static MemberInfoResponse from(Member member) {
        return new MemberInfoResponse(member.getName(), member.getPhone());
    }
}
