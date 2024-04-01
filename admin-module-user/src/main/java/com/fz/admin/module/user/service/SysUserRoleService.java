package com.fz.admin.module.user.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.fz.admin.module.user.model.entity.SysUserRole;

import java.util.List;

public interface SysUserRoleService extends IService<SysUserRole> {

    /**
     * 根据用户id获取用户角色id列表，并且放入缓存中
     */
    List<Long> getRoleIdsByUserIdFromCache(Long userId);

    /**
     * 删除用户和角色的对应关系
     * @param userId
     */
    void deleteRolesByUserId(Long userId);
}
