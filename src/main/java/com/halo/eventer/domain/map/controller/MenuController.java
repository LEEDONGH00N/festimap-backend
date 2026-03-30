package com.halo.eventer.domain.map.controller;

import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

import org.springframework.web.bind.annotation.*;

import com.halo.eventer.domain.map.dto.menu.MenuCreateDto;
import com.halo.eventer.domain.map.dto.menu.MenuResDto;
import com.halo.eventer.domain.map.dto.menu.MenuUpdateDto;
import com.halo.eventer.domain.map.service.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "메뉴", description = "지도 마커의 메뉴 관리 API")
public class MenuController {

    private final MenuService menuService;

    @Operation(summary = "메뉴 생성", description = "지도 마커에 새로운 메뉴를 생성합니다.")
    @PostMapping("/maps/{mapId}/menus")
    public void create(
            @Min(1) @PathVariable("mapId") Long mapId, @Valid @RequestBody List<MenuCreateDto> menuCreateDtos) {
        menuService.create(menuCreateDtos, mapId);
    }

    @Operation(summary = "메뉴 목록 조회", description = "지도 마커의 메뉴 목록을 조회합니다.")
    @GetMapping("/maps/{mapId}/menus")
    public List<MenuResDto> getMenus(@Min(1) @PathVariable("mapId") Long mapId) {
        return menuService.getMenus(mapId);
    }

    @Operation(summary = "메뉴 수정", description = "지도 마커의 메뉴를 수정합니다.")
    @PatchMapping("/maps/{mapId}/menus")
    public List<MenuResDto> update(
            @Min(1) @PathVariable("mapId") Long mapId, @Valid @RequestBody List<MenuUpdateDto> menuUpdateDtos) {
        return menuService.update(mapId, menuUpdateDtos);
    }

    @Operation(summary = "메뉴 삭제", description = "메뉴를 삭제합니다.")
    @DeleteMapping("/menus/{id}")
    public void delete(@Min(1) @PathVariable("id") Long id) {
        menuService.delete(id);
    }
}
