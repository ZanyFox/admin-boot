package com.fz.admin.module.user.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fz.admin.module.user.model.param.UserPageParam;
import com.fz.admin.module.user.model.entity.SysUser;
import com.fz.admin.module.user.model.vo.UserPermMenuInfoVO;
import org.apache.ibatis.annotations.Param;

public interface SysUserMapper extends BaseMapper<SysUser> {


    /**
     * 获取用户角色和用户菜单权限
     * @param id 用户id
     * @return 用户角色和用户菜单
     */
    UserPermMenuInfoVO selectUserPermissionInfoVO(@Param("id") Long id);


    IPage<SysUser> selectUserPage(IPage<SysUser> page, @Param("param") UserPageParam param);


    SysUser selectUserDetail(Long id);

}




