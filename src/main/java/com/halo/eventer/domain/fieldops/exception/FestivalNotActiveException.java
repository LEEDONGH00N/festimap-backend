package com.halo.eventer.domain.fieldops.exception;

import com.halo.eventer.global.error.ErrorCode;
import com.halo.eventer.global.error.exception.BaseException;

public class FestivalNotActiveException extends BaseException {

    public FestivalNotActiveException() {
        super(ErrorCode.FESTIVAL_NOT_ACTIVE);
    }
}
