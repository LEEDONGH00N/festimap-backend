package com.halo.eventer.domain.program_reservation.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@RequiredArgsConstructor
@Slf4j
public class MySqlDbLockRepository implements DbLockRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public boolean tryLock(String key, int timeoutSeconds) {
        Integer res = jdbcTemplate.queryForObject("SELECT GET_LOCK(?, ?)", Integer.class, key, timeoutSeconds);
        if (res == null) {
            log.warn("GET_LOCK returned NULL. key={}", key);
            return false;
        }
        if (res == 0) {
            log.info("GET_LOCK timeout. key={}", key);
            return false;
        }
        return res == 1;
    }

    @Override
    public void unlock(String key) {
        try {
            Integer res = jdbcTemplate.queryForObject("SELECT RELEASE_LOCK(?)", Integer.class, key);
            if (res == null) {
                log.warn("RELEASE_LOCK returned NULL. key={}", key);
            } else if (res == 0) {
                // 현재 세션이 그 락을 보유하지 않음(커넥션이 달라졌거나 이미 해제됨)
                log.warn("RELEASE_LOCK returned 0 (not owner). key={}", key);
            }
        } catch (Exception e) {
            log.warn("RELEASE_LOCK failed. key={}", key, e);
        }
    }
}
