package com.fz.admin.framework.security.service;


/**
 * 权限 Service 接口
 * 提供用户-角色、角色-菜单、角色-部门的关联权限处理
 */
public interface PermissionService {

    /**
     * 判断是否有权限，任一一个即可
     * @param permissions 权限
     * @return 是否
     */
    boolean hasAnyPermission(String ...permissions);


    boolean hasPermission(String permission);

    /**
     * 判断是否有角色，任一一个即可
     *
     * @param roles 需要鉴权的角色数组
     * @return 是否
     */
    boolean hasAnyRoles(Long userId, String... roles);


}
