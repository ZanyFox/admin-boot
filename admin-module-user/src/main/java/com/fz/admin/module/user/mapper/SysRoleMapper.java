package com.fz.admin.module.user.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fz.admin.module.user.model.param.RolePageParam;
import com.fz.admin.module.user.model.entity.SysRole;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysRoleMapper extends BaseMapper<SysRole> {

    List<SysRole> selectRoleByUserId(Long id);

    /**
     * 根据菜单权限获取拥有权限的角色
     * @param perms
     * @return
     */
    List<SysRole> selectRoleByMenuPerms(@Param("perms") List<String> perms);

    List<SysRole> selectRolesByUserId(Long userId);

    IPage<SysRole> selectRolePage(IPage<SysRole> page, @Param("param") RolePageParam param);
}




