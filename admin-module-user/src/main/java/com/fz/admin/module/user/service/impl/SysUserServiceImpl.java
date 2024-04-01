package com.fz.admin.module.user.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.fz.admin.framework.common.enums.RoleEnum;
import com.fz.admin.framework.common.enums.ServRespCode;
import com.fz.admin.framework.common.exception.ServiceException;
import com.fz.admin.framework.common.pojo.PageResult;
import com.fz.admin.framework.common.util.CollectionConverter;
import com.fz.admin.framework.common.util.Json;
import com.fz.admin.framework.file.FileService;
import com.fz.admin.framework.mybatis.util.MyBatisUtils;
import com.fz.admin.module.user.model.param.UserPageParam;
import com.fz.admin.module.user.mapper.SysUserMapper;
import com.fz.admin.module.user.mapper.SysUserPostMapper;
import com.fz.admin.module.user.model.entity.SysUser;
import com.fz.admin.module.user.model.entity.SysUserPost;
import com.fz.admin.module.user.model.param.UserProfileUpdateParam;
import com.fz.admin.module.user.model.param.UserProfileUpdatePasswordParam;
import com.fz.admin.module.user.model.param.UserSaveParam;
import com.fz.admin.module.user.model.vo.UserPermMenuInfoVO;
import com.fz.admin.module.user.service.SysUserRoleService;
import com.fz.admin.module.user.service.SysUserService;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    private SysUserMapper userMapper;

    private PasswordEncoder passwordEncoder;

    private SysUserPostMapper userPostMapper;

    private SysUserRoleService userRoleService;

    private FileService fileService;


    @Override
    public UserPermMenuInfoVO getUserInfoWithPermissions(Long id) {

        UserPermMenuInfoVO userPermMenuInfoVO = userMapper.selectUserPermissionInfoVO(id);
        if (userPermMenuInfoVO == null) return null;

        List<String> perms = userPermMenuInfoVO.getPermissions().stream().distinct()
                .collect(Collectors.toCollection((Supplier<List<String>>) ArrayList::new));
        userPermMenuInfoVO.setPermissions(perms);

        if (userPermMenuInfoVO.getRoles().contains(RoleEnum.SUPER_ADMIN.getKey())) {
            userPermMenuInfoVO.getPermissions().clear();
            userPermMenuInfoVO.getPermissions().add("*:*:*");
        }
        return userPermMenuInfoVO;
    }

    @Override
    public PageResult<SysUser> page(UserPageParam param) {

        IPage<SysUser> page = MyBatisUtils.buildPage(param);
        userMapper.selectUserPage(page, param);
        return new PageResult<>(page.getRecords(), page.getTotal());
    }


    @Override
    public SysUser getUserDetail(Long id) {

        return userMapper.selectUserDetail(id);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long createUser(UserSaveParam param) {

        // TODO 唯一性校验
        SysUser user = new SysUser();
        BeanUtils.copyProperties(param, user);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        if (ObjectUtils.isNotEmpty(param.getPostIds())) {
            String postIds = Json.toJsonString(param.getPostIds());
            user.setPostIds(postIds);
        }
        userMapper.insert(user);

        if (ObjectUtils.isNotEmpty(param.getPostIds())) {
            List<SysUserPost> sysUserPosts = CollectionConverter.convertList(param.getPostIds(), postId -> {
                SysUserPost sysUserPost = new SysUserPost();
                sysUserPost.setUserId(user.getId());
                sysUserPost.setPostId(postId);
                return sysUserPost;
            });
            userPostMapper.insertBatch(sysUserPosts);
        }

        return user.getId();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateUser(UserSaveParam param) {

        param.setPassword(null);
        // TODO 校验唯一性

        SysUser user = new SysUser();
        BeanUtils.copyProperties(param, user);

        if (param.getPostIds() != null) {
            String postIdsStr = Json.toJsonString(param.getPostIds());
            user.setPostIds(postIdsStr);
        }
        userMapper.updateById(user);
        updateUserPost(param.getPostIds(), param.getId());
    }


    private void updateUserPost(Set<Long> postIds, Long userId) {

        if (postIds == null) return;

        List<SysUserPost> existUserPosts = userPostMapper
                .selectList(Wrappers.<SysUserPost>lambdaQuery()
                        .eq(SysUserPost::getUserId, userId));

        List<Long> deletePostIds = existUserPosts.stream()
                .filter(userPost -> !postIds.contains(userPost.getPostId()))
                .map(SysUserPost::getId)
                .toList();

        List<Long> existPostIds = CollectionConverter.convertList(existUserPosts, SysUserPost::getPostId);

        List<SysUserPost> insertUserPosts = postIds.stream().filter(id -> !existPostIds.contains(id))
                .map(id -> {
                    SysUserPost userPost = new SysUserPost();
                    userPost.setUserId(userId);
                    userPost.setPostId(id);
                    return userPost;
                })
                .toList();

        if (ObjectUtils.isNotEmpty(deletePostIds))
            userPostMapper.deleteBatchIds(deletePostIds);

        if (ObjectUtils.isNotEmpty(insertUserPosts))
            Db.saveBatch(insertUserPosts);
    }

    @Transactional
    @Override
    public void deleteUser(Long userId) {

        validateUserExist(userId);
        userMapper.deleteById(userId);
        userPostMapper.delete(Wrappers.<SysUserPost>lambdaUpdate()
                .eq(SysUserPost::getUserId, userId));
        userRoleService.deleteRolesByUserId(userId);


    }

    @Override
    public void updateUserPassword(Long id, String password) {

        validateUserExist(id);
        SysUser user = new SysUser();
        user.setId(id);
        user.setPassword(passwordEncoder.encode(password));
        userMapper.updateById(user);
    }

    @Override
    public void updateUserStatus(Long id, Integer status) {
        validateUserExist(id);
        userMapper.update(lambdaUpdate()
                .eq(SysUser::getId, id)
                .set(SysUser::getStatus, status));
    }

    @Override
    public SysUser getUserProfile(Long userId) {

        return userMapper.selectUserDetail(userId);
    }

    @Override
    public void updateUserProfile(Long userId, UserProfileUpdateParam param) {

        validateUserExist(userId);
        validateEmailUnique(userId, param.getEmail());
        validateMobileUnique(userId, param.getMobile());
        SysUser user = new SysUser();
        BeanUtils.copyProperties(param, user);
        user.setId(userId);
        updateById(user);
    }

    @Override
    public void updateUserPassword(Long userId, UserProfileUpdatePasswordParam param) {

        validateUserOldPassword(userId, param.getOldPassword());
        lambdaUpdate().eq(SysUser::getId, userId)
                .set(SysUser::getPassword, passwordEncoder.encode(param.getNewPassword()))
                .update();
    }

    @Override
    public String updateUserAvatar(Long userId, InputStream inputStream, String filename) {

        validateUserExist(userId);
        String avatarUrl = fileService.uploadFile(inputStream, filename);
        lambdaUpdate().eq(SysUser::getId, userId).set(SysUser::getAvatar, avatarUrl).update();
        return avatarUrl;
    }

    private void validateUserOldPassword(Long userId, String password) {
        if (userId == null)
            throw new ServiceException(ServRespCode.USER_NOT_EXIST);

        SysUser user = getById(userId);
        if (user == null)
            throw new ServiceException(ServRespCode.USER_NOT_EXIST);

        if (StringUtils.isBlank(password))
            throw new ServiceException(ServRespCode.USER_NOT_EXIST);

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new ServiceException(ServRespCode.USER_NOT_EXIST);
        }

    }

    private void validateUserExist(Long userId) {
        if (userId == null) return;

        if (getById(userId) == null)
            throw new ServiceException(ServRespCode.REQUEST_PARAMETER_ERROR.getCode(), "用户不存在");
    }


    private void validateEmailUnique(Long id, String email) {
        if (StringUtils.isBlank(email)) {
            return;
        }
        SysUser user = userMapper.selectOne(Wrappers.<SysUser>lambdaQuery()
                .eq(SysUser::getEmail, email));
        if (user == null) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同 id 的用户
        if (id == null) {
            throw new ServiceException(ServRespCode.EMAIL_EXISTED);
        }
        if (!Objects.equals(id, user.getId())) {
            throw new ServiceException(ServRespCode.EMAIL_EXISTED);
        }
    }


    private void validateMobileUnique(Long id, String mobile) {
        if (StrUtil.isBlank(mobile)) {
            return;
        }
        SysUser user = userMapper.selectOne(lambdaQuery().eq(SysUser::getMobile, mobile));
        if (user == null) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同 id 的用户
        if (id == null) {
            throw new ServiceException(ServRespCode.MOBILE_EXISTED);
        }
        if (!user.getId().equals(id)) {
            throw new ServiceException(ServRespCode.MOBILE_EXISTED);
        }
    }

}




