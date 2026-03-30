package com.halo.eventer.domain.member.exception;

import com.halo.eventer.global.error.ErrorCode;
import com.halo.eventer.global.error.exception.BaseException;

public class PhoneAlreadyRegisteredException extends BaseException {
    public PhoneAlreadyRegisteredException() {
        super(ErrorCode.PHONE_ALREADY_REGISTERED);
    }
}
