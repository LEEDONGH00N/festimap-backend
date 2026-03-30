package com.halo.eventer.domain.parking.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

import org.springframework.web.bind.annotation.*;

import com.halo.eventer.domain.parking.dto.common.VisibilityChangeReqDto;
import com.halo.eventer.domain.parking.dto.parking_lot.AdminParkingLotResDto;
import com.halo.eventer.domain.parking.dto.parking_lot.CongestionLevelReqDto;
import com.halo.eventer.domain.parking.dto.parking_lot.ParkingLotReqDto;
import com.halo.eventer.domain.parking.service.ParkingLotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2")
@Tag(name = "주차장", description = "주차장 관리 API")
public class ParkingLotController {

    private final ParkingLotService parkingLotService;

    @Operation(summary = "주차장 생성", description = "특정 주차 구역에 새로운 주차장을 생성합니다.")
    @PostMapping("/admin/parking-zones/{parkingZoneId}/parking-lots")
    public void create(
            @PathVariable("parkingZoneId") Long parkingZoneId, @RequestBody ParkingLotReqDto parkingLotReqDto) {
        parkingLotService.create(parkingZoneId, parkingLotReqDto);
    }

    @Operation(summary = "주차장 상세 조회", description = "주차장 ID로 상세 정보를 조회합니다.")
    @GetMapping("/admin/parking-lots/{id}")
    public AdminParkingLotResDto getParkingLot(@PathVariable("id") @Min(1) Long id) {
        return parkingLotService.getParkingLot(id);
    }

    @Operation(summary = "주차장 표시 여부 변경", description = "주차장의 표시 여부를 변경합니다.")
    @PatchMapping("/admin/parking-lots/{id}/visibility")
    public void changeVisible(@PathVariable("id") @Min(1) Long id, @RequestBody @Valid VisibilityChangeReqDto reqDto) {
        parkingLotService.changeVisible(id, reqDto);
    }

    @Operation(summary = "주차장 수정", description = "주차장 정보를 수정합니다.")
    @PutMapping("/admin/parking-lots/{id}")
    public void updateContent(@PathVariable("id") @Min(1) Long id, @RequestBody ParkingLotReqDto reqDto) {
        parkingLotService.updateContent(id, reqDto);
    }

    @Operation(summary = "주차장 혼잡도 변경", description = "주차장의 혼잡도를 변경합니다.")
    @PatchMapping("/admin/parking-lots/{id}/congestion-level")
    public void changeCongestionLevel(
            @PathVariable("id") @Min(1) Long id, @RequestBody @Valid CongestionLevelReqDto reqDto) {
        parkingLotService.changeCongestionLevel(id, reqDto);
    }

    @Operation(summary = "주차장 삭제", description = "주차장을 삭제합니다.")
    @DeleteMapping("/admin/parking-lots/{id}")
    public void delete(@PathVariable("id") @Min(1) Long id) {
        parkingLotService.delete(id);
    }
}
