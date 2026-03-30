package com.halo.eventer.domain.member.exception;

import com.halo.eventer.global.error.ErrorCode;
import com.halo.eventer.global.error.exception.BaseException;

public class CompanyEmailAlreadyExistsException extends BaseException {

    public CompanyEmailAlreadyExistsException() {
        super(ErrorCode.COMPANY_EMAIL_ALREADY_EXISTS);
    }
}
