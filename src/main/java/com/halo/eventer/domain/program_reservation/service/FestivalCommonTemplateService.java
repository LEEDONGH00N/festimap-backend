package com.halo.eventer.domain.program_reservation.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.halo.eventer.domain.festival.Festival;
import com.halo.eventer.domain.festival.repository.FestivalRepository;
import com.halo.eventer.domain.program_reservation.dto.request.TemplateSaveAllRequest;
import com.halo.eventer.domain.program_reservation.dto.response.TemplateManagementResponse;
import com.halo.eventer.domain.program_reservation.dto.response.TemplateResponse;
import com.halo.eventer.domain.program_reservation.entity.content.FestivalCommonTemplate;
import com.halo.eventer.domain.program_reservation.repository.FestivalCommonTemplateRepository;
import com.halo.eventer.global.error.ErrorCode;
import com.halo.eventer.global.error.exception.BaseException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FestivalCommonTemplateService {
    private final FestivalCommonTemplateRepository templateRepository;
    private final FestivalRepository festivalRepository;

    @Transactional(readOnly = true)
    public TemplateManagementResponse getList(Long festivalId) {
        Festival festival = festivalRepository
                .findById(festivalId)
                .orElseThrow(() -> new BaseException("존재하지 않는 축제입니다.", ErrorCode.ENTITY_NOT_FOUND));
        List<TemplateResponse> templates = templateRepository.findAllByFestivalIdOrderBySortOrder(festivalId).stream()
                .map(TemplateResponse::from)
                .collect(Collectors.toList());
        return TemplateManagementResponse.of(festival.getProgramThumbnail(), templates);
    }

    @Transactional
    public void saveAll(Long festivalId, TemplateSaveAllRequest request) {
        Festival festival = festivalRepository
                .findById(festivalId)
                .orElseThrow(() -> new BaseException("존재하지 않는 축제입니다.", ErrorCode.ENTITY_NOT_FOUND));

        templateRepository.deleteAllByFestivalId(festivalId);
        templateRepository.flush();

        List<TemplateSaveAllRequest.Item> items = request.getTemplates();
        for (int i = 0; i < items.size(); i++) {
            TemplateSaveAllRequest.Item item = items.get(i);
            FestivalCommonTemplate template =
                    FestivalCommonTemplate.of(festival, i, item.getTitle(), item.getContent());
            templateRepository.save(template);
        }
    }

    @Transactional
    public void delete(Long templateId) {
        FestivalCommonTemplate template = templateRepository
                .findById(templateId)
                .orElseThrow(() -> new BaseException("존재하지 않는 템플릿입니다.", ErrorCode.ENTITY_NOT_FOUND));
        templateRepository.delete(template);
    }
}
