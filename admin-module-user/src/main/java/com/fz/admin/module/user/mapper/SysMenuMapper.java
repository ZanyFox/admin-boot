package com.fz.admin.module.user.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fz.admin.module.user.model.entity.SysMenu;
import com.fz.admin.module.user.model.entity.SysRole;

import java.util.List;

public interface SysMenuMapper extends BaseMapper<SysMenu> {


    List<SysMenu> selectAllMenu();

    List<SysMenu> selectMenuUserId(Long userId);

    List<SysRole> selectRolesByMenuId(Long menuId);
}




