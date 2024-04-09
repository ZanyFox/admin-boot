package com.fz.admin.module.user.service.impl;


import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fz.admin.framework.common.enums.CommonStatusEnum;
import com.fz.admin.framework.common.enums.DataScopeEnum;
import com.fz.admin.framework.common.enums.RoleEnum;
import com.fz.admin.framework.common.enums.ServRespCode;
import com.fz.admin.framework.common.exception.ServiceException;
import com.fz.admin.framework.common.pojo.PageResult;
import com.fz.admin.framework.common.util.Json;
import com.fz.admin.framework.mybatis.util.MyBatisUtils;
import com.fz.admin.framework.redis.constant.RedisCacheConstant;
import com.fz.admin.module.user.mapper.*;
import com.fz.admin.module.user.model.entity.*;
import com.fz.admin.module.user.model.param.RoleAssignUsersParam;
import com.fz.admin.module.user.model.param.RolePageParam;
import com.fz.admin.module.user.model.param.RoleSaveParam;
import com.fz.admin.module.user.model.pojo.RoleDataScope;
import com.fz.admin.module.user.service.SysDeptService;
import com.fz.admin.module.user.service.SysRoleMenuService;
import com.fz.admin.module.user.service.SysRoleService;
import com.fz.admin.module.user.service.SysUserRoleService;
import com.google.common.base.Suppliers;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.fz.admin.framework.common.util.CollectionConverter.convertList;

