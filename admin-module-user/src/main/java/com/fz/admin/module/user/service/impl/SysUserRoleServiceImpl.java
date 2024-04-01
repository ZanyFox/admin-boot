package com.fz.admin.module.user.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fz.admin.framework.redis.constant.RedisCacheConstant;
import com.fz.admin.module.user.mapper.SysUserRoleMapper;
import com.fz.admin.module.user.model.entity.SysUserRole;
import com.fz.admin.module.user.service.SysUserRoleService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleMapper, SysUserRole> implements SysUserRoleService {

    @Cacheable(value = RedisCacheConstant.USER_ROLE_IDS, key = "#userId")
    @Override
    public List<Long> getRoleIdsByUserIdFromCache(Long userId) {

        return lambdaQuery()
                .eq(SysUserRole::getUserId, userId)
                .list().stream().map(SysUserRole::getRoleId).distinct().toList();
    }

    /**
     *
     * @param userId
     */
    @CacheEvict(cacheNames = RedisCacheConstant.USER_ROLE_IDS, key = "#userId")
    @Override
    public void deleteRolesByUserId(Long userId) {
        lambdaUpdate().eq(SysUserRole::getUserId, userId).remove();
    }
}




