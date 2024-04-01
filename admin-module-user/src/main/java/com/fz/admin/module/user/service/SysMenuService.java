package com.fz.admin.module.user.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.fz.admin.module.user.model.param.MenuCreateOrUpdateParam;
import com.fz.admin.module.user.model.param.MenuListParam;
import com.fz.admin.module.user.model.entity.SysMenu;
import com.fz.admin.module.user.model.vo.MenuRouteVO;

import java.util.List;
import java.util.Set;


public interface SysMenuService extends IService<SysMenu> {


    /**
     * @param id 用户id
     * @return 前端路由信息
     */
    List<MenuRouteVO> getRoutesByUserId(Long id);

    /**
     * 获取拥有该菜单权限的角色编号集合
     *
     * @param menuIds 菜单id
     * @return 拥有菜单权限的角色id
     */
    Set<Long> getRoleIdsByMenuId(Long menuIds);


    /**
     * 创建菜单
     *
     * @param pram 菜单参数
     * @return 菜单id
     */
    Long createMenu(MenuCreateOrUpdateParam pram);


    /**
     * 更新菜单
     *
     * @param param 更新参数
     */
    void updateMenu(MenuCreateOrUpdateParam param);

    /**
     * 删除菜单
     *
     * @param id 菜单id
     */
    void deleteMenu(Long id);

    /**
     * 获取菜单列表
     *
     * @param param 查询条件
     * @return 菜单列表
     */
    List<SysMenu> getMenuList(MenuListParam param);

    /**
     * 获取只包含部分数据的菜单列表,用于下拉展示
     *
     * @return 菜单列表
     */
    List<SysMenu> getSimpleMenus();
}
