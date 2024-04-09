package com.fz.admin.module.user.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.fz.admin.module.user.model.entity.SysRoleMenu;

import java.util.Set;

public interface SysRoleMenuService extends IService<SysRoleMenu> {

    /**
     * 更新roleId的menuIds，
     * @param roleId 角色id
     * @param menuIds 菜单id集合
     */
    void updateRoleMenus(Long roleId, Set<Long> menuIds);
}
