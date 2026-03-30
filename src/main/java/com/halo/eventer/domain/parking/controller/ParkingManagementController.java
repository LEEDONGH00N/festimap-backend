package com.halo.eventer.domain.parking.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

import org.springframework.web.bind.annotation.*;

import com.halo.eventer.domain.image.dto.FileDto;
import com.halo.eventer.domain.parking.dto.common.DisplayOrderChangeReqDto;
import com.halo.eventer.domain.parking.dto.parking_management.ParkingManagementIdResDto;
import com.halo.eventer.domain.parking.dto.parking_management.ParkingManagementReqDto;
import com.halo.eventer.domain.parking.dto.parking_management.ParkingManagementResDto;
import com.halo.eventer.domain.parking.dto.parking_management.ParkingManagementSubPageResDto;
import com.halo.eventer.domain.parking.dto.parking_management.ParkingSubPageReqDto;
import com.halo.eventer.domain.parking.service.ParkingManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2")
@Tag(name = "주차 관리", description = "주차 관리 설정 API")
public class ParkingManagementController {

    private final ParkingManagementService parkingManagementService;

    @Operation(summary = "주차 관리 생성", description = "축제에 주차 관리 설정을 생성합니다.")
    @PostMapping("/admin/festivals/{festivalId}/parking-managements")
    public void create(
            @Min(1) @PathVariable("festivalId") Long festivalId,
            @Valid @RequestBody ParkingManagementReqDto parkingManagementReqDto) {
        parkingManagementService.create(festivalId, parkingManagementReqDto);
    }

    @Operation(summary = "주차 관리 조회 (관리자)", description = "주차 관리 상세 정보를 조회합니다.")
    @GetMapping("/admin/parking-managements/{id}")
    public ParkingManagementResDto getParkingManagement(@PathVariable("id") Long id) {
        return parkingManagementService.getParkingManagement(id);
    }

    @Operation(summary = "주차 관리 조회 (사용자)", description = "표시 중인 주차 관리 정보를 조회합니다.")
    @GetMapping("/user/parking-managements/{id}")
    public ParkingManagementResDto getParkingManagementByUserId(@PathVariable("id") Long id) {
        return parkingManagementService.getVisibleParkingManagement(id);
    }

    @Operation(summary = "주차 관리 수정", description = "주차 관리 설정을 수정합니다.")
    @PutMapping("/admin/parking-managements/{id}")
    public void update(
            @Min(1) @PathVariable("id") Long id, @Valid @RequestBody ParkingManagementReqDto parkingManagementReqDto) {
        parkingManagementService.update(id, parkingManagementReqDto);
    }

    @Operation(summary = "주차 서브 페이지 정보 조회", description = "주차 서브 페이지 정보를 조회합니다.")
    @GetMapping("/admin/parking-managements/{id}/sub-page-info")
    public ParkingManagementSubPageResDto getParkingManagementByFestivalIdAndParkingInfoType(
            @PathVariable("id") Long id) {
        return parkingManagementService.getParkingManagementSubPage(id);
    }

    @Operation(summary = "주차 서브 페이지 헤더 이름 수정", description = "주차 서브 페이지의 헤더 이름을 수정합니다.")
    @PatchMapping("/admin/parking-managements/{id}/sub-page-header-name")
    public void updateSubPageHeaderName(
            @Min(1) @PathVariable("id") Long id, @Valid @RequestBody ParkingSubPageReqDto parkingSubPageReqDto) {
        parkingManagementService.updateSubPageHeaderName(id, parkingSubPageReqDto);
    }

    @Operation(summary = "주차장 지도 이미지 추가", description = "주차장 지도 이미지를 추가합니다.")
    @PostMapping("/admin/parking-managements/{id}/parking-map-images")
    public void createParkingMapImage(@Min(1) @PathVariable("id") Long id, @RequestBody FileDto fileDto) {
        parkingManagementService.createParkingMapImage(id, fileDto);
    }

    @Operation(summary = "주차장 지도 이미지 표시 순서 수정", description = "주차장 지도 이미지의 표시 순서를 수정합니다.")
    @PatchMapping("/admin/parking-managements/{id}/parking-map-images/display-order")
    public void updateParkingMapImageDisplayOrder(
            @Min(1) @PathVariable("id") Long id,
            @Valid @RequestBody DisplayOrderChangeReqDto displayOrderChangeReqDto) {
        parkingManagementService.updateParkingMapImageDisplayOrder(id, displayOrderChangeReqDto);
    }

    @Operation(summary = "주차장 지도 이미지 삭제", description = "주차장 지도 이미지를 삭제합니다.")
    @DeleteMapping("/admin/parking-managements/{id}/parking-map-images")
    public void deleteParkingMapImages(
            @Min(1) @PathVariable("id") Long id,
            @Valid @RequestBody DisplayOrderChangeReqDto displayOrderChangeReqDto) {
        parkingManagementService.deleteParkingMapImages(id, displayOrderChangeReqDto);
    }

    @Operation(summary = "축제별 주차 관리 ID 조회", description = "축제 ID로 주차 관리 ID를 조회합니다.")
    @GetMapping("/common/festivals/{festivalId}/parkingManagements")
    public ParkingManagementIdResDto getParkingManagementsId(@Min(1) @PathVariable("festivalId") Long festivalId) {
        return parkingManagementService.getParkingManagementId(festivalId);
    }
}
