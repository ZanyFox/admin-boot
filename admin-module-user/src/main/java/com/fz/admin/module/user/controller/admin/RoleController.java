package com.fz.admin.module.user.controller.admin;


import com.fz.admin.framework.common.pojo.PageResult;
import com.fz.admin.framework.common.pojo.ServRespEntity;
import com.fz.admin.module.user.model.param.RoleCreateParam;
import com.fz.admin.module.user.model.param.RolePageParam;
import com.fz.admin.module.user.model.param.RoleUpdateStatusParam;
import com.fz.admin.module.user.model.entity.SysRole;
import com.fz.admin.module.user.service.SysRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.fz.admin.framework.common.pojo.ServRespEntity.success;

@Validated
@Tag(name = "角色管理")
@RestController
@AllArgsConstructor
@RequestMapping("/system/role")
public class RoleController {


    private SysRoleService roleService;

    @PostMapping("/create")
    @Operation(summary = "创建角色")
    // @PreAuthorize("@ss.hasAnyPermission('system:role:create')")
    public ServRespEntity<Long> createRole(@Validated @RequestBody RoleCreateParam param) {
        return success(roleService.createRole(param));
    }


    @PutMapping("/update")
    @Operation(summary = "修改角色")
    // @PreAuthorize("@ss.hasAnyPermission('system:role:update')")
    public ServRespEntity<Boolean> updateRole(@Valid @RequestBody RoleCreateParam param) {
        roleService.updateRole(param);
        return success(true);
    }


    @PutMapping("/update-status")
    @Operation(summary = "修改角色状态")
    // @PreAuthorize("@ss.hasAnyPermission('system:role:update')")
    public ServRespEntity<Boolean> updateRoleStatus(@Valid @RequestBody RoleUpdateStatusParam reqVO) {
        roleService.updateRoleStatus(reqVO.getId(), reqVO.getStatus());
        return success(true);
    }


    @DeleteMapping("/delete")
    @Operation(summary = "删除角色")
    @Parameter(name = "id", description = "角色id", required = true, example = "0")
    // @PreAuthorize("@ss.hasPermission('system:role:delete')")
    public ServRespEntity<Boolean> deleteRole(@RequestParam("id") Long id) {
        roleService.deleteRole(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得角色信息")
    // @PreAuthorize("@ss.hasPermission('system:role:query')")
    public ServRespEntity<SysRole> getRole(@RequestParam("id") @Digits(integer = 10, fraction = 0, message = "参数不合法") Long id) {

        SysRole role = roleService.getById(id);
        return success(role);
    }

    @GetMapping("/page")
    @Operation(summary = "获得角色分页")
    // @PreAuthorize("@ss.hasPermission('system:role:query')")
    public ServRespEntity<PageResult<SysRole>> getRolePage(RolePageParam param) {
        PageResult<SysRole> pageResult = roleService.getRolePage(param);
        return success(pageResult);
    }


    @GetMapping({"/list-all-simple", "/simple-list"})
    @Operation(summary = "获取角色精简信息列表", description = "只包含被开启的角色，主要用于前端的下拉选项")
    public ServRespEntity<List<SysRole>> getSimpleRoleList() {
        List<SysRole> list = roleService.getSimpleRoles();
        return success(list);
    }



}
