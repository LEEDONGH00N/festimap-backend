package com.halo.eventer.domain.festival.exception;

import com.halo.eventer.global.error.ErrorCode;
import com.halo.eventer.global.error.exception.BaseException;

public class FestivalAccessDeniedException extends BaseException {

    public FestivalAccessDeniedException() {
        super(ErrorCode.FESTIVAL_ACCESS_DENIED);
    }
}
