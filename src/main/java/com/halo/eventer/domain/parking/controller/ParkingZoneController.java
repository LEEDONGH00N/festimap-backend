package com.halo.eventer.domain.parking.controller;

import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

import org.springframework.web.bind.annotation.*;

import com.halo.eventer.domain.parking.dto.common.DisplayOrderChangeReqDto;
import com.halo.eventer.domain.parking.dto.common.NameUpdateReqDto;
import com.halo.eventer.domain.parking.dto.common.VisibilityChangeReqDto;
import com.halo.eventer.domain.parking.dto.parking_zone.ParkingZoneReqDto;
import com.halo.eventer.domain.parking.dto.parking_zone.ParkingZoneResDto;
import com.halo.eventer.domain.parking.service.ParkingZoneService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2")
@Tag(name = "주차 구역", description = "주차 구역 관리 API")
public class ParkingZoneController {

    private final ParkingZoneService parkingZoneService;

    @Operation(summary = "주차 구역 생성", description = "새로운 주차 구역을 생성합니다.")
    @PostMapping("/admin/parking-managements/{parkingManagementId}/parking-zones")
    public void create(
            @PathVariable @Min(1) Long parkingManagementId, @RequestBody @Valid ParkingZoneReqDto parkingZoneReqDto) {
        parkingZoneService.create(parkingManagementId, parkingZoneReqDto);
    }

    @Operation(summary = "주차 구역 목록 조회 (관리자)", description = "모든 주차 구역 목록을 조회합니다.")
    @GetMapping("/admin/parking-managements/{parkingManagementId}/parking-zones")
    public List<ParkingZoneResDto> getParkingZones(@PathVariable @Min(1) Long parkingManagementId) {
        return parkingZoneService.getParkingZones(parkingManagementId);
    }

    @Operation(summary = "주차 구역 목록 조회 (사용자)", description = "표시 중인 주차 구역 목록을 조회합니다.")
    @GetMapping("/user/parking-managements/{parkingManagementId}/parking-zones")
    public List<ParkingZoneResDto> getVisibleParkingZones(@PathVariable @Min(1) Long parkingManagementId) {
        return parkingZoneService.getVisibleParkingZones(parkingManagementId);
    }

    @Operation(summary = "주차 구역 표시 여부 변경", description = "주차 구역의 표시 여부를 변경합니다.")
    @PatchMapping("/admin/parking-zones/{id}/visibility")
    public void changeVisibility(
            @PathVariable("id") @Min(1) Long id, @RequestBody @Valid VisibilityChangeReqDto reqDto) {
        parkingZoneService.changeVisible(id, reqDto);
    }

    @Operation(summary = "주차 구역 이름 변경", description = "주차 구역의 이름을 변경합니다.")
    @PatchMapping("/admin/parking-zones/{id}/name")
    public void changeName(@PathVariable("id") @Min(1) Long id, @RequestBody @Valid NameUpdateReqDto reqDto) {
        parkingZoneService.changeName(id, reqDto);
    }

    @Operation(summary = "주차 구역 표시 순서 변경", description = "주차 구역의 표시 순서를 변경합니다.")
    @PutMapping("/admin/parking-managements/{parkingManagementId}/parking-zones/display-order")
    public void changeDisplayOrder(
            @PathVariable @Min(1) Long parkingManagementId, @RequestBody @Valid DisplayOrderChangeReqDto reqDto) {
        parkingZoneService.changeDisplayOrder(parkingManagementId, reqDto);
    }

    @Operation(summary = "주차 구역 삭제", description = "주차 구역을 삭제합니다.")
    @DeleteMapping("/admin/parking-managements/{parkingManagementId}/parking-zones/{id}")
    public void delete(@PathVariable @Min(1) Long parkingManagementId, @PathVariable("id") @Min(1) Long id) {
        parkingZoneService.delete(parkingManagementId, id);
    }
}
