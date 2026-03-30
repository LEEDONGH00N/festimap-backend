package com.halo.eventer.domain.program_reservation.service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import jakarta.persistence.EntityManager;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.halo.eventer.domain.program_reservation.Program;
import com.halo.eventer.domain.program_reservation.dto.request.AdditionalAnswerRequest;
import com.halo.eventer.domain.program_reservation.dto.request.AdditionalFieldSaveRequest;
import com.halo.eventer.domain.program_reservation.dto.response.AdditionalFieldListResponse;
import com.halo.eventer.domain.program_reservation.dto.response.UserAdditionalFieldListResponse;
import com.halo.eventer.domain.program_reservation.entity.additional.AdditionalFieldType;
import com.halo.eventer.domain.program_reservation.entity.additional.ProgramAdditionalField;
import com.halo.eventer.domain.program_reservation.entity.additional.ProgramAdditionalFieldOption;
import com.halo.eventer.domain.program_reservation.entity.additional.ProgramReservationAdditionalAnswer;
import com.halo.eventer.domain.program_reservation.entity.reservation.ProgramReservation;
import com.halo.eventer.domain.program_reservation.repository.ProgramAdditionalFieldOptionRepository;
import com.halo.eventer.domain.program_reservation.repository.ProgramAdditionalFieldRepository;
import com.halo.eventer.domain.program_reservation.repository.ProgramRepository;
import com.halo.eventer.domain.program_reservation.repository.ProgramReservationAdditionalAnswerRepository;
import com.halo.eventer.global.error.ErrorCode;
import com.halo.eventer.global.error.exception.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdditionalFieldService {

    private final ProgramRepository programRepository;
    private final ProgramAdditionalFieldRepository fieldRepository;
    private final ProgramAdditionalFieldOptionRepository optionRepository;
    private final ProgramReservationAdditionalAnswerRepository answerRepository;
    private final EntityManager entityManager;

    @Transactional(readOnly = true)
    public AdditionalFieldListResponse getFields(Long programId) {
        loadProgramOrThrow(programId);

        List<ProgramAdditionalField> fields = fieldRepository.findAllByProgramIdOrderBySortOrder(programId);
        List<Long> fieldIds = fields.stream().map(ProgramAdditionalField::getId).collect(Collectors.toList());

        Map<Long, List<ProgramAdditionalFieldOption>> optionsMap = fieldIds.isEmpty()
                ? Collections.emptyMap()
                : optionRepository.findAllByFieldIdInOrderBySortOrder(fieldIds).stream()
                        .collect(Collectors.groupingBy(o -> o.getField().getId()));

        return AdditionalFieldListResponse.of(fields, optionsMap);
    }

    @Transactional
    public void saveFields(Long programId, AdditionalFieldSaveRequest request) {
        Program program = loadProgramOrThrow(programId);

        // 1) existing fields 조회 및 맵핑
        List<ProgramAdditionalField> existingFields = fieldRepository.findAllByProgramId(programId);
        Map<Long, ProgramAdditionalField> existingFieldMap =
                existingFields.stream().collect(Collectors.toMap(ProgramAdditionalField::getId, Function.identity()));

        // 2) temp sort shift (UNIQUE 충돌 방지)
        if (!existingFields.isEmpty()) {
            int min = fieldRepository.findMinSortOrderByProgramId(programId).orElse(0);
            int tempStart = min - existingFields.size() - 1; // 기존 최소보다 더 작은 영역
            for (int idx = 0; idx < existingFields.size(); idx++) {
                existingFields.get(idx).changeSortOrder(tempStart + idx);
            }
            entityManager.flush();
        }

        // 3) request fields를 index 순서대로 upsert
        Set<Long> requestFieldIds = new HashSet<>();
        List<AdditionalFieldSaveRequest.FieldRequest> fieldRequests =
                request.getFields() != null ? request.getFields() : List.of();

        for (int i = 0; i < fieldRequests.size(); i++) {
            AdditionalFieldSaveRequest.FieldRequest fr = fieldRequests.get(i);
            ProgramAdditionalField field;

            if (fr.getId() == null) {
                field = ProgramAdditionalField.of(program, fr.getType(), fr.getLabel(), false, i);
                fieldRepository.save(field);
                entityManager.flush(); // 신규 field id 확보
            } else {
                requestFieldIds.add(fr.getId());
                field = existingFieldMap.get(fr.getId());
                if (field == null) {
                    throw new BaseException("존재하지 않는 추가 정보 항목입니다.", ErrorCode.ENTITY_NOT_FOUND);
                }
                field.update(fr.getType(), fr.getLabel(), false, fr.isActive(), i);
            }

            saveOptions(field, fr.getOptions() != null ? fr.getOptions() : List.of());
        }

        // 4) request에 없는 existing field 처리 (sort_order는 활성 항목 뒤로 이어붙임)
        int deactivatedSortOrder = fieldRequests.size();
        for (ProgramAdditionalField existing : existingFields) {
            if (!requestFieldIds.contains(existing.getId())) {
                if (answerRepository.existsByFieldId(existing.getId())) {
                    existing.deactivate();
                    existing.changeSortOrder(deactivatedSortOrder++);
                } else {
                    optionRepository.deleteAllByFieldId(existing.getId());
                    fieldRepository.delete(existing);
                }
            }
        }
    }

    @Transactional(readOnly = true)
    public UserAdditionalFieldListResponse getActiveFields(Long programId) {
        loadProgramOrThrow(programId);

        List<ProgramAdditionalField> fields =
                fieldRepository.findAllByProgramIdAndIsActiveTrueOrderBySortOrder(programId);
        List<Long> fieldIds = fields.stream().map(ProgramAdditionalField::getId).collect(Collectors.toList());

        Map<Long, List<ProgramAdditionalFieldOption>> optionsMap = fieldIds.isEmpty()
                ? Collections.emptyMap()
                : optionRepository.findAllByFieldIdInAndIsActiveTrueOrderBySortOrder(fieldIds).stream()
                        .collect(Collectors.groupingBy(o -> o.getField().getId()));

        return UserAdditionalFieldListResponse.of(fields, optionsMap);
    }

    @Transactional
    public void saveAnswers(ProgramReservation reservation, List<AdditionalAnswerRequest> answers) {
        if (answers == null || answers.isEmpty()) {
            return;
        }

        Long programId = reservation.getProgram().getId();

        // active fields/options 조회
        List<ProgramAdditionalField> activeFields =
                fieldRepository.findAllByProgramIdAndIsActiveTrueOrderBySortOrder(programId);
        Map<Long, ProgramAdditionalField> fieldMap =
                activeFields.stream().collect(Collectors.toMap(ProgramAdditionalField::getId, Function.identity()));

        List<Long> fieldIds =
                activeFields.stream().map(ProgramAdditionalField::getId).collect(Collectors.toList());
        Map<Long, List<ProgramAdditionalFieldOption>> activeOptionsMap = fieldIds.isEmpty()
                ? Collections.emptyMap()
                : optionRepository.findAllByFieldIdInAndIsActiveTrueOrderBySortOrder(fieldIds).stream()
                        .collect(Collectors.groupingBy(o -> o.getField().getId()));

        for (AdditionalAnswerRequest answer : answers) {
            ProgramAdditionalField field = fieldMap.get(answer.getFieldId());
            if (field == null) {
                throw new BaseException(
                        "유효하지 않은 추가 정보 항목입니다: fieldId=" + answer.getFieldId(), ErrorCode.INVALID_INPUT_VALUE);
            }

            ProgramReservationAdditionalAnswer entity;

            if (field.getType() == AdditionalFieldType.TEXT) {
                if (answer.getOptionId() != null) {
                    throw new BaseException(
                            "TEXT 타입 필드에는 optionId를 지정할 수 없습니다: fieldId=" + field.getId(),
                            ErrorCode.INVALID_INPUT_VALUE);
                }
                if (answer.getValueText() == null || answer.getValueText().isBlank()) {
                    throw new BaseException(
                            "TEXT 타입 필드의 값이 비어 있습니다: fieldId=" + field.getId(), ErrorCode.INVALID_INPUT_VALUE);
                }
                entity = ProgramReservationAdditionalAnswer.ofText(reservation, field, answer.getValueText());
            } else {
                // SELECT
                if (answer.getOptionId() == null) {
                    throw new BaseException(
                            "SELECT 타입 필드에는 optionId가 필요합니다: fieldId=" + field.getId(), ErrorCode.INVALID_INPUT_VALUE);
                }

                List<ProgramAdditionalFieldOption> fieldOptions =
                        activeOptionsMap.getOrDefault(field.getId(), List.of());
                ProgramAdditionalFieldOption selectedOption = fieldOptions.stream()
                        .filter(o -> o.getId().equals(answer.getOptionId()))
                        .findFirst()
                        .orElseThrow(() -> new BaseException(
                                "유효하지 않은 옵션입니다: optionId=" + answer.getOptionId(), ErrorCode.INVALID_INPUT_VALUE));

                if (selectedOption.isFreeText()) {
                    if (answer.getValueText() == null || answer.getValueText().isBlank()) {
                        throw new BaseException(
                                "자유 입력 옵션의 값이 비어 있습니다: optionId=" + selectedOption.getId(),
                                ErrorCode.INVALID_INPUT_VALUE);
                    }
                } else {
                    if (answer.getValueText() != null) {
                        throw new BaseException(
                                "자유 입력이 아닌 옵션에는 valueText를 지정할 수 없습니다: optionId=" + selectedOption.getId(),
                                ErrorCode.INVALID_INPUT_VALUE);
                    }
                }

                entity = ProgramReservationAdditionalAnswer.ofSelect(
                        reservation, field, selectedOption, answer.getValueText());
            }

            answerRepository.save(entity);
        }
    }

    private void saveOptions(
            ProgramAdditionalField field, List<AdditionalFieldSaveRequest.OptionRequest> optionRequests) {
        if (field.getId() == null) {
            throw new BaseException("추가 정보 항목 저장이 완료되지 않았습니다.", ErrorCode.INVALID_INPUT_VALUE);
        }

        // A) existing options 조회
        List<ProgramAdditionalFieldOption> existingOptions = optionRepository.findAllByFieldId(field.getId());

        // B) temp sort shift (UNIQUE(field_id, sort_order) 충돌 방지)
        if (!existingOptions.isEmpty()) {
            int min = optionRepository.findMinSortOrderByFieldId(field.getId()).orElse(0);
            int tempStart = min - existingOptions.size() - 1;
            for (int idx = 0; idx < existingOptions.size(); idx++) {
                existingOptions.get(idx).changeSortOrder(tempStart + idx);
            }
            entityManager.flush();
        }

        // C) TEXT 타입: options 최종 0개
        if (field.getType() == AdditionalFieldType.TEXT) {
            int deactivatedSort = 0;
            for (ProgramAdditionalFieldOption opt : existingOptions) {
                if (answerRepository.existsByOptionId(opt.getId())) {
                    opt.deactivate();
                    opt.changeSortOrder(deactivatedSort++);
                } else {
                    optionRepository.delete(opt);
                }
            }
            return;
        }

        // D) SELECT 타입: options >= 1 검증 후 upsert
        if (optionRequests.isEmpty()) {
            throw new BaseException("SELECT 타입은 최소 1개의 옵션이 필요합니다.", ErrorCode.INVALID_INPUT_VALUE);
        }

        upsertAndCleanOptions(field, existingOptions, optionRequests);
    }

    private void upsertAndCleanOptions(
            ProgramAdditionalField field,
            List<ProgramAdditionalFieldOption> existingOptions,
            List<AdditionalFieldSaveRequest.OptionRequest> optionRequests) {

        Map<Long, ProgramAdditionalFieldOption> existingOptionMap = existingOptions.stream()
                .collect(Collectors.toMap(ProgramAdditionalFieldOption::getId, Function.identity()));

        Set<Long> requestOptionIds = new HashSet<>();

        for (int j = 0; j < optionRequests.size(); j++) {
            AdditionalFieldSaveRequest.OptionRequest or = optionRequests.get(j);
            if (or.getId() == null) {
                ProgramAdditionalFieldOption option =
                        ProgramAdditionalFieldOption.of(field, or.getLabel(), j, or.isFreeText());
                optionRepository.save(option);
            } else {
                requestOptionIds.add(or.getId());
                ProgramAdditionalFieldOption option = existingOptionMap.get(or.getId());
                if (option == null) {
                    throw new BaseException("존재하지 않는 옵션입니다.", ErrorCode.ENTITY_NOT_FOUND);
                }
                option.update(or.getLabel(), or.isActive(), j, or.isFreeText());
            }
        }

        // request에 없는 existing option 처리 (sort_order는 활성 옵션 뒤로 이어붙임)
        int deactivatedSort = optionRequests.size();
        for (ProgramAdditionalFieldOption existing : existingOptions) {
            if (!requestOptionIds.contains(existing.getId())) {
                if (answerRepository.existsByOptionId(existing.getId())) {
                    existing.deactivate();
                    existing.changeSortOrder(deactivatedSort++);
                } else {
                    optionRepository.delete(existing);
                }
            }
        }
    }

    private Program loadProgramOrThrow(Long programId) {
        return programRepository
                .findById(programId)
                .orElseThrow(() -> new BaseException("존재하지 않는 프로그램입니다.", ErrorCode.ENTITY_NOT_FOUND));
    }
}
