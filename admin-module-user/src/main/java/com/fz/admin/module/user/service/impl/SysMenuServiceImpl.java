package com.fz.admin.module.user.service.impl;


import cn.hutool.http.HttpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fz.admin.framework.common.constant.Constants;
import com.fz.admin.framework.common.enums.CommonStatusEnum;
import com.fz.admin.framework.common.enums.ServRespCode;
import com.fz.admin.framework.common.exception.ServiceException;
import com.fz.admin.framework.common.util.CollectionConverter;
import com.fz.admin.module.user.constant.UserConstants;
import com.fz.admin.module.user.enums.MenuTypeEnum;
import com.fz.admin.module.user.mapper.SysMenuMapper;
import com.fz.admin.module.user.mapper.SysRoleMapper;
import com.fz.admin.module.user.mapper.SysRoleMenuMapper;
import com.fz.admin.module.user.mapper.SysUserMapper;
import com.fz.admin.module.user.model.entity.SysMenu;
import com.fz.admin.module.user.model.entity.SysRole;
import com.fz.admin.module.user.model.entity.SysRoleMenu;
import com.fz.admin.module.user.model.param.MenuCreateOrUpdateParam;
import com.fz.admin.module.user.model.param.MenuListParam;
import com.fz.admin.module.user.model.vo.MenuRouteVO;
import com.fz.admin.module.user.model.vo.RouteMetaVO;
import com.fz.admin.module.user.service.SysMenuService;
import com.google.common.annotations.VisibleForTesting;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.fz.admin.module.user.model.entity.SysMenu.ID_ROOT;

