package com.halo.eventer.domain.missing_person.controller;

import org.springframework.web.bind.annotation.*;

import com.halo.eventer.domain.missing_person.dto.MissingPersonListDto;
import com.halo.eventer.domain.missing_person.dto.MissingPersonReqDto;
import com.halo.eventer.domain.missing_person.dto.MissingPersonResDto;
import com.halo.eventer.domain.missing_person.service.MissingPersonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/missingPerson")
@Slf4j
@Tag(name = "미아/실종자", description = "미아 및 실종자 관리 API")
public class MissingPersonController {

    private final MissingPersonService missingPersonService;

    @Operation(summary = "실종자 등록", description = "새로운 실종자를 등록합니다.")
    @PostMapping()
    public void uploadMissingPerson(
            @RequestParam("festivalId") Long festivalId, @RequestBody MissingPersonReqDto missingPersonReqDto) {
        missingPersonService.createMissingPerson(festivalId, missingPersonReqDto);
    }

    @Operation(summary = "실종자 목록 조회", description = "모든 실종자 목록을 조회합니다.")
    @GetMapping()
    public MissingPersonListDto getMissingPerson() {
        return new MissingPersonListDto(missingPersonService.getAllMissingPersonList());
    }

    @Operation(summary = "실종자 상세 조회", description = "실종자 ID로 상세 정보를 조회합니다.")
    @GetMapping("/{missingPersonId}")
    public MissingPersonResDto getMissingPerson(@PathVariable(name = "missingPersonId") Long missingPersonId) {
        return new MissingPersonResDto(missingPersonService.getMissingPerson(missingPersonId));
    }

    @Operation(summary = "실종자 정보 수정", description = "실종자 정보를 수정합니다.")
    @PatchMapping()
    public MissingPersonListDto updateMissingPerson(
            @RequestParam(name = "missingPersonId") Long missingPersonId,
            @RequestBody MissingPersonReqDto missingPersonReqDto) {
        missingPersonService.updateMissingPerson(missingPersonId, missingPersonReqDto);
        return new MissingPersonListDto(missingPersonService.getAllMissingPersonList());
    }

    @Operation(summary = "실종자 팝업 설정", description = "실종자 팝업 표시 여부를 설정합니다.")
    @PatchMapping("/popup")
    public MissingPersonListDto selectMissingPersonPopup(
            @RequestParam(name = "missingPersonId") Long missingPersonId, @RequestParam(name = "check") boolean check) {
        missingPersonService.checkPopup(missingPersonId, check);
        return new MissingPersonListDto(missingPersonService.getAllMissingPersonList());
    }

    @Operation(summary = "실종자 삭제", description = "실종자를 삭제합니다.")
    @DeleteMapping("/{missingId}")
    public MissingPersonListDto deleteMissingPerson(@PathVariable(name = "missingId") Long missingId) {
        missingPersonService.deleteMissingPerson(missingId);
        return new MissingPersonListDto(missingPersonService.getAllMissingPersonList());
    }
}
