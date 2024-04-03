package com.fz.admin.module.user.controller.admin;

import com.fz.admin.framework.common.enums.ServRespCode;
import com.fz.admin.framework.common.exception.ServiceException;
import com.fz.admin.framework.common.pojo.ServRespEntity;
import com.fz.admin.framework.datapermission.annotation.DataPermission;
import com.fz.admin.module.user.model.entity.SysUser;
import com.fz.admin.module.user.model.param.UserProfileUpdatePasswordParam;
import com.fz.admin.module.user.service.SysUserService;
import com.fz.admin.module.user.model.param.UserProfileUpdateParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static com.fz.admin.framework.common.pojo.ServRespEntity.success;
import static com.fz.admin.framework.security.util.SecurityContextUtils.getLoginUserId;

@Tag(name = "用户个人中心")
@RestController
@RequestMapping("/system/user/profile")
@AllArgsConstructor
@Validated
@Slf4j
public class UserProfileController {


    private SysUserService userService;


    @GetMapping("/get")
    @Operation(summary = "获得登录用户信息")
    @DataPermission(enable = false) // 关闭数据权限，避免只查看自己时，查询不到部门。
    public ServRespEntity<SysUser> getUserProfile() {
        return success(userService.getUserProfile(getLoginUserId()));
    }


    @PutMapping("/update")
    @Operation(summary = "修改用户个人信息")
    public ServRespEntity<Boolean> updateUserProfile(@Validated @RequestBody UserProfileUpdateParam param) {
        userService.updateUserProfile(getLoginUserId(), param);
        return success(true);
    }

    @PutMapping("/update-password")
    @Operation(summary = "修改用户个人密码")
    public ServRespEntity<Boolean> updateUserProfilePassword(@Validated @RequestBody UserProfileUpdatePasswordParam param) {
        userService.updateUserPassword(getLoginUserId(), param);
        return success(true);
    }


    @RequestMapping(value = "/update-avatar",
            method = {RequestMethod.POST, RequestMethod.PUT}) // 解决 uni-app 不支持 Put 上传文件的问题
    @Operation(summary = "上传用户个人头像")
    public ServRespEntity<String> updateUserAvatar(@RequestPart("avatarFile") MultipartFile file) throws Exception {

        if (file.isEmpty()) {
            throw new ServiceException(ServRespCode.REQUEST_PARAMETER_ERROR);
        }
        String avatar = userService.updateUserAvatar(getLoginUserId(), file);
        return success(avatar);
    }


}
