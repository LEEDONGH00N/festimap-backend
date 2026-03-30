package com.halo.eventer.domain.fieldops.exception;

import com.halo.eventer.global.error.ErrorCode;
import com.halo.eventer.global.error.exception.BaseException;

public class FieldOpsSessionNotFoundException extends BaseException {

    public FieldOpsSessionNotFoundException() {
        super(ErrorCode.FIELD_OPS_SESSION_NOT_FOUND);
    }
}
