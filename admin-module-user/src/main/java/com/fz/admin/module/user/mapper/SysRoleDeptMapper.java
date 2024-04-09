package com.fz.admin.module.user.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fz.admin.module.user.model.entity.SysRoleDept;

import java.util.List;

public interface SysRoleDeptMapper extends BaseMapper<SysRoleDept> {

    void insertBatch(List<SysRoleDept> sysRoleDepts);
}




