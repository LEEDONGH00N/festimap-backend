package com.halo.eventer.domain.fieldops.service;

import java.security.SecureRandom;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.halo.eventer.domain.category.Category;
import com.halo.eventer.domain.category.repository.CategoryRepository;
import com.halo.eventer.domain.fieldops.FieldOpsSession;
import com.halo.eventer.domain.fieldops.exception.CategoryNotFoundException;
import com.halo.eventer.domain.fieldops.exception.FieldOpsSessionAlreadyExistsException;
import com.halo.eventer.domain.fieldops.exception.FieldOpsSessionNotFoundException;
import com.halo.eventer.domain.fieldops.repository.FieldOpsSessionRepository;
import com.halo.eventer.global.security.PasswordService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FieldOpsSessionService {

    private static final int PASSWORD_LENGTH = 4;
    private static final String PASSWORD_CHARS = "0123456789";

    private final FieldOpsSessionRepository fieldOpsSessionRepository;
    private final CategoryRepository categoryRepository;
    private final PasswordService passwordService;
    private final SecureRandom secureRandom = new SecureRandom();

    @Transactional
    public FieldOpsSessionCreateResult createSession(Long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(CategoryNotFoundException::new);

        if (fieldOpsSessionRepository.existsByCategory(category)) {
            throw new FieldOpsSessionAlreadyExistsException();
        }

        String rawPassword = generatePassword();
        String hashedPassword = passwordService.encode(rawPassword);

        FieldOpsSession session = FieldOpsSession.create(category, hashedPassword);
        category.enableFieldOps();
        fieldOpsSessionRepository.save(session);

        return new FieldOpsSessionCreateResult(session, rawPassword);
    }

    public FieldOpsSession getSession(Long sessionId) {
        return fieldOpsSessionRepository.findById(sessionId).orElseThrow(FieldOpsSessionNotFoundException::new);
    }

    public FieldOpsSession getSessionByCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(CategoryNotFoundException::new);
        return fieldOpsSessionRepository.findByCategory(category).orElseThrow(FieldOpsSessionNotFoundException::new);
    }

    public FieldOpsSession getSessionByToken(String token) {
        return fieldOpsSessionRepository
                .findByTokenWithFestival(token)
                .orElseThrow(FieldOpsSessionNotFoundException::new);
    }

    @Transactional
    public String resetPassword(Long sessionId) {
        FieldOpsSession session =
                fieldOpsSessionRepository.findById(sessionId).orElseThrow(FieldOpsSessionNotFoundException::new);

        String rawPassword = generatePassword();
        String hashedPassword = passwordService.encode(rawPassword);

        session.resetPassword(hashedPassword);

        return rawPassword;
    }

    @Transactional
    public void disableSession(Long sessionId) {
        FieldOpsSession session =
                fieldOpsSessionRepository.findById(sessionId).orElseThrow(FieldOpsSessionNotFoundException::new);

        session.disable();
        session.getCategory().disableFieldOps();
    }

    private String generatePassword() {
        StringBuilder sb = new StringBuilder(PASSWORD_LENGTH);
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            int idx = secureRandom.nextInt(PASSWORD_CHARS.length());
            sb.append(PASSWORD_CHARS.charAt(idx));
        }
        return sb.toString();
    }

    public record FieldOpsSessionCreateResult(FieldOpsSession session, String rawPassword) {}
}
