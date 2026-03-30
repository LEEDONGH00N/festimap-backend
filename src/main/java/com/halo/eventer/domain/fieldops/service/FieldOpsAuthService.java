package com.halo.eventer.domain.fieldops.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.halo.eventer.domain.fieldops.FieldOpsSession;
import com.halo.eventer.domain.fieldops.exception.FestivalNotActiveException;
import com.halo.eventer.domain.fieldops.exception.FieldOpsPasswordInvalidException;
import com.halo.eventer.domain.fieldops.exception.FieldOpsSessionExpiredException;
import com.halo.eventer.domain.fieldops.exception.FieldOpsSessionNotFoundException;
import com.halo.eventer.domain.fieldops.repository.FieldOpsSessionRepository;
import com.halo.eventer.global.security.PasswordService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FieldOpsAuthService {

    private final FieldOpsSessionRepository fieldOpsSessionRepository;
    private final PasswordService passwordService;

    public FieldOpsSession validateTokenAndCheckStatus(String token) {
        FieldOpsSession session = fieldOpsSessionRepository
                .findByTokenWithFestival(token)
                .orElseThrow(FieldOpsSessionNotFoundException::new);

        validateSessionStatus(session);
        return session;
    }

    public FieldOpsSession verifyPassword(String token, String rawPassword) {
        FieldOpsSession session = fieldOpsSessionRepository
                .findByTokenWithFestival(token)
                .orElseThrow(FieldOpsSessionNotFoundException::new);

        validateSessionStatus(session);

        if (!passwordService.matches(rawPassword, session.getPwHash())) {
            throw new FieldOpsPasswordInvalidException();
        }

        return session;
    }

    public boolean isTokenValid(String token) {
        return fieldOpsSessionRepository
                .findByTokenWithFestival(token)
                .map(FieldOpsSession::isValid)
                .orElse(false);
    }

    private void validateSessionStatus(FieldOpsSession session) {
        if (!session.isValid()) {
            if (!session.getCategory().getFestivalModule().getFestival().isActive()) {
                throw new FestivalNotActiveException();
            }
            throw new FieldOpsSessionExpiredException();
        }
    }
}
