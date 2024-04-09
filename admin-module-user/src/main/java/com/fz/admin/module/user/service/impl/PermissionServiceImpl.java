package com.fz.admin.module.user.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.fz.admin.framework.common.enums.CommonStatusEnum;
import com.fz.admin.framework.common.enums.RoleEnum;
import com.fz.admin.framework.common.enums.ServRespCode;
import com.fz.admin.framework.common.exception.ServiceException;
import com.fz.admin.module.user.mapper.SysRoleMapper;
import com.fz.admin.module.user.mapper.SysRoleMenuMapper;
import com.fz.admin.module.user.mapper.SysUserMapper;
import com.fz.admin.module.user.mapper.SysUserRoleMapper;
import com.fz.admin.module.user.model.entity.*;
import com.fz.admin.module.user.service.PermissionService;
import com.fz.admin.module.user.service.SysMenuService;
import com.fz.admin.module.user.service.SysRoleService;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.fz.admin.framework.common.util.CollectionConverter.convertList;
import static com.fz.admin.framework.common.util.CollectionConverter.convertSet;
import static com.fz.admin.framework.security.util.SecurityContextUtils.getLoginUserId;


@Service
@AllArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private SysRoleMapper roleMapper;

    private SysUserRoleMapper userRoleMapper;

    private SysRoleService roleService;

    private SysMenuService menuService;

    private SysRoleMenuMapper roleMenuMapper;

    private SysUserMapper userMapper;

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
    public boolean hasAnyPermissions(Long userId, String... permissions) {

        if (ObjectUtils.isEmpty(permissions)) return true;


        List<SysRole> enabledRoles = roleService.getRolesByUserId(userId).stream()
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
    public boolean hasPermission(Long userId, String permission) {
        return hasAnyPermissions(userId, permission);
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
        Set<Long> menuRoleIds = roleService.getRolesByMenuId(menu.getId()).stream().map(SysRole::getId).collect(Collectors.toSet());


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


    @Override
    public void assignRoleDataScope(Long roleId, Integer dataScope, Set<Long> dataScopeDeptIds) {
        roleService.updateRoleDataScope(roleId, dataScope, dataScopeDeptIds);
    }

    /*
     * 计算用户需要新增的角色和需要删除的角色再进行新增和删除
     * */
    @Transactional
    @Override
    public void updateUserRole(Long userId, Set<Long> roleIds, boolean needValidate) {

        if (needValidate) {
            SysUser sysUser = ChainWrappers.lambdaQueryChain(userMapper).eq(SysUser::getId, userId).one();
            if (sysUser == null)
                throw new ServiceException(ServRespCode.REQUEST_PARAMETER_ERROR.getCode(), "用户不存在");
        }

        List<SysUserRole> userRoles = ChainWrappers.lambdaQueryChain(userRoleMapper).eq(SysUserRole::getUserId, userId).list();
        // 获得用户拥有角色编号
        Set<Long> dbRoleIds = convertSet(userRoles, SysUserRole::getRoleId);

        // 计算新增和删除的角色编号
        Set<Long> roleIdList = CollUtil.emptyIfNull(roleIds);
        // 计算差集 返回 roleIdList 中存在的而 dbRoleIds 中不存在的 也就是需要新增的角色
        Collection<Long> createRoleIds = CollUtil.subtract(roleIdList, dbRoleIds);
        // 计算差集 返回 dbRoleIds 中存在的而 roleIdList 中不存在的 也就是删除的角色
        Collection<Long> deleteMenuIds = CollUtil.subtract(dbRoleIds, roleIdList);

        // 执行新增和删除。对于已经授权的角色，不用做任何处理
        if (ObjectUtils.isNotEmpty(createRoleIds)) {

            List<SysUserRole> createRoles = createRoleIds.stream().map(id -> {
                SysUserRole sysUserRole = new SysUserRole();
                sysUserRole.setRoleId(id);
                sysUserRole.setUserId(userId);
                return sysUserRole;
            }).toList();

            userRoleMapper.insertBatch(createRoles);

        }
        if (ObjectUtils.isNotEmpty(deleteMenuIds)) {
            ChainWrappers.lambdaUpdateChain(userRoleMapper).eq(SysUserRole::getUserId, userId)
                    .in(SysUserRole::getRoleId, deleteMenuIds)
                    .remove();
        }
    }

    /*
     * 全部删除后再插入
     * */
    @Transactional
    @Override
    public void assignRoleMenu(Long roleId, Set<Long> menuIds) {

        ChainWrappers.lambdaUpdateChain(roleMenuMapper).eq(SysRoleMenu::getRoleId, roleId).remove();

        if (ObjectUtils.isNotEmpty(menuIds)) {
            List<SysRoleMenu> roleMenus = menuIds.stream().map(id -> {
                SysRoleMenu sysRoleMenu = new SysRoleMenu();
                sysRoleMenu.setRoleId(roleId);
                sysRoleMenu.setMenuId(id);
                return sysRoleMenu;
            }).toList();
            roleMenuMapper.insertBatch(roleMenus);
        }
    }


    /*
     * 计算出差集，再执行删除和插入
     * */
    @Transactional
    protected void assignRoleMenu2(Long roleId, Set<Long> menuIds) {

        List<SysRoleMenu> sysRoleMenus = roleMenuMapper.selectList(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, roleId));
        // 获得角色拥有菜单编号
        Set<Long> dbMenuIds = convertSet(sysRoleMenus, SysRoleMenu::getMenuId);
        // 计算新增和删除的菜单编号
        Set<Long> menuIdList = CollUtil.emptyIfNull(menuIds);
        Collection<Long> createMenuIds = CollUtil.subtract(menuIdList, dbMenuIds);
        Collection<Long> deleteMenuIds = CollUtil.subtract(dbMenuIds, menuIdList);

        // 执行新增和删除。对于已经授权的菜单，不用做任何处理
        if (CollUtil.isNotEmpty(createMenuIds)) {
            Db.saveBatch(convertList(createMenuIds, menuId -> {
                SysRoleMenu entity = new SysRoleMenu();
                entity.setRoleId(roleId);
                entity.setMenuId(menuId);
                return entity;
            }));
        }
        if (CollUtil.isNotEmpty(deleteMenuIds)) {
            roleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>()
                    .eq(SysRoleMenu::getRoleId, roleId)
                    .in(SysRoleMenu::getMenuId, menuIds));
        }
    }

}
