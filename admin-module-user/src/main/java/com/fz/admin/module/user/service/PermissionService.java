package com.fz.admin.module.user.service;

import java.util.Set;

public interface PermissionService {


    /**
     * 判断是否有权限，任一一个即可
     * @param userId 用户id
     * @param permissions 权限
     * @return 是否具有任意一个权限
     */
    boolean hasAnyPermissions(Long userId,  String... permissions);


    /**
     * @param userId 用户id
     * @param permission 菜单权限
     * @return 是否具有该权限
     */
    boolean hasPermission(Long userId, String permission);

    /**
     * 判断是否有角色，任一一个即可
     *
     * @param roles 需要鉴权的角色数组
     * @return 是否
     */
    boolean hasAnyRoles(Long userId, String... roles);

    /**
     * 为角色授予菜单权限
     * @param roleId 角色id
     * @param menuIds 菜单列表
     */
    void assignRoleMenu(Long roleId, Set<Long> menuIds);

    /**
     * 授予角色数据权限
     * @param roleId 角色id
     * @param dataScope 数据权限 详见 {@link com.fz.admin.framework.common.enums.DataScopeEnum}
     * @param dataScopeDeptIds 当数据权限为指定部门时的部门id列表
     */
    void assignRoleDataScope(Long roleId, Integer dataScope, Set<Long> dataScopeDeptIds);


    /**
     * 授予用户角色
     * @param userId 用户id
     * @param roleIds 角色id列表
     */
    void updateUserRole(Long userId, Set<Long> roleIds, boolean needValidate);


}
