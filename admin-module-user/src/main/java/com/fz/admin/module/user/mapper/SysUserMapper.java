package com.fz.admin.module.user.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fz.admin.module.user.model.entity.SysUser;
import com.fz.admin.module.user.model.param.UserAssignRolePageParam;
import com.fz.admin.module.user.model.param.UserPageParam;
import com.fz.admin.module.user.model.vo.UserPermMenuInfoVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

public interface SysUserMapper extends BaseMapper<SysUser> {


    /**
     * 获取用户角色和用户菜单权限
     *
     * @param id 用户id
     * @return 用户角色和用户菜单
     */
    UserPermMenuInfoVO selectUserPermissionInfoVO(@Param("id") Long id);


    /**
     * 查询用户分页数据
     *
     * @param page  分页
     * @param param 分页参数
     * @return 分页结果
     */
    IPage<SysUser> selectUserPage(IPage<SysUser> page, @Param("param") UserPageParam param);

    /**
     * 查询用户详情信息，包括用户信息、用户所在部门信息、用户岗位信息、用户角色信息
     *
     * @param id 用户id
     * @return 用户详情数据
     */
    SysUser selectUserDetail(Long id);

    /**
     * 通过角色id，查询用户
     *
     * @param roleId 角色id
     * @return 用户列表
     */
    List<SysUser> selectUsersByRoleId(Long roleId);


    /**
     * 查询没有该角色的用户分页数据
     *
     * @param page   分页
     * @param roleId 角色id
     * @param param  分页参数
     * @return 分页结果
     */
    IPage<SysUser> selectNotAssignedRoleUserPage(IPage<SysUser> page, @Param("roleId") Long roleId, @Param("param") UserAssignRolePageParam param);

    /**
     * 查询拥有该角色的用户分页数据
     *
     * @param page   分页
     * @param roleId 角色id
     * @param param  分页参数
     * @return 分页结果
     */
    IPage<SysUser> selectUserPageByRoleId(IPage<SysUser> page, @Param("roleId") Long roleId, @Param("param") UserAssignRolePageParam param);

    /**
     * 查询拥有角色的用户id，并且在指定用户id集合中，用于批量为角色分配用户时判断用户是否存在，且是否拥有该角色
     * @param roleId 角色id
     * @param userIds 用户id列表
     * @return 用户列表，只包含用户id和角色id
     */
    List<SysUser> selectUserAndRoleIdByUserIds(@Param("roleId") Long roleId, @Param("userIds") Set<Long> userIds);
}




