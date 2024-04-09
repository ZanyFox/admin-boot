package com.fz.admin.module.user.controller.admin;

import com.fz.admin.framework.common.pojo.PageResult;
import com.fz.admin.framework.common.pojo.ServRespEntity;
import com.fz.admin.framework.web.validation.group.CreateOrUpdateGroup;
import com.fz.admin.module.user.model.entity.SysUser;
import com.fz.admin.module.user.model.param.*;
import com.fz.admin.module.user.model.vo.MenuRouteVO;
import com.fz.admin.module.user.model.vo.UserPermMenuInfoVO;
import com.fz.admin.module.user.service.SysMenuService;
import com.fz.admin.module.user.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.fz.admin.framework.common.pojo.ServRespEntity.success;
import static com.fz.admin.framework.security.util.SecurityContextUtils.getLoginUserId;


@Tag(name = "用户管理")
@AllArgsConstructor
@Validated
@RestController
@RequestMapping("/system/user")
public class UserController {


    private SysUserService userService;

    private SysMenuService menuService;


    @PostMapping("/create")
    @Operation(summary = "新增用户")
    // @PreAuthorize("@ss.hasPermission('system:user:create')")
    public ServRespEntity<Long> createUser(@Validated @RequestBody UserSaveParam param) {
        Long id = userService.createUser(param);
        return success(id);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除用户")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    // @PreAuthorize("@ss.hasAnyPermission('system:user:delete')")
    public ServRespEntity<Boolean> deleteUser(@RequestParam("id") Long id) {
        userService.deleteUser(id);
        return success(true);
    }

    @DeleteMapping("/delete-batch")
    @Operation(summary = "删除用户")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    // @PreAuthorize("@ss.hasAnyPermission('system:user:delete')")
    public ServRespEntity<Boolean> deleteUserBatch(@RequestBody @NotEmpty(message = "不可为空") List<Long> ids) {
        userService.deleteUserBatchByIds(ids);
        return success(true);
    }

    @PutMapping("update")
    @Operation(summary = "修改用户")
    // @PreAuthorize("@ss.hasAnyPermission('system:user:update')")
    public ServRespEntity<Boolean> updateUser(@Validated({CreateOrUpdateGroup.UpdateGroup.class}) @RequestBody UserSaveParam param) {
        userService.updateUser(param);
        return success(true);
    }

    @PutMapping("/update-password")
    @Operation(summary = "重置用户密码")
    // @PreAuthorize("@ss.hasAnyPermission('system:user:update-password')")
    public ServRespEntity<Boolean> updateUserPassword(@Validated @RequestBody UserUpdatePasswordParam param) {
        userService.updateUserPassword(param.getId(), param.getPassword());
        return success(true);
    }

    @PutMapping("/update-status")
    @Operation(summary = "修改用户状态")
    // @PreAuthorize("@ss.hasAnyPermission('system:user:update')")
    public ServRespEntity<Boolean> updateUserStatus(@Validated @RequestBody UserUpdateStatusParam param) {
        userService.updateUserStatus(param.getId(), param.getStatus());
        return success(true);
    }

    @Operation(summary = "获得用户分页列表")
    // @PreAuthorize("@ss.hasAnyPermission('system:user:list')")
    @GetMapping("/page")
    public ServRespEntity<PageResult<SysUser>> getUserPage(UserPageParam pageParam) {
        return success(userService.getUserpage(pageParam));
    }


    @GetMapping("/get")
    @Operation(summary = "获得用户详情")
    @Parameter(name = "id", description = "编号", required = true, example = "0")
    // @PreAuthorize("@ss.hasAnyPermission('system:user:query')")
    public ServRespEntity<SysUser> getUser(@RequestParam("id") Long id) {
        SysUser user = userService.getUserDetail(id);
        return success(user);
    }


    @GetMapping("permission-info")
    public ServRespEntity<UserPermMenuInfoVO> getPermissionMenuInfo() {

        UserPermMenuInfoVO info = userService.getUserInfoWithPermissions(getLoginUserId());
        return success(info);
    }


    @Operation(summary = "获取当前用户路由信息")
    @GetMapping("routes")
    public ServRespEntity<List<MenuRouteVO>> getMenuRoute() {
        return success(menuService.getRoutesByUserId(getLoginUserId()));
    }

    @Operation(summary = "获取没被分配该角色的用户列表")
    @Parameter(name = "roleId", description = "角色id", required = true)
    @GetMapping("/unassigned-role")
    public ServRespEntity<PageResult<SysUser>> listUsersNotAssignedRole(@RequestParam("roleId") Long roleId, UserAssignRolePageParam param) {
        return success(userService.getUserNotAssignedRolePage(roleId, param));
    }

    @Operation(summary = "获取被分配该角色的用户列表")
    @Parameter(name = "roleId", description = "角色id", required = true)
    @GetMapping("/assigned-role")
    public ServRespEntity<PageResult<SysUser>> listUsersAssignedRole(@RequestParam("roleId") Long roleId, UserAssignRolePageParam param) {
        return success(userService.getUserAssignedRolePage(roleId, param));
    }
}
