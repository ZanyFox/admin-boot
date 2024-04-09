package com.fz.admin.module.user.service.impl;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.fz.admin.framework.common.enums.DeletedEnum;
import com.fz.admin.framework.common.enums.RoleEnum;
import com.fz.admin.framework.common.enums.ServRespCode;
import com.fz.admin.framework.common.exception.ServiceException;
import com.fz.admin.framework.common.pojo.PageResult;
import com.fz.admin.framework.common.util.CollectionConverter;
import com.fz.admin.framework.common.util.Json;
import com.fz.admin.framework.datapermission.util.DataPermissionUtils;
import com.fz.admin.framework.file.FileService;
import com.fz.admin.framework.mybatis.util.MyBatisUtils;
import com.fz.admin.module.user.mapper.SysUserMapper;
import com.fz.admin.module.user.mapper.SysUserPostMapper;
import com.fz.admin.module.user.model.entity.SysUser;
import com.fz.admin.module.user.model.entity.SysUserPost;
import com.fz.admin.module.user.model.param.*;
import com.fz.admin.module.user.model.vo.UserPermMenuInfoVO;
import com.fz.admin.module.user.service.*;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.fz.admin.framework.common.enums.ServRespCode.REQUEST_PARAMETER_ERROR;

@Service
@AllArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    private SysUserMapper userMapper;

    private PasswordEncoder passwordEncoder;

    private SysUserPostMapper userPostMapper;

    private SysUserRoleService userRoleService;

    private FileService fileService;

    private SysDeptService deptService;

    private SysPostService postService;

    private PermissionService permissionService;

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
    public PageResult<SysUser> getUserpage(UserPageParam param) {

        IPage<SysUser> page = MyBatisUtils.buildPage(param);
        userMapper.selectUserPage(page, param);
        return new PageResult<>(page.getRecords(), page.getTotal());
    }


    @Override
    public SysUser getUserDetail(Long id) {

        SysUser user = userMapper.selectUserDetail(id);

        if (user == null || DeletedEnum.isDeleted(user.getDeleted())) {
            throw new ServiceException(REQUEST_PARAMETER_ERROR.getCode(), "该用户不存在");
        }
        return user;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long createUser(UserSaveParam param) {

        DataPermissionUtils.executeIgnore(() -> {
            // 校验用户名唯一
            validateUsernameUnique(null, param.getUsername());
            // 校验手机号唯一
            validateMobileUnique(null, param.getMobile());
            // 校验邮箱唯一
            validateEmailUnique(null, param.getEmail());
            // 校验部门处于开启状态
            deptService.validateDept(param.getDeptId());
            // 校验岗位处于开启状态
            postService.validatePostList(param.getPostIds());
        });


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

        if (ObjectUtils.isNotEmpty(param.getRoleIds()))
            permissionService.updateUserRole(user.getId(), param.getRoleIds(), false);

        return user.getId();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateUser(UserSaveParam param) {

        param.setPassword(null);

        // 关闭数据权限，避免因为没有数据权限，查询不到数据，进而导致唯一校验不正确

        DataPermissionUtils.executeIgnore(() -> {
            // 校验用户存在
            validateUserExists(param.getId());
            // 校验用户名唯一
            validateUsernameUnique(param.getId(), param.getUsername());
            // 校验手机号唯一
            validateMobileUnique(param.getId(), param.getMobile());
            // 校验邮箱唯一
            validateEmailUnique(param.getId(), param.getEmail());
            // 校验部门处于开启状态
            deptService.validateDept(param.getDeptId());
            // 校验岗位处于开启状态
            postService.validatePostList(param.getPostIds());
        });


        SysUser user = new SysUser();
        BeanUtils.copyProperties(param, user);

        if (param.getPostIds() != null) {
            String postIdsStr = Json.toJsonString(param.getPostIds());
            user.setPostIds(postIdsStr);
        }
        userMapper.updateById(user);
        // 更新用户岗位
        updateUserPost(param.getPostIds(), param.getId());
        // 更新用户角色
        permissionService.updateUserRole(param.getId(), param.getRoleIds(), false);
    }

    /**
     * 更新用户岗位
     *
     * @param postIds 岗位id列表
     * @param userId  用户id
     */
    private void updateUserPost(Set<Long> postIds, Long userId) {

        if (postIds == null) return;

        // 获取用户现有岗位
        List<SysUserPost> existUserPosts = userPostMapper
                .selectList(Wrappers.<SysUserPost>lambdaQuery()
                        .eq(SysUserPost::getUserId, userId));

        // 获取用户被删除的岗位
        List<Long> deletePostIds = existUserPosts.stream()
                .filter(userPost -> !postIds.contains(userPost.getPostId()))
                .map(SysUserPost::getId)
                .toList();

        List<Long> existPostIds = CollectionConverter.convertList(existUserPosts, SysUserPost::getPostId);

        // 获取用户新增的岗位
        List<SysUserPost> insertUserPosts = postIds.stream().filter(id -> !existPostIds.contains(id))
                .map(id -> {
                    SysUserPost userPost = new SysUserPost();
                    userPost.setUserId(userId);
                    userPost.setPostId(id);
                    return userPost;
                })
                .toList();

        // 删除岗位
        if (ObjectUtils.isNotEmpty(deletePostIds))
            userPostMapper.deleteBatchIds(deletePostIds);

        // 新增岗位
        if (ObjectUtils.isNotEmpty(insertUserPosts))
            Db.saveBatch(insertUserPosts);
    }


    @Transactional
    @Override
    public void deleteUser(Long userId) {

        validateUserExists(userId);
        userMapper.deleteById(userId);
        userPostMapper.delete(Wrappers.<SysUserPost>lambdaUpdate()
                .eq(SysUserPost::getUserId, userId));
        userRoleService.deleteRolesByUserId(userId);
    }

    @Override
    public void updateUserPassword(Long id, String password) {

        validateUserExists(id);
        SysUser user = new SysUser();
        user.setId(id);
        user.setPassword(passwordEncoder.encode(password));
        userMapper.updateById(user);
    }

    @Override
    public void updateUserStatus(Long id, Integer status) {
        validateUserExists(id);
        lambdaUpdate().eq(SysUser::getId, id).set(SysUser::getStatus, status).update();
    }

    @Override
    public SysUser getUserProfile(Long userId) {

        return userMapper.selectUserDetail(userId);
    }

    @Override
    public void updateUserProfile(Long userId, UserProfileUpdateParam param) {

        validateUserExists(userId);
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
    public String updateUserAvatar(Long userId, MultipartFile file) throws IOException {

        validateUserExists(userId);
        String avatarUrl = fileService.uploadFile(file.getInputStream(), file.getOriginalFilename());
        lambdaUpdate().eq(SysUser::getId, userId).set(SysUser::getAvatar, avatarUrl).update();
        return avatarUrl;
    }

    @Transactional
    @Override
    public void deleteUserBatchByIds(List<Long> ids) {

        lambdaUpdate().in(SysUser::getId, ids).remove();
        userPostMapper.delete(Wrappers.<SysUserPost>lambdaQuery().in(SysUserPost::getUserId, ids));
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

    private void validateUserExists(Long userId) {

        SysUser sysUser = getById(userId);
        if (sysUser == null)
            throw new ServiceException(ServRespCode.REQUEST_PARAMETER_ERROR.getCode(), "用户不存在");

    }

    @Override
    public PageResult<SysUser> getUserNotAssignedRolePage(Long roleId, UserAssignRolePageParam param) {

        IPage<SysUser> page = MyBatisUtils.buildPage(param);
        userMapper.selectNotAssignedRoleUserPage(page, roleId, param);
        return new PageResult<>(page.getRecords(), page.getTotal());
    }

    @Override
    public PageResult<SysUser> getUserAssignedRolePage(Long roleId, UserAssignRolePageParam param) {
        IPage<SysUser> page = MyBatisUtils.buildPage(param);
        userMapper.selectUserPageByRoleId(page, roleId, param);
        return new PageResult<>(page.getRecords(), page.getTotal());
    }


    private void validateEmailUnique(Long id, String email) {
        if (StringUtils.isBlank(email)) {
            return;
        }
        SysUser user = userMapper.selectOne(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getEmail, email));
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

    private void validateUsernameUnique(Long id, String username) {
        if (id == null && StringUtils.isBlank(username)) {
            throw new ServiceException(ServRespCode.REQUEST_PARAMETER_ERROR.getCode(), "用户名不能为空");
        }
        SysUser user = lambdaQuery().eq(SysUser::getUsername, username).one();
        if (user == null) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同 id 的用户
        if (id == null) {
            throw new ServiceException(ServRespCode.REQUEST_PARAMETER_ERROR.getCode(), "用户名重复");

        }
        if (!user.getId().equals(id)) {
            throw new ServiceException(ServRespCode.REQUEST_PARAMETER_ERROR.getCode(), "用户名重复");
        }
    }

    private void validateMobileUnique(Long id, String mobile) {
        if (id == null && StringUtils.isBlank(mobile)) {
            throw new ServiceException(ServRespCode.REQUEST_PARAMETER_ERROR.getCode(), "手机号不能为空");
        }
        SysUser user = lambdaQuery().eq(SysUser::getMobile, mobile).one();
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




