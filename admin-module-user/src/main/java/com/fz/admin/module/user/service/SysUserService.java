package com.fz.admin.module.user.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.fz.admin.framework.common.pojo.PageResult;
import com.fz.admin.module.user.model.param.UserPageParam;
import com.fz.admin.module.user.model.entity.SysUser;
import com.fz.admin.module.user.model.param.UserProfileUpdateParam;
import com.fz.admin.module.user.model.param.UserProfileUpdatePasswordParam;
import com.fz.admin.module.user.model.param.UserSaveParam;
import com.fz.admin.module.user.model.vo.UserPermMenuInfoVO;

import java.io.InputStream;

public interface SysUserService extends IService<SysUser> {

    UserPermMenuInfoVO getUserInfoWithPermissions(Long id);

    PageResult<SysUser> page(UserPageParam param);

    SysUser getUserDetail(Long id);

    Long createUser(UserSaveParam param);

    void updateUser(UserSaveParam param);

    void deleteUser(Long userId);

    void updateUserPassword(Long id, String password);

    void updateUserStatus(Long id, Integer status);

    SysUser getUserProfile(Long userId);

    void updateUserProfile(Long userId, UserProfileUpdateParam param);

    void updateUserPassword(Long userId, UserProfileUpdatePasswordParam param);

    String updateUserAvatar(Long userId, InputStream inputStream, String filename);
}
