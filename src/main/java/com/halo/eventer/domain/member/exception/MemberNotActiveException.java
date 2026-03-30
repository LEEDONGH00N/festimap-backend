package com.halo.eventer.domain.member.exception;

import com.halo.eventer.global.error.ErrorCode;
import com.halo.eventer.global.error.exception.BaseException;

public class MemberNotActiveException extends BaseException {
    public MemberNotActiveException() {
        super(ErrorCode.MEMBER_NOT_ACTIVE);
    }
}