@Service
@AllArgsConstructor
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {


    private SysMenuMapper menuMapper;

    private SysUserMapper userMapper;

    private SysRoleMapper roleMapper;

    private SysRoleMenuMapper roleMenuMapper;

    @Override
    public List<MenuRouteVO> getRoutesByUserId(Long id) {

        List<String> roles = roleMapper.selectRoleByUserId(id).stream().map(SysRole::getKey).toList();

        List<SysMenu> menus;
        if (roles.contains("admin")) {
            menus = menuMapper.selectAllMenu();
        } else menus = menuMapper.selectMenuUserId(id);

        return buildRoutes(buildMenuTree(menus, 0L));
    }


    @Override
    public Set<Long> getRoleIdsByMenuId(Long menuId) {

        List<SysRole> sysRoles = menuMapper.selectRolesByMenuId(menuId);
        return CollectionConverter.convertSet(sysRoles, SysRole::getId);
    }


    @Override
    public Long createMenu(MenuCreateOrUpdateParam param) {

        validateParentMenu(param.getParentId(), null);
        validateMenu(param.getParentId(), param.getName(), param.getId());

        SysMenu menu = new SysMenu();
        BeanUtils.copyProperties(param, menu);
        clearBtnMenuUnnecessaryAttr(menu);
        menuMapper.insert(menu);
        return menu.getId();
    }

    @Override
    public void updateMenu(MenuCreateOrUpdateParam param) {

        if (param.getId() == null || menuMapper.selectById(param.getId()) == null)
            throw new ServiceException(ServRespCode.REQUEST_PARAMETER_ERROR.getCode(), "菜单不存在");

        validateParentMenu(param.getParentId(), param.getId());
        validateMenu(param.getParentId(), param.getName(), param.getId());

        SysMenu menu = new SysMenu();
        BeanUtils.copyProperties(param, menu);
        clearBtnMenuUnnecessaryAttr(menu);
        menuMapper.updateById(menu);
    }

    @Transactional
    @Override
    public void deleteMenu(Long id) {
        if (id == null || menuMapper.selectById(id) == null)
            throw new ServiceException(ServRespCode.REQUEST_PARAMETER_ERROR.getCode(), "菜单不存在");

        Long childCount = menuMapper.selectCount(new LambdaQueryWrapper<SysMenu>().eq(SysMenu::getParentId, id));

        // 存在子菜单不允许删除
        if (childCount > 0)
            throw new ServiceException(ServRespCode.REQUEST_PARAMETER_ERROR.getCode(), "存在子菜单,不允许删除");

        Long roleMenuCount = roleMenuMapper.selectCount(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getMenuId, id));

        // 有角色被分配该菜单,不允许删除
        if (roleMenuCount > 0)
            throw new ServiceException(ServRespCode.REQUEST_PARAMETER_ERROR.getCode(), "存在子菜单,不允许删除");


        menuMapper.deleteById(id);

    }

    @Override
    public List<SysMenu> getMenuList(MenuListParam param) {

        return lambdaQuery()
                .eq(param.getStatus() != null, SysMenu::getStatus, param.getStatus())
                .like(StringUtils.isNotBlank(param.getName()), SysMenu::getName, param.getName())
                .list();
    }

    @Override
    public List<SysMenu> getSimpleMenus() {

        return lambdaQuery()
                .select(SysMenu::getId, SysMenu::getParentId, SysMenu::getType, SysMenu::getName)
                .eq(SysMenu::getStatus, CommonStatusEnum.ENABLE.getStatus())
                .list();
    }


    /**
     * 校验父菜单是否合法
     * <p>
     * 1. 不能设置自己为父菜单
     * <p>
     * 2. 父菜单不存在
     * <p>
     * 3. 父菜单必须是 {@link MenuTypeEnum#MENU} 目录或者菜单类型
     *
     * @param parentId 父菜单编号
     * @param childId  当前菜单编号
     */

    void validateParentMenu(Long parentId, Long childId) {
        if (parentId == null || ID_ROOT.equals(parentId)) {
            return;
        }
        // 不能设置自己为父菜单
        if (parentId.equals(childId)) {
            throw new ServiceException(ServRespCode.REQUEST_PARAMETER_ERROR.getCode(), "不能设置自己为父菜单");
        }

        SysMenu menu = menuMapper.selectById(parentId);
        // 父菜单不存在
        if (menu == null) {
            throw new ServiceException(ServRespCode.REQUEST_PARAMETER_ERROR.getCode(), "父菜单不存在");

        }
        // 父菜单必须是目录或者菜单类型
        if (!MenuTypeEnum.DIR.getType().equals(menu.getType())
                && !MenuTypeEnum.MENU.getType().equals(menu.getType())) {
            throw new ServiceException(ServRespCode.REQUEST_PARAMETER_ERROR.getCode(), "父菜单的类型必须是目录或菜单");
        }
    }

    /**
     * 校验菜单是否合法
     * <p>
     * 1. 如果 id 为空表示创建时判断父菜单下名字是否唯一
     * <p>
     * 2. 如果 id 不为空,表示更新时,判断父菜单下名字是否唯一
     *
     * @param name     菜单名字
     * @param parentId 父菜单编号
     * @param id       菜单编号
     */
    @VisibleForTesting
    void validateMenu(Long parentId, String name, Long id) {
        SysMenu menu = lambdaQuery().eq(SysMenu::getParentId, parentId).eq(SysMenu::getName, name).one();
        if (menu == null) {
            return;
        }
        // 如果 id 为空，表示创建时判断名字是否唯一,所以查到了menu直接抛出异常
        if (id == null) {
            throw new ServiceException(ServRespCode.REQUEST_PARAMETER_ERROR.getCode(), "菜单名称重复");
        }
        // id不为空 表示更新时判断名字是否为唯一,需要判断查出来的id是不是更新的菜单id
        if (!menu.getId().equals(id)) {
            throw new ServiceException(ServRespCode.REQUEST_PARAMETER_ERROR.getCode(), "菜单名称重复");
        }
    }


    /**
     * 清除按钮菜单类型不必要的属性
     *
     * @param menu 菜单
     */
    private void clearBtnMenuUnnecessaryAttr(SysMenu menu) {
        // 菜单为按钮类型时，无需 component、icon、path 属性，进行置空
        if (MenuTypeEnum.BUTTON.getType().equals(menu.getType())) {
            menu.setComponent("");
            // menu.setComponentName("");
            menu.setIcon("");
            menu.setPath("");
        }
    }


    /**
     * 构建
     *
     * @param menus    SysMenu
     * @param parentId 需要构建的父id
     * @return menu及其子项
     */
    private List<SysMenu> buildMenuTree(List<SysMenu> menus, Long parentId) {

        return menus.stream().filter(sysMenu -> Objects.equals(sysMenu.getParentId(), parentId))
                .peek(sysMenu -> sysMenu.setChildren(buildMenuTree(menus, sysMenu.getId())))
                .collect(Collectors.toList());
    }


    /**
     * 是否为菜单内部跳转
     *
     * @param menu 菜单信息
     * @return 结果
     */
    private boolean isMenuFrame(SysMenu menu) {
        return isLevelOneDir(menu) && MenuTypeEnum.isMenu(menu.getMenuType()) && !menu.getIsFrame();
    }

    /**
     * 是否为内链组件
     *
     * @param menu 菜单信息
     * @return 结果
     */
    private boolean isInnerLink(SysMenu menu) {

        return !menu.getIsFrame() && (HttpUtil.isHttps(menu.getPath()) || HttpUtil.isHttp(menu.getPath()));
    }

    /**
     * 是否是一级目录
     */
    private boolean isLevelOneDir(SysMenu menu) {
        return Objects.equals(menu.getParentId(), ID_ROOT);

    }


    /**
     * 内链域名特殊字符替换
     *
     * @return 替换后的内链域名
     */
    public String innerLinkReplaceEach(String path) {

        return StringUtils.replaceEach(path, new String[]{Constants.HTTP_PREFIX, Constants.HTTPS_PREFIX, "www.", StringPool.DOT, StringPool.COLON},
                new String[]{"", "", "", StringPool.SLASH, StringPool.SLASH});
    }

    /**
     * 获取路由地址
     *
     * @param menu 菜单信息
     * @return 路由地址
     */
    private String buildRouterPath(SysMenu menu) {
        String routerPath = menu.getPath();
        // 内链打开外网方式
        if (!isLevelOneDir(menu) && isInnerLink(menu)) {
            routerPath = innerLinkReplaceEach(routerPath);
        }
        // 非外链并且是一级目录（类型为目录）
        if (isLevelOneDir(menu) && MenuTypeEnum.isDir(menu.getMenuType()) && !menu.getIsFrame()) {
            routerPath = StringPool.SLASH + menu.getPath();
        }
        // 非外链并且是一级目录（类型为菜单）
        else if (isMenuFrame(menu)) {
            routerPath = "/";
        }
        return routerPath;
    }


    /**
     * 获取组件信息
     *
     * @param menu 菜单信息
     * @return 组件信息
     */
    public String buildComponent(SysMenu menu) {
        String component = UserConstants.LAYOUT;
        if (StringUtils.isNotBlank(menu.getComponent()) && !isMenuFrame(menu)) {
            component = menu.getComponent();
        } else if (StringUtils.isEmpty(menu.getComponent()) && !isLevelOneDir(menu) && isInnerLink(menu)) {
            component = UserConstants.INNER_LINK;
            //  判断是否是ParentView组件
        } else if (StringUtils.isEmpty(menu.getComponent()) && !isLevelOneDir(menu) && MenuTypeEnum.isDir(menu.getMenuType())) {
            component = UserConstants.PARENT_VIEW;
        }
        return component;
    }


    private RouteMetaVO buildRouteMeta(SysMenu menu) {

        return RouteMetaVO.builder()
                .title(menu.getName())
                .icon(menu.getIcon())
                .noCache(!Boolean.TRUE.equals(menu.getIsCache()))
                .link(StringUtils.startsWithAny(menu.getPath(), Constants.HTTP_PREFIX, Constants.HTTPS_PREFIX) ? menu.getPath() : null)
                .build();
    }

    /**
     * 构建前端的路由信息
     */
    private List<MenuRouteVO> buildRoutes(List<SysMenu> menus) {
        List<MenuRouteVO> routes = new LinkedList<>();
        for (SysMenu menu : menus) {

            MenuRouteVO route = new MenuRouteVO();
            route.setHidden(!menu.getVisible());

            String routerName = StringUtils.capitalize(menu.getPath());
            // 非外链并且是一级目录（类型为目录）
            if (isMenuFrame(menu)) {
                routerName = StringUtils.EMPTY;
            }

            route.setName(routerName);
            route.setPath(buildRouterPath(menu));
            route.setComponent(buildComponent(menu));
            route.setQuery(menu.getQuery());
            route.setMeta(buildRouteMeta(menu));

            List<SysMenu> childMenus = menu.getChildren();
            if (!ObjectUtils.isEmpty(childMenus) && MenuTypeEnum.isDir(menu.getMenuType())) {
                route.setAlwaysShow(true);
                route.setRedirect("noRedirect");
                route.setChildren(buildRoutes(childMenus));
            } else if (isMenuFrame(menu)) {
                route.setMeta(null);

                MenuRouteVO child = new MenuRouteVO();
                child.setPath(menu.getPath());
                child.setComponent(menu.getComponent());
                child.setName(StringUtils.capitalize(menu.getPath()));
                child.setMeta(buildRouteMeta(menu));
                child.setQuery(menu.getQuery());

                route.setChildren(List.of(child));
            } else if (isLevelOneDir(menu) && isInnerLink(menu)) {

                RouteMetaVO meta = RouteMetaVO.builder()
                        .title(menu.getName())
                        .icon(menu.getIcon()).build();

                route.setMeta(meta);
                route.setPath("/");

                MenuRouteVO innerLinkRoute = new MenuRouteVO();
                String routerPath = innerLinkReplaceEach(menu.getPath());
                innerLinkRoute.setPath(routerPath);
                innerLinkRoute.setComponent(UserConstants.INNER_LINK);
                innerLinkRoute.setName(StringUtils.capitalize(routerPath));
                innerLinkRoute.setMeta(buildRouteMeta(menu));

                route.setChildren(List.of(innerLinkRoute));
            }
            routes.add(route);
        }
        return routes;
    }
}




