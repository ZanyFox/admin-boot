package com.fz.admin.module.user.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.fz.admin.framework.common.pojo.PageResult;
import com.fz.admin.module.user.model.param.RoleAssignUsersParam;
import com.fz.admin.module.user.model.param.RoleSaveParam;
import com.fz.admin.module.user.model.param.RolePageParam;
import com.fz.admin.module.user.model.entity.SysRole;
import com.fz.admin.module.user.model.pojo.RoleDataScope;

import java.util.List;
import java.util.Set;

public interface SysRoleService extends IService<SysRole> {

    List<SysRole> getRoleList(Set<Long> roleIds);

    /**
     * 获得角色，从缓存中
     *
     * @param id 角色编号
     * @return 角色
     */
    SysRole getRoleFromCache(Long id);

    boolean hasAdmin(List<Long> roleIds);

    /**
     * 从缓存冲获取用户所拥有的角色
     * @param userId 用户id
     * @return 角色列表
     */
    List<SysRole> getRolesByUserIdFromCache(Long userId);

    /**
     * 获取用户数据权限
     *
     * @param userId 用户id
     */
    RoleDataScope getDataScopeByUserId(Long userId);


    /**
     * 根据用户id获取角色列表
     * @param userId 用户 id
     * @return 角色列表
     */
    List<SysRole> getRolesByUserId(Long userId);

    /**
     * 创建角色
     * @param param 角色参数
     * @return 角色id
     */
    Long createRole(RoleSaveParam param);

    /**
     * 更新角色信息
     * @param param 角色信息
     */
    void updateRole(RoleSaveParam param);

    /**
     * 更新角色状态
     * @param id 角色id
     * @param status 状态
     */
    void updateRoleStatus(Long id, Integer status);

    /**
     * 删除角色
     *
     * @param roleId 角色id
     */
    void deleteRole(Long roleId);

     /**
     * 角色分页
     * @param param 查询条件
     * @return 分页结果
     */
    PageResult<SysRole> getRolePage(RolePageParam param);


    /**
     * 获取角色包含id和name的列表
     * @return 角色列表
     */
    List<SysRole> getSimpleRoles();

    /**
     * 获取拥有该菜单权限的角色编号列表
     *
     * @param menuId 菜单id
     * @return 拥有菜单权限的角色id
     */
    List<SysRole> getRolesByMenuId(Long menuId);

    /**
     * 授予角色数据权限
     * @param roleId 角色id
     * @param dataScope 数据权限 详见 {@link com.fz.admin.framework.common.enums.DataScopeEnum}
     * @param dataScopeDeptIds 当数据权限为指定部门时的部门id列表
     */
    void updateRoleDataScope(Long roleId, Integer dataScope, Set<Long> dataScopeDeptIds);

    /**
     * 批量删除角色
     * @param ids
     */
    void deleteRoleBatchByIds(Set<Long> ids);

    /**
     * 为角色批量分配用户
     * @param param 参数
     */
    void assignUsers(RoleAssignUsersParam param);

    /**
     * 批量撤销用户角色
     *
     * @param roleId
     * @param userIds
     */
    void deleteUserRoleBatch(Long roleId, Set<Long> userIds);
}
