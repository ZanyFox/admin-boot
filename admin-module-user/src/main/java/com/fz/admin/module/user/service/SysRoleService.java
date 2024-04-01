package com.fz.admin.module.user.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.fz.admin.framework.common.pojo.PageResult;
import com.fz.admin.module.user.model.param.RoleCreateParam;
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
     * @return 角色集合
     */
    List<SysRole> getRolesByUserIdFromCache(Long userId);

    /**
     * 获取用户数据权限
     *
     * @param userId 用户id
     */
    RoleDataScope getDataScopeByUserId(Long userId);


    /**
     * 根据用户id获取角色集合
     * @param userId 用户 id
     * @return 角色集合
     */
    List<SysRole> getRolesByUserId(Long userId);

    /**
     * 创建角色
     * @param param 角色参数
     * @return 角色id
     */
    Long createRole(RoleCreateParam param);

    /**
     * 更新角色信息
     * @param param 角色信息
     */
    void updateRole(RoleCreateParam param);

    /**
     * 更新角色状态
     * @param id 角色id
     * @param status 状态
     */
    void updateRoleStatus(Long id, Integer status);

    /**
     * 删除角色
     * @param id 角色id
     */
    void deleteRole(Long id);

    SysRole getRoleById(Long id);


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
}
