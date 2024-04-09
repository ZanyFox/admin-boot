package com.fz.admin.module.user.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fz.admin.module.user.model.entity.SysUserRole;

import java.util.List;

public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {

    /**
     * 批量新增
     * @param createRoles 需要新增的关系
     */
    void insertBatch(List<SysUserRole> roles);
}




