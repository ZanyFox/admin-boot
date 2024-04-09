package com.fz.admin.module.user.controller.admin;


import com.fz.admin.framework.common.pojo.ServRespEntity;
import com.fz.admin.module.user.model.entity.SysMenu;
import com.fz.admin.module.user.model.param.MenuCreateOrUpdateParam;
import com.fz.admin.module.user.model.param.MenuListParam;
import com.fz.admin.module.user.model.vo.MenuSimpleRespVO;
import com.fz.admin.module.user.service.SysMenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.fz.admin.framework.common.pojo.ServRespEntity.success;
import static com.fz.admin.framework.common.util.CollectionConverter.convertSet;

@Tag(name = "菜单管理")
@Validated
@RestController
@RequestMapping("/system/menu")
@AllArgsConstructor
public class MenuController {


    private SysMenuService menuService;


    @PostMapping("/create")
    @Operation(summary = "创建菜单")
    // @PreAuthorize("@ss.hasPermission('system:menu:create')")
    public ServRespEntity<Long> createMenu(@Validated @RequestBody MenuCreateOrUpdateParam pram) {
        Long menuId = menuService.createMenu(pram);
        return success(menuId);
    }


    @PutMapping("/update")
    @Operation(summary = "修改菜单")
    // @PreAuthorize("@ss.hasPermission('system:menu:update')")
    public ServRespEntity<Boolean> updateMenu(@Valid @RequestBody MenuCreateOrUpdateParam param) {
        menuService.updateMenu(param);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除菜单")
    @Parameter(name = "id", description = "菜单id", required = true, example = "0")
    // @PreAuthorize("@ss.hasPermission('system:menu:delete')")
    public ServRespEntity<Boolean> deleteMenu(@RequestParam("id") Long id) {
        menuService.deleteMenu(id);
        return success(true);
    }


    @GetMapping("/list")
    @Operation(summary = "获取菜单列表", description = "用于菜单展示")
    // @PreAuthorize("@ss.hasPermission('system:menu:query')")
    public ServRespEntity<List<SysMenu>> getMenuList(MenuListParam param) {
        List<SysMenu> list = menuService.getMenuList(param);
        return success(list);
    }

    @GetMapping({"/list-all-simple", "simple-list"})
    @Operation(summary = "获取菜单精简信息列表", description = "只包含被开启的菜单，用于【角色分配菜单】功能的选项。")
    public ServRespEntity<List<MenuSimpleRespVO>> getSimpleMenuList() {
        List<MenuSimpleRespVO> list = menuService.getSimpleMenus();
        return success(list);
    }

    @GetMapping("/get")
    @Operation(summary = "获取菜单信息")
    @PreAuthorize("@ss.hasPermission('system:menu:query')")
    public ServRespEntity<SysMenu> getMenu(Long id) {
        SysMenu menu = menuService.getById(id);
        return success(menu);
    }

    @Operation(summary = "获得角色拥有的菜单id列表")
    @Parameter(name = "roleId", description = "角色编号", required = true)
    @GetMapping("/list-by-role")
    public ServRespEntity<Set<Long>> listMenuByRoleIds(@RequestParam("roleId") Long roleId) {
        List<SysMenu> menus = menuService.listMenuByRoleIds(Collections.singleton(roleId), MenuListParam.EMPTY);
        return success(convertSet(menus, SysMenu::getId));
    }


}
