package com.halo.eventer.domain.fieldops.exception;

import com.halo.eventer.global.error.ErrorCode;
import com.halo.eventer.global.error.exception.BaseException;

public class FieldOpsSessionExpiredException extends BaseException {

    public FieldOpsSessionExpiredException() {
        super(ErrorCode.FIELD_OPS_SESSION_EXPIRED);
    }
}
