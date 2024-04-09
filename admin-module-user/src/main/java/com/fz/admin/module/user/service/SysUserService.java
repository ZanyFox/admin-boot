package com.fz.admin.module.user.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.fz.admin.framework.common.pojo.PageResult;
import com.fz.admin.module.user.model.entity.SysUser;
import com.fz.admin.module.user.model.param.*;
import com.fz.admin.module.user.model.vo.UserPermMenuInfoVO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface SysUserService extends IService<SysUser> {

    /**
     * 获取用户权限包括角色和菜单权限
     * @param id 用户id
     * @return 用户权限信息
     */
    UserPermMenuInfoVO getUserInfoWithPermissions(Long id);

    /**
     * 分页
     * @param param 分页参数
     * @return 分页数据
     */
    PageResult<SysUser> getUserpage(UserPageParam param);

    /**
     * 获取用户相关信息 包括部门岗位职位
     * @param id 用户id
     * @return 用户数据
     */
    SysUser getUserDetail(Long id);

    /**
     * 创建用户
     * @param param 参数
     * @return 用户id
     */
    Long createUser(UserSaveParam param);


    /**
     * 更新用户
     * @param param 用户参数
     */
    void updateUser(UserSaveParam param);

    /**
     * 删除用户
     * @param userId 用户id
     */
    void deleteUser(Long userId);


    /**
     * 更新用户密码
     * @param id 用户id
     * @param password 用户密码
     */
    void updateUserPassword(Long id, String password);


    /**
     * 更新用户状态
     * @param id 用户id
     * @param status 更新状态
     */
    void updateUserStatus(Long id, Integer status);

    /**
     * 获取用户详细信息
     * @param userId 用户id
     * @return 用户对象
     */
    SysUser getUserProfile(Long userId);

    /**
     * 更新用户详细信息
     * @param userId 用户id
     * @param param 参数
     */
    void updateUserProfile(Long userId, UserProfileUpdateParam param);

    /**
     * 更新用户密码
     * @param userId  用户id
     * @param param 更新参数
     */
    void updateUserPassword(Long userId, UserProfileUpdatePasswordParam param);

    /**
     * 更新用户头像
     * @param userId 用户id
     * @param file 头像
     * @return 头像url
     */
    String updateUserAvatar(Long userId, MultipartFile file) throws IOException;

    /**
     * 批量删除用户
     * @param ids 用户id列表
     */
    void deleteUserBatchByIds(List<Long> ids);



    /**
     * 获取未被分配该角色的用户
     *
     * @param roleId 角色id
     * @return 用户列表
     */
    PageResult<SysUser> getUserNotAssignedRolePage(Long roleId, UserAssignRolePageParam param);

    /**
     * 获取已被分配该角色的用户列表
     * @param roleId 角色id
     * @param param 分页参数
     * @return 分页数据
     */
    PageResult<SysUser> getUserAssignedRolePage(Long roleId, UserAssignRolePageParam param);
}