@Slf4j
@Service
@AllArgsConstructor
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    private SysRoleMapper roleMapper;

    private SysRoleDeptMapper roleDeptMapper;

    private RedisTemplate<String, Object> redisTemplate;

    private SysUserRoleService userRoleService;

    private SysUserRoleMapper userRoleMapper;

    private SysUserMapper userMapper;

    private SysDeptService deptService;

    private SysRoleMenuMapper roleMenuMapper;

    private SysRoleMenuService roleMenuService;

    @Override
    public List<SysRole> getRoleList(Set<Long> roleIds) {

        if (ObjectUtils.isEmpty(roleIds)) return Collections.emptyList();

        return lambdaQuery().in(SysRole::getId, roleIds).list();
    }


    @Cacheable(cacheNames = RedisCacheConstant.ROLE, key = "#id", unless = "#result == null ")
    @Override
    public SysRole getRoleFromCache(Long id) {
        return roleMapper.selectById(id);
    }


    @Override
    public boolean hasAdmin(List<Long> roleIds) {

        if (ObjectUtils.isEmpty(roleIds)) return false;
        SysRoleServiceImpl self = (SysRoleServiceImpl) AopContext.currentProxy();

        return roleIds.stream().anyMatch((id) -> {
            SysRole role = self.getRoleFromCache(id);
            return role != null && RoleEnum.isSuperAdmin(role.getKey());
        });
    }

    /**
     * 批量获取role，先从缓存中获取，没有再从数据库中获取
     *
     * @param ids 需要获取的角色的 id
     * @return 角色列表
     */
    private List<SysRole> getRolesFromCacheByRoleIds(List<Long> ids) {

        if (ObjectUtils.isEmpty(ids)) return Collections.emptyList();

        List<Object> roleCache = redisTemplate.opsForValue().multiGet(convertList(ids, id -> RedisCacheConstant.ROLE + id));


        if (ObjectUtils.isEmpty(roleCache)) {
            List<SysRole> roles = lambdaQuery().in(SysRole::getId, ids).list();
            Map<String, SysRole> roleMap = roles.stream()
                    .collect(Collectors.toMap((role) -> RedisCacheConstant.ROLE + role.getId(), (role) -> role));
            redisTemplate.opsForValue().multiSet(roleMap);

            return roles;
        }

        List<SysRole> existRoles = roleCache.stream().filter(Objects::nonNull).map(o -> ((SysRole) o)).toList();
        // 说明可以从缓存中获取所有的角色
        if (existRoles.size() == ids.size())
            return existRoles;

        List<Long> existRoleIds = convertList(existRoles, SysRole::getId);
        // 获取缓存中
        List<Long> absentIds = ids.stream().filter(id -> !existRoleIds.contains(id)).toList();
        List<SysRole> absentRoles = lambdaQuery().in(SysRole::getId, absentIds).list();

        Map<String, SysRole> roleMap = absentRoles.stream()
                .collect(Collectors.toMap(role -> RedisCacheConstant.ROLE + role.getId(), r -> r));
        redisTemplate.opsForValue().multiSet(roleMap);
        List<SysRole> roles = new ArrayList<>();
        roles.addAll(existRoles);
        roles.addAll(absentRoles);

        return roles;
    }

    @Override
    public List<SysRole> getRolesByUserIdFromCache(Long userId) {

        // 获得用户拥有的角色编号
        List<Long> roleIds = userRoleService.getRoleIdsByUserIdFromCache(userId);

        if (ObjectUtils.isEmpty(roleIds)) return Collections.emptyList();

        return getRolesFromCacheByRoleIds(roleIds);

    }

    @Override
    public RoleDataScope getDataScopeByUserId(Long userId) {


        Supplier<Long> userDeptIdSupplier = Suppliers.memoize(() -> userMapper.selectById(userId).getDeptId());


        List<SysRole> roles = getRolesByUserIdFromCache(userId);

        // 如果角色为空，则只能查看自己
        RoleDataScope result = new RoleDataScope();
        if (CollUtil.isEmpty(roles)) {
            result.setSelf(true);
            return result;
        }

        // 获得用户的部门编号的缓存，通过 Guava 的 Suppliers 惰性求值，即有且仅有第一次发起 DB 的查询

        // 遍历每个角色，计算
        for (SysRole role : roles) {

            Set<Long> dataScopeDeptIds = Json.parseObject(role.getDataScopeDeptIds(), new TypeReference<>() {
            });

            // 为空时，跳过
            if (role.getDataScope() == null) {
                continue;
            }
            // 情况一，ALL
            if (Objects.equals(role.getDataScope(), DataScopeEnum.ALL.getScope())) {
                result.setAll(true);
                continue;
            }
            // 情况二，DEPT_CUSTOM
            if (Objects.equals(role.getDataScope(), DataScopeEnum.DEPT_CUSTOM.getScope())) {

                result.getDeptIds().addAll(dataScopeDeptIds);
                // 自定义可见部门时，保证可以看到自己所在的部门。否则，一些场景下可能会有问题。
                // 例如说，登录时，基于 t_user 的 username 查询会可能被 dept_id 过滤掉
                result.getDeptIds().add(userDeptIdSupplier.get());
                continue;
            }
            // 情况三，DEPT_ONLY
            if (Objects.equals(role.getDataScope(), DataScopeEnum.DEPT_ONLY.getScope())) {
                if (userDeptIdSupplier.get() != null)
                    result.getDeptIds().add(userDeptIdSupplier.get());
                continue;
            }
            // 情况四，DEPT_DEPT_AND_CHILD  本部门和子部门
            if (Objects.equals(role.getDataScope(), DataScopeEnum.DEPT_AND_CHILD.getScope())) {

                result.getDeptIds().add(userDeptIdSupplier.get());
                List<Long> childDeptIds = deptService.getChildDeptList(userDeptIdSupplier.get()).stream().map(SysDept::getId).toList();
                result.getDeptIds().addAll(childDeptIds);
                continue;
            }

            // 情况五，SELF
            if (Objects.equals(role.getDataScope(), DataScopeEnum.SELF.getScope())) {
                result.setSelf(true);
                continue;
            }


            // 未知情况，error log 即可
            log.error("[getDeptDataPermission][LoginUser({}) role({}) 无法处理]", userId, Json.toJsonString(result));
        }
        return result;
    }


    @Override
    public List<SysRole> getRolesByUserId(Long userId) {

        return roleMapper.selectRolesByUserId(userId);
    }

    @Transactional
    @Override
    public Long createRole(RoleSaveParam param) {

        validateRoleDuplicate(param.getName(), param.getKey(), null);

        SysRole role = new SysRole();
        BeanUtils.copyProperties(param, role);
        role.setStatus(CommonStatusEnum.ENABLE.getStatus());
        role.setDataScope(DataScopeEnum.ALL.getScope()); // 默认可查看所有数据。原因是，可能一些项目不需要项目权限
        roleMapper.insert(role);
        // 新增角色菜单关联关系
        if (ObjectUtils.isNotEmpty(param.getMenuIds())) {
            // TODO 校验菜单存在
            List<SysRoleMenu> insertRoleMenus = param.getMenuIds().stream().map(id -> {
                SysRoleMenu roleMenu = new SysRoleMenu();
                roleMenu.setRoleId(role.getId());
                roleMenu.setMenuId(id);
                return roleMenu;
            }).toList();
            roleMenuMapper.insertBatch(insertRoleMenus);
        }
        return role.getId();
    }

    @Transactional
    @CacheEvict(value = RedisCacheConstant.ROLE, key = "#param.id")
    @Override
    public void updateRole(RoleSaveParam param) {

        validateRoleExist(param.getId());
        validateRoleDuplicate(param.getName(), param.getKey(), param.getId());

        SysRole updateRole = new SysRole();
        BeanUtils.copyProperties(param, updateRole);
        roleMapper.updateById(updateRole);
        roleMenuService.updateRoleMenus(param.getId(), param.getMenuIds());
    }


    @CacheEvict(value = RedisCacheConstant.ROLE, key = "#id")
    @Override
    public void updateRoleStatus(Long id, Integer status) {
        validateRoleExist(id);

        Long userRoleCount = ChainWrappers.lambdaQueryChain(userRoleMapper).eq(SysUserRole::getRoleId, id).count();
        if (userRoleCount > 0)
            throw new ServiceException(ServRespCode.REQUEST_PARAMETER_ERROR.getCode(), "角色已被分配无法禁用");
        lambdaUpdate().eq(SysRole::getId, id).set(SysRole::getStatus, status).update();
    }


    @Transactional(rollbackFor = Exception.class)
    @Caching(evict = {
            // 删除role:id缓存
            @CacheEvict(value = RedisCacheConstant.ROLE, key = "#id"),
            // 删除用户角色缓存
            @CacheEvict(value = RedisCacheConstant.USER_ROLE_IDS, allEntries = true)
    })
    @Override
    public void deleteRole(Long roleId) {

        validateRoleExist(roleId);

        List<SysUserRole> sysUserRoles = ChainWrappers.lambdaQueryChain(userRoleMapper)
                .eq(SysUserRole::getRoleId, roleId).list();

        if (ObjectUtils.isNotEmpty(sysUserRoles)) {
            throw new ServiceException(ServRespCode.REQUEST_PARAMETER_ERROR.getCode(), "角色被分配无法删除");
        }

        removeById(roleId);
        // 删除角色与菜单关联关系
        roleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, roleId));
        // 删除角色与部门关联关系（角色数据权限）
        ChainWrappers.lambdaUpdateChain(roleDeptMapper).eq(SysRoleDept::getRoleId, roleId).remove();
    }


    @Override
    public PageResult<SysRole> getRolePage(RolePageParam param) {

        IPage<SysRole> page = MyBatisUtils.buildPage(param);
        roleMapper.selectRolePage(page, param);
        return new PageResult<>(page.getRecords(), page.getTotal());
    }

    @Override
    public List<SysRole> getSimpleRoles() {

        return lambdaQuery()
                .select(SysRole::getId, SysRole::getName)
                .eq(SysRole::getStatus, CommonStatusEnum.ENABLE.getStatus()).list();
    }

    @Override
    public List<SysRole> getRolesByMenuId(Long menuId) {

        return roleMapper.selectRolesByMenuId(menuId);
    }

    @Transactional
    @Override
    public void updateRoleDataScope(Long roleId, Integer dataScope, Set<Long> dataScopeDeptIds) {
        validateRoleExist(roleId);
        SysRole updateRole = new SysRole();
        updateRole.setId(roleId);
        updateRole.setDataScope(dataScope);
        updateRole.setDataScopeDeptIds(Json.toJsonString(dataScopeDeptIds));
        roleMapper.updateById(updateRole);

        ChainWrappers.lambdaUpdateChain(SysRoleDept.class).eq(SysRoleDept::getRoleId, roleId).remove();

        if (ObjectUtils.isNotEmpty(dataScopeDeptIds)) {
            List<SysRoleDept> sysRoleDepts = dataScopeDeptIds.stream().map(id -> {
                SysRoleDept sysRoleDept = new SysRoleDept();
                sysRoleDept.setRoleId(roleId);
                sysRoleDept.setDeptId(id);
                return sysRoleDept;
            }).toList();
            roleDeptMapper.insertBatch(sysRoleDepts);
        }
    }

    @Transactional
    @Override
    public void deleteRoleBatchByIds(Set<Long> ids) {

        List<Long> existRoleIds = ChainWrappers.lambdaQueryChain(userRoleMapper)
                .in(SysUserRole::getRoleId, ids).list().stream()
                .map(SysUserRole::getRoleId).toList();

        if (ObjectUtils.isNotEmpty(existRoleIds)) {
            throw new ServiceException(ServRespCode.REQUEST_PARAMETER_ERROR.getCode(), String.format("角色%s被分配，无法删除", Json.toJsonString(existRoleIds)));
        }

        removeBatchByIds(ids);
        // 删除角色部门关联关系（数据权限）
        ChainWrappers.lambdaUpdateChain(roleDeptMapper).in(SysRoleDept::getRoleId, ids).remove();
        // 删除角色菜单关联关系
        ChainWrappers.lambdaUpdateChain(roleMenuMapper).in(SysRoleMenu::getRoleId, ids).remove();
    }

    @Override
    public void assignUsers(RoleAssignUsersParam param) {
        validateRoleExist(param.getRoleId());

        List<SysUser> existedUsers = userMapper.selectUserAndRoleIdByUserIds(param.getRoleId(), param.getUserIds());

        List<Long> existedUserIds = existedUsers.stream().map(SysUser::getId).toList();

        Set<Long> notExistedIds = param.getUserIds().stream().filter(id -> !existedUserIds.contains(id)).collect(Collectors.toSet());
        if (ObjectUtils.isNotEmpty(notExistedIds)) {
            throw new ServiceException(ServRespCode.REQUEST_PARAMETER_ERROR.getCode(), String.format("用户%1$s不存在", Json.toJsonString(notExistedIds)));
        }

        // 判断是否有用户已经拥有该角色
        Predicate<SysUser> hasRoleId = (user) -> convertList(user.getRoles(), SysRole::getId).contains(param.getRoleId());
        List<Long> hasRoleUserIds = existedUsers.stream().filter(user -> ObjectUtils.isNotEmpty(user.getRoles()) && hasRoleId.test(user)).map(SysUser::getId).toList();
        if (ObjectUtils.isNotEmpty(hasRoleUserIds)) {
            throw new ServiceException(ServRespCode.REQUEST_PARAMETER_ERROR.getCode(), String.format("用户%1$s已经拥有该角色", Json.toJsonString(hasRoleUserIds)));
        }

        List<SysUserRole> insertUserRoles = existedUserIds.stream().map(id -> {
            SysUserRole sysUserRole = new SysUserRole();
            sysUserRole.setRoleId(param.getRoleId());
            sysUserRole.setUserId(id);
            return sysUserRole;
        }).toList();

        userRoleMapper.insertBatch(insertUserRoles);
    }

    @Override
    public void deleteUserRoleBatch(Long roleId, Set<Long> userIds) {

        validateRoleExist(roleId);

        ChainWrappers.lambdaUpdateChain(userRoleMapper)
                .eq(SysUserRole::getRoleId, roleId)
                .in(SysUserRole::getUserId, userIds)
                .remove();
    }

    void validateRoleExist(Long id) {

        if (id == null || lambdaQuery().eq(SysRole::getId, id).count() == 0)
            throw new ServiceException(ServRespCode.REQUEST_PARAMETER_ERROR.getCode(), "该角色不存在");
    }


    void validateRoleDuplicate(String name, String key, Long id) {
        // 0. 超级管理员，不允许创建
        if (RoleEnum.isSuperAdmin(key)) {
            throw new ServiceException(ServRespCode.REQUEST_PARAMETER_ERROR.getCode(), "无法创建admin角色");
        }
        // 1. 该 name 名字被其它角色所使用
        SysRole role = lambdaQuery().eq(SysRole::getName, name).one();

        if (role != null && !role.getId().equals(id)) {
            throw new ServiceException(ServRespCode.REQUEST_PARAMETER_ERROR.getCode(), "已存在该角色");

        }

        if (!StringUtils.hasText(key)) {
            return;
        }
        // 该 code 编码被其它角色所使用
        role = lambdaQuery().eq(SysRole::getKey, key).one();
        if (role != null && !role.getId().equals(id)) {
            throw new ServiceException(ServRespCode.REQUEST_PARAMETER_ERROR.getCode(), "已存在该角色");
        }
    }
}




