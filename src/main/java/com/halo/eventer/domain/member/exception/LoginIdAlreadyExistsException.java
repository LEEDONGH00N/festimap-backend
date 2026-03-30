package com.halo.eventer.domain.member.exception;

import com.halo.eventer.global.error.ErrorCode;
import com.halo.eventer.global.error.exception.BaseException;

public class LoginIdAlreadyExistsException extends BaseException {

    public LoginIdAlreadyExistsException() {
        super(ErrorCode.LOGIN_ID_ALREADY_EXISTS);
    }
}
