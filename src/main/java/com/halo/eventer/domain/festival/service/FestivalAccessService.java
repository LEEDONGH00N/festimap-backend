package com.halo.eventer.domain.festival.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.halo.eventer.domain.category.Category;
import com.halo.eventer.domain.category.repository.CategoryRepository;
import com.halo.eventer.domain.festival.Festival;
import com.halo.eventer.domain.festival.exception.FestivalAccessDeniedException;
import com.halo.eventer.domain.festival.exception.FestivalNotFoundException;
import com.halo.eventer.domain.festival.repository.FestivalRepository;
import com.halo.eventer.domain.fieldops.FieldOpsSession;
import com.halo.eventer.domain.fieldops.exception.CategoryNotFoundException;
import com.halo.eventer.domain.fieldops.exception.FieldOpsSessionNotFoundException;
import com.halo.eventer.domain.fieldops.repository.FieldOpsSessionRepository;
import com.halo.eventer.domain.member.Member;
import com.halo.eventer.domain.member.MemberRole;
import com.halo.eventer.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FestivalAccessService {

    private final FestivalRepository festivalRepository;
    private final CategoryRepository categoryRepository;
    private final FieldOpsSessionRepository fieldOpsSessionRepository;

    public void validateFestivalAccess(Long festivalId) {
        Festival festival =
                festivalRepository.findById(festivalId).orElseThrow(() -> new FestivalNotFoundException(festivalId));
        validateAccess(festival);
    }

    public void validateCategoryAccess(Long categoryId) {
        Category category = categoryRepository.findByIdWithFestival(categoryId);
        if (category == null) {
            throw new CategoryNotFoundException();
        }
        Festival festival = category.getFestivalModule().getFestival();
        validateAccess(festival);
    }

    public void validateFieldOpsSessionAccess(Long sessionId) {
        FieldOpsSession session = fieldOpsSessionRepository
                .findByIdWithFestival(sessionId)
                .orElseThrow(FieldOpsSessionNotFoundException::new);
        Festival festival = session.getCategory().getFestivalModule().getFestival();
        validateAccess(festival);
    }

    private void validateAccess(Festival festival) {
        Member currentMember = getCurrentMember();
        if (currentMember == null) {
            throw new FestivalAccessDeniedException();
        }

        if (currentMember.getRole() == MemberRole.SUPER_ADMIN) {
            return;
        }

        if (!festival.isOwnedBy(currentMember.getId())) {
            throw new FestivalAccessDeniedException();
        }
    }

    private Member getCurrentMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails) {
            return ((CustomUserDetails) principal).getMember();
        }
        return null;
    }
}
