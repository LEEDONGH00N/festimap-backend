package com.halo.eventer.domain.program_reservation.repository;

public interface DbLockRepository {
    boolean tryLock(String key, int timeoutSeconds);

    void unlock(String key);
}
