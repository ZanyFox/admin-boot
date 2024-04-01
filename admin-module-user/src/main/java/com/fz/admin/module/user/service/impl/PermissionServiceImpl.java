package com.fz.admin.module.user.service.impl;

import com.fz.admin.framework.common.enums.CommonStatusEnum;
import com.fz.admin.framework.common.enums.RoleEnum;
import com.fz.admin.framework.security.service.PermissionService;
import com.fz.admin.module.user.mapper.SysRoleMapper;
import com.fz.admin.module.user.mapper.SysUserRoleMapper;
import com.fz.admin.module.user.model.entity.SysMenu;
import com.fz.admin.module.user.model.entity.SysRole;
import com.fz.admin.module.user.service.SysMenuService;
import com.fz.admin.module.user.service.SysRoleService;
import com.fz.admin.module.user.service.SysUserRoleService;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.fz.admin.framework.common.util.CollectionConverter.convertList;
import static com.fz.admin.framework.common.util.CollectionConverter.convertSet;
import static com.fz.admin.framework.security.util.SecurityContextUtils.getLoginUserId;


@Service("ss")
@AllArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private SysRoleMapper roleMapper;

    private SysUserRoleMapper userRoleMapper;

    private SysUserRoleService userRoleService;

    private SysRoleService roleService;

    private StringRedisTemplate stringRedisTemplate;

    private SysMenuService menuService;

    /*
     * 循环查询
     */
    private boolean hasAnyPermission1(String... permissions) {

        if (ObjectUtils.isEmpty(permissions)) return true;


        List<SysRole> enabledRoles = roleService.getRolesByUserIdFromCache(getLoginUserId()).stream()
                .filter((role) -> CommonStatusEnum.isEnable(role.getStatus()))
                .collect(Collectors.toList());


        List<Long> roleIds = convertList(enabledRoles, SysRole::getId);

        // 当前用户角色为空时 无权限
        if (ObjectUtils.isEmpty(roleIds)) {
            return false;
        }

        // 遍历判断每个权限，如果有一满足，说明有权限
        for (String permission : permissions) {
            if (hasAnyPermission(roleIds, permission)) {
                return true;
            }
        }
        // 如果时admin也有权限
        return roleService.hasAdmin(roleIds);
    }

    @Override
    public boolean hasAnyPermission(String... permissions) {

        if (ObjectUtils.isEmpty(permissions)) return true;


        List<SysRole> enabledRoles = roleService.getRolesByUserId(getLoginUserId()).stream()

                .filter((role) -> CommonStatusEnum.isEnable(role.getStatus()))
                .collect(Collectors.toList());


        List<Long> roleIds = convertList(enabledRoles, SysRole::getId);

        // 当前用户角色为空时 无权限
        if (ObjectUtils.isEmpty(roleIds)) {
            return false;
        }

        // 获取拥有权限的角色
        List<Long> menuRoleIds = roleMapper.selectRoleByMenuPerms(Arrays.asList(permissions))
                .stream().filter((role) -> CommonStatusEnum.isEnable(role.getStatus())).map(SysRole::getId).toList();

        List<String> roleKeys = convertList(enabledRoles, SysRole::getKey);

        // 有权限 或者是管理
        return roleIds.stream().anyMatch(menuRoleIds::contains) || roleKeys.contains(RoleEnum.SUPER_ADMIN.getKey());
    }

    @Override
    public boolean hasPermission(String permission) {
        return hasAnyPermission(permission);
    }


    /**
     * 判断指定角色，是否拥有该 permission 权限
     *
     * @param roleIds    指定角色数组
     * @param permission 权限标识
     * @return 是否拥有
     */
    private boolean hasAnyPermission(List<Long> roleIds, String permission) {

        // 角色为空 无权限
        if (ObjectUtils.isEmpty(roleIds)) return false;

        SysMenu menu = menuService.lambdaQuery()
                .eq(SysMenu::getPerm, permission)
                .one();

        if (menu == null) return false;

        // 获取拥有该权限的角色id列表
        Set<Long> menuRoleIds = menuService.getRoleIdsByMenuId(menu.getId());


        // 与用户拥有的角色对比 如果有交集，说明有权限
        return roleIds.stream().anyMatch(menuRoleIds::contains);
    }


    @Override
    public boolean hasAnyRoles(Long userId, String... roles) {
        // 如果为空，说明已经有权限
        if (ObjectUtils.isEmpty(roles)) {
            return true;
        }

        // 获得当前用户启用的角色。如果为空，说明没有权限
        List<SysRole> enabledRoles = roleService.getRolesByUserIdFromCache(userId).stream()
                .filter((role) -> CommonStatusEnum.isEnable(role.getStatus()))
                .collect(Collectors.toList());

        if (ObjectUtils.isEmpty(enabledRoles)) {
            return false;
        }

        // 判断是否有角色
        Set<String> userRoles = convertSet(enabledRoles, SysRole::getKey);

        return userRoles.stream().anyMatch(s -> List.of(roles).contains(s));
    }

}
