package com.halo.eventer.domain.parking.controller;

import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

import org.springframework.web.bind.annotation.*;

import com.halo.eventer.domain.parking.dto.common.VisibilityChangeReqDto;
import com.halo.eventer.domain.parking.dto.parking_notice.ParkingNoticeContentReqDto;
import com.halo.eventer.domain.parking.dto.parking_notice.ParkingNoticeReqDto;
import com.halo.eventer.domain.parking.dto.parking_notice.ParkingNoticeResDto;
import com.halo.eventer.domain.parking.service.ParkingNoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2")
@Tag(name = "주차 공지사항", description = "주차 관련 공지사항 관리 API")
public class ParkingNoticeController {

    private final ParkingNoticeService parkingNoticeService;

    @Operation(summary = "주차 공지사항 생성", description = "새로운 주차 공지사항을 생성합니다.")
    @PostMapping("/admin/parking-managements/{parkingManagementId}/parking-notices")
    public void create(
            @Min(1) @PathVariable("parkingManagementId") Long parkingManagementId,
            @Valid @RequestBody ParkingNoticeReqDto parkingNoticeReqDto) {
        parkingNoticeService.create(parkingManagementId, parkingNoticeReqDto);
    }

    @Operation(summary = "주차 공지사항 목록 조회 (관리자)", description = "모든 주차 공지사항 목록을 조회합니다.")
    @GetMapping("/admin/parking-managements/{parkingManagementId}/parking-notices")
    public List<ParkingNoticeResDto> getParkingNotices(
            @Min(1) @PathVariable("parkingManagementId") Long parkingManagementId) {
        return parkingNoticeService.getParkingNotices(parkingManagementId);
    }

    @Operation(summary = "주차 공지사항 목록 조회 (사용자)", description = "표시 중인 주차 공지사항 목록을 조회합니다.")
    @GetMapping("/user/parking-managements/{parkingManagementId}/parking-notices")
    public List<ParkingNoticeResDto> getParkingNotice(
            @Min(1) @PathVariable("parkingManagementId") Long parkingManagementId) {
        return parkingNoticeService.getVisibleParkingNotices(parkingManagementId);
    }

    @Operation(summary = "주차 공지사항 내용 수정", description = "주차 공지사항의 내용을 수정합니다.")
    @PatchMapping(("/admin/parking-notices/{id}/content"))
    public void updateContent(
            @Min(1) @PathVariable("id") Long id,
            @Valid @RequestBody ParkingNoticeContentReqDto parkingNoticeContentReqDto) {
        parkingNoticeService.updateContent(id, parkingNoticeContentReqDto);
    }

    @Operation(summary = "주차 공지사항 표시 여부 변경", description = "주차 공지사항의 표시 여부를 변경합니다.")
    @PatchMapping("/admin/parking-notices/{id}/visibility")
    public void changeVisibility(
            @Min(1) @PathVariable("id") Long id, @Valid @RequestBody VisibilityChangeReqDto visibilityChangeReqDto) {
        parkingNoticeService.changeVisibility(id, visibilityChangeReqDto);
    }

    @Operation(summary = "주차 공지사항 삭제", description = "주차 공지사항을 삭제합니다.")
    @DeleteMapping("/admin/parking-notices/{id}")
    public void delete(@Min(1) @PathVariable("id") Long id) {
        parkingNoticeService.delete(id);
    }
}
