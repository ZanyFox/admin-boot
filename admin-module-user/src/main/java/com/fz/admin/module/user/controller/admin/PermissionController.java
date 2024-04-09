package com.fz.admin.module.user.controller.admin;

import com.fz.admin.framework.common.pojo.ServRespEntity;
import com.fz.admin.module.user.model.entity.SysMenu;
import com.fz.admin.module.user.model.entity.SysRole;
import com.fz.admin.module.user.model.param.RoleUpdateDataScopeParam;
import com.fz.admin.module.user.model.param.RoleAssignMenuParam;
import com.fz.admin.module.user.model.param.UserAssignRoleParam;
import com.fz.admin.module.user.service.PermissionService;
import com.fz.admin.module.user.service.SysMenuService;
import com.fz.admin.module.user.service.SysRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.fz.admin.framework.common.pojo.ServRespEntity.success;
import static com.fz.admin.framework.common.util.CollectionConverter.convertSet;


/**
 * 提供赋予用户、角色的权限的 API 接口
 */
@Tag(name = "权限")
@AllArgsConstructor
@RestController
@RequestMapping("/system/permission")
public class PermissionController {

    private PermissionService permissionService;
    private SysMenuService menuService;
    private SysRoleService roleService;


    @Operation(summary = "获得角色拥有的菜单id列表")
    @Parameter(name = "roleId", description = "角色编号", required = true)
    @GetMapping("/list-role-menus")
    // @PreAuthorize("@ss.hasPermission('system:permission:assign-role-menu')")
    public ServRespEntity<Set<Long>> getRoleMenuList(Long roleId) {
        List<SysMenu> menus = menuService.listMenuByRoleIds(Collections.singleton(roleId), null);
        return success(convertSet(menus, SysMenu::getId));
    }

    @PostMapping("/assign-role-menu")
    @Operation(summary = "授予角色菜单权限")
    // @PreAuthorize("@ss.hasPermission('system:permission:assign-role-menu')")
    public ServRespEntity<Boolean> assignRoleMenu(@Validated @RequestBody RoleAssignMenuParam param) {
        // 执行菜单的分配
        permissionService.assignRoleMenu(param.getRoleId(), param.getMenuIds());
        return success(true);
    }

    @PostMapping("/assign-role-data-scope")
    @Operation(summary = "授予角色数据权限")
    // @PreAuthorize("@ss.hasPermission('system:permission:assign-role-data-scope')")
    public ServRespEntity<Boolean> assignRoleDataScope(@Validated @RequestBody RoleUpdateDataScopeParam param) {
        permissionService.assignRoleDataScope(param.getRoleId(), param.getDataScope(), param.getDataScopeDeptIds());
        return success(true);
    }


    @Operation(summary = "获取管理员拥有的角色id列表")
    @Parameter(name = "userId", description = "用户编号", required = true)
    @GetMapping("/list-user-roles")
    // @PreAuthorize("@ss.hasPermission('system:permission:assign-user-role')")
    public ServRespEntity<Set<Long>> getRoleIdsByUserId(@RequestParam("userId") Long userId) {
        Set<Long> ids = convertSet(roleService.getRolesByUserId(userId), SysRole::getId);
        return success(ids);
    }


    @Operation(summary = "授予用户角色")
    @PostMapping("/assign-user-role")
    // @PreAuthorize("@ss.hasPermission('system:permission:assign-user-role')")
    public ServRespEntity<Boolean> assignUserRole(@Validated @RequestBody UserAssignRoleParam param) {
        permissionService.updateUserRole(param.getUserId(), param.getRoleIds(), true);
        return success(true);
    }

}
