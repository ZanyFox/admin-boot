package com.fz.admin.module.user.service.impl;


import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fz.admin.module.user.mapper.SysRoleMenuMapper;
import com.fz.admin.module.user.model.entity.SysRoleMenu;
import com.fz.admin.module.user.service.SysRoleMenuService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;

import static com.fz.admin.framework.common.util.CollectionConverter.convertList;
import static com.fz.admin.framework.common.util.CollectionConverter.convertSet;

@Service
@AllArgsConstructor
public class SysRoleMenuServiceImpl extends ServiceImpl<SysRoleMenuMapper, SysRoleMenu> implements SysRoleMenuService {


    private SysRoleMenuMapper roleMenuMapper;


    @Override
    public void updateRoleMenus(Long roleId, Set<Long> menuIds) {

        if (menuIds == null) return;

        // role现有的menu id集合
        Set<Long> dbMenuIds = convertSet(lambdaQuery().eq(SysRoleMenu::getRoleId, roleId).list(), SysRoleMenu::getMenuId);
        Set<Long> menuIdList = CollUtil.emptyIfNull(menuIds);
        // 计算差集 menuIdList 有 但是 dbMenuIds 没有，就是需要新增的菜单id
        Collection<Long> createMenuIds = CollUtil.subtract(menuIdList, dbMenuIds);
        // 计算差集 dbMenuIds 有 但是 menuIdList 没有的  就是需要删除的菜单id
        Collection<Long> deleteMenuIds = CollUtil.subtract(dbMenuIds, menuIdList);
        // 执行新增和删除。对于已经授权的菜单，不用做任何处理
        if (CollUtil.isNotEmpty(createMenuIds)) {
            roleMenuMapper.insertBatch(convertList(createMenuIds, menuId -> {
                SysRoleMenu entity = new SysRoleMenu();
                entity.setRoleId(roleId);
                entity.setMenuId(menuId);
                return entity;
            }));
        }

        if (CollUtil.isNotEmpty(deleteMenuIds)) {
            lambdaUpdate().eq(SysRoleMenu::getRoleId, roleId).in(SysRoleMenu::getMenuId, menuIds).remove();
        }
    }
}




