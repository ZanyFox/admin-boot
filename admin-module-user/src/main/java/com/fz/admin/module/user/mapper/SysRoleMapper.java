package com.fz.admin.module.user.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fz.admin.module.user.model.entity.SysRole;
import com.fz.admin.module.user.model.param.RolePageParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysRoleMapper extends BaseMapper<SysRole> {

    /**
     * 查询角色列表 通过用户id
     * @param id 用户id
     * @return 角色列表
     */
    List<SysRole> selectRoleByUserId(Long id);

    /**
     * 根据菜单权限获取拥有权限的角色
     *
     * @param perms
     * @return
     */
    List<SysRole> selectRoleByMenuPerms(@Param("perms") List<String> perms);

    /**
     * 查询角色通过用户id
     * @param userId 用户id
     * @return 角色列表
     */
    List<SysRole> selectRolesByUserId(Long userId);

    IPage<SysRole> selectRolePage(IPage<SysRole> page, @Param("param") RolePageParam param);


    /**
     * 查询拥有菜单权限的角色
     *
     * @param menuId 菜单id
     * @return 角色列表
     */
    List<SysRole> selectRolesByMenuId(Long menuId);

}




