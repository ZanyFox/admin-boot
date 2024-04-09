package com.fz.admin.module.user.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.fz.admin.module.user.model.entity.SysMenu;
import com.fz.admin.module.user.model.param.MenuCreateOrUpdateParam;
import com.fz.admin.module.user.model.param.MenuListParam;
import com.fz.admin.module.user.model.vo.MenuRouteVO;
import com.fz.admin.module.user.model.vo.MenuSimpleRespVO;

import java.util.List;
import java.util.Set;


public interface SysMenuService extends IService<SysMenu> {


    /**
     * @param id 用户id
     * @return 前端路由信息
     */
    List<MenuRouteVO> getRoutesByUserId(Long id);



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
    List<MenuSimpleRespVO> getSimpleMenus();


    /**
     * 根据角色id获取菜单
     * @param roleIds 角色id列表
     * @param param
     * @return
     */
    List<SysMenu> listMenuByRoleIds(Set<Long> roleIds, MenuListParam param);

}
