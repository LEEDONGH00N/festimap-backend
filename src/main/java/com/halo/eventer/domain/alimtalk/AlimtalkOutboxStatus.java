package com.halo.eventer.domain.alimtalk;

public enum AlimtalkOutboxStatus {
    PENDING, // 아직 전송 시도 전
    SENT, // SENS가 2xx로 요청을 접수한 상태
    FAILED // 요청 단계에서 실패한 상태
}
