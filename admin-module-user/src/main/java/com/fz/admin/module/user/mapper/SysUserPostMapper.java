package com.fz.admin.module.user.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fz.admin.module.user.model.entity.SysUserPost;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysUserPostMapper extends BaseMapper<SysUserPost> {

    /**
     * Parameter 'userPosts' not found. Available parameters are [arg0, collection, list]
     * 批量插入
     */
    Boolean insertBatch(@Param("userPosts") List<SysUserPost> userPosts);


}




