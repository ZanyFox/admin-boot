package com.fz.admin.module.user.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.fz.admin.framework.common.pojo.PageResult;
import com.fz.admin.module.user.model.entity.SysPost;
import com.fz.admin.module.user.model.param.PostCreateOrUpdateParam;
import com.fz.admin.module.user.model.param.PostPageParam;

import java.util.List;

public interface SysPostService extends IService<SysPost> {

    /**
     * 新增岗位
     *
     * @param param 岗位参数
     * @return 岗位id
     */
    Long createPost(PostCreateOrUpdateParam param);


    /**
     * 更新岗位
     *
     * @param param 岗位参数
     */
    void updatePost(PostCreateOrUpdateParam param);

    /**
     * 删除岗位
     * @param id 岗位id
     */
    void deletePostById(Long id);

    SysPost getPostById(Long id);

    /**
     * 批量删除
     * @param ids  id列表
     */
    void deletePostByIds(List<Long> ids);

    /**
     * 获取岗位的简单列表  用于下拉菜单
     * @return 岗位列表
     */
    List<SysPost> getSimplePostList();

    PageResult<SysPost> getPostPage(PostPageParam param);
}
