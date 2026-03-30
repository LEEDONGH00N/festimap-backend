package com.halo.eventer.domain.fieldops.exception;

import com.halo.eventer.global.error.ErrorCode;
import com.halo.eventer.global.error.exception.BaseException;

public class FieldOpsSessionAlreadyExistsException extends BaseException {

    public FieldOpsSessionAlreadyExistsException() {
        super(ErrorCode.FIELD_OPS_SESSION_ALREADY_EXISTS);
    }
}
