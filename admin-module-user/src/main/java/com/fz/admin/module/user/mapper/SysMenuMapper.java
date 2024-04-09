package com.fz.admin.module.user.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fz.admin.module.user.model.entity.SysMenu;
import com.fz.admin.module.user.model.param.MenuListParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

public interface SysMenuMapper extends BaseMapper<SysMenu> {


    List<SysMenu> selectAllMenu();

    List<SysMenu> selectMenuUserId(Long userId);

    /**
     * 查询菜单通过用户id
     * @param userId 用户id
     * @return 菜单列表
     */
    List<SysMenu> selectMenusByUserId(Long userId);

    List<SysMenu> selectMenusByRoleIds(@Param("roleIds") Set<Long> roleIds, @Param("param") MenuListParam param);
}




