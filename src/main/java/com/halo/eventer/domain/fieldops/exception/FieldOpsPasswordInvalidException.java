package com.halo.eventer.domain.fieldops.exception;

import com.halo.eventer.global.error.ErrorCode;
import com.halo.eventer.global.error.exception.BaseException;

public class FieldOpsPasswordInvalidException extends BaseException {

    public FieldOpsPasswordInvalidException() {
        super(ErrorCode.FIELD_OPS_PASSWORD_INVALID);
    }
}
