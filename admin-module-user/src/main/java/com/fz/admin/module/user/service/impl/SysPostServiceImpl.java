package com.fz.admin.module.user.service.impl;


import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fz.admin.framework.common.enums.CommonStatusEnum;
import com.fz.admin.framework.common.enums.ServRespCode;
import com.fz.admin.framework.common.exception.ServiceException;
import com.fz.admin.framework.common.pojo.PageResult;
import com.fz.admin.framework.mybatis.util.MyBatisUtils;
import com.fz.admin.module.user.mapper.SysPostMapper;
import com.fz.admin.module.user.mapper.SysUserMapper;
import com.fz.admin.module.user.mapper.SysUserPostMapper;
import com.fz.admin.module.user.model.entity.SysPost;
import com.fz.admin.module.user.model.entity.SysUserPost;
import com.fz.admin.module.user.model.param.PostSaveParam;
import com.fz.admin.module.user.model.param.PostPageParam;
import com.fz.admin.module.user.service.SysPostService;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class SysPostServiceImpl extends ServiceImpl<SysPostMapper, SysPost> implements SysPostService {


    private SysPostMapper postMapper;

    private SysUserMapper userMapper;

    private SysUserPostMapper userPostMapper;

    @Override
    public Long createPost(PostSaveParam param) {

        param.setId(null);
        validatePostNameUnique(null, param.getName());
        validatePostCodeUnique(null, param.getCode());

        SysPost post = new SysPost();
        BeanUtils.copyProperties(param, post);
        postMapper.insert(post);
        return post.getId();
    }

    @Override
    public void updatePost(PostSaveParam param) {

        validatePostExists(param.getId());
        validatePostNameUnique(param.getId(), param.getName());
        validatePostCodeUnique(param.getId(), param.getName());

        SysPost post = new SysPost();
        BeanUtils.copyProperties(param, post);
    }

    @Override
    public void deletePostById(Long id) {
        validatePostExists(id);

        List<SysUserPost> sysUserPosts = userPostMapper.selectList(new LambdaQueryWrapper<SysUserPost>().eq(SysUserPost::getPostId, id));
        if (ObjectUtils.isNotEmpty(sysUserPosts)) {
            throw new ServiceException(ServRespCode.REQUEST_PARAMETER_ERROR.getCode(), "该岗位存在员工，无法删除");

        }
        postMapper.deleteById(id);
    }

    @Override
    public SysPost getPostById(Long id) {
        return postMapper.selectById(id);
    }

    @Override
    public void deletePostByIds(List<Long> ids) {

        List<SysUserPost> sysUserPosts = userPostMapper
                .selectList(Wrappers.<SysUserPost>lambdaQuery().in(SysUserPost::getPostId, ids));

        if (ObjectUtils.isNotEmpty(sysUserPosts))
            throw new ServiceException(ServRespCode.REQUEST_PARAMETER_ERROR.getCode(), "岗位存在员工，无法删除");

        postMapper.deleteBatchIds(ids);
    }

    @Override
    public List<SysPost> getSimplePostList() {

        return lambdaQuery()
                .select(SysPost::getId, SysPost::getName, SysPost::getOrder)
                .eq(SysPost::getStatus, CommonStatusEnum.ENABLE.getStatus())
                .list();
    }

    @Override
    public PageResult<SysPost> getPostPage(PostPageParam param) {

        IPage<SysPost> page = MyBatisUtils.buildPage(param);
        lambdaQuery()
                .eq(ObjectUtils.isNotEmpty(param.getStatus()), SysPost::getStatus, param.getStatus())
                .like(StringUtils.isNotBlank(param.getName()), SysPost::getName, param.getName())
                .like(StringUtils.isNotBlank(param.getCode()), SysPost::getCode, param.getCode())
                .page(page);

        return new PageResult<>(page.getRecords(), page.getTotal());
    }

    @Override
    public void validatePostList(Collection<Long> ids) {

        if (CollUtil.isEmpty(ids)) {
            return;
        }
        // 获得岗位信息
        Map<Long, SysPost> postMap = postMapper.selectBatchIds(ids).stream()
                .collect(Collectors.toMap(SysPost::getId, post -> post));

        // 校验
        ids.forEach(id -> {
            SysPost post = postMap.get(id);
            if (post == null) {
                throw new ServiceException(ServRespCode.REQUEST_PARAMETER_ERROR.getCode(), "岗位不存在");

            }
            if (!CommonStatusEnum.ENABLE.getStatus().equals(post.getStatus())) {
                throw new ServiceException(ServRespCode.REQUEST_PARAMETER_ERROR.getCode(), "岗位未启用");
            }
        });
    }


    /**
     * 校验岗位是否存在
     *
     * @param id 岗位id
     */
    private void validatePostExists(Long id) {
        if (id == null) {
            return;
        }
        if (postMapper.selectById(id) == null) {
            throw new ServiceException(ServRespCode.REQUEST_PARAMETER_ERROR.getCode(), "岗位不存在");
        }
    }

    /**
     * 校验岗位名称是否唯一
     *
     * @param id   岗位id
     * @param name 岗位名称
     */
    private void validatePostNameUnique(Long id, String name) {

        SysPost post = lambdaQuery().eq(SysPost::getName, name).one();

        if (post == null) {
            return;
        }
        // 如果 id 为空，表示新增
        if (id == null) {
            throw new ServiceException(ServRespCode.REQUEST_PARAMETER_ERROR.getCode(), "岗位名重复");
        }
        // id 不为空，表示更新，查到的名字可能是它本身
        if (!post.getId().equals(id)) {
            throw new ServiceException(ServRespCode.REQUEST_PARAMETER_ERROR.getCode(), "岗位名重复");
        }
    }

    /**
     * 校验岗位标识是否唯一
     *
     * @param id   岗位id
     * @param code 岗位标识
     */
    private void validatePostCodeUnique(Long id, String code) {
        SysPost post = lambdaQuery().eq(SysPost::getCode, code).one();

        if (post == null) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同 id 的岗位
        if (id == null) {
            throw new ServiceException(ServRespCode.REQUEST_PARAMETER_ERROR.getCode(), "岗位标识重复");
        }
        if (!post.getId().equals(id)) {
            throw new ServiceException(ServRespCode.REQUEST_PARAMETER_ERROR.getCode(), "岗位标识重复");
        }
    }

}




