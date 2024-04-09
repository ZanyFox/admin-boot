package com.fz.admin.module.user.model.param;

import cn.hutool.core.lang.RegexPool;
import cn.hutool.core.util.ObjectUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fz.admin.framework.common.enums.CommonStatusEnum;
import com.fz.admin.framework.web.validation.group.CreateOrUpdateGroup;
import com.fz.admin.framework.web.validation.validator.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.util.Set;

@Schema(description = "用户创建/修改参数")
@Data
public class UserSaveParam {

    @Schema(description = "用户编号", example = "1024")
    @NotNull(groups = {CreateOrUpdateGroup.UpdateGroup.class}, message = "更新时必须有用户id")
    private Long id;

    @Schema(description = "用户账号", requiredMode = Schema.RequiredMode.REQUIRED, example = "admin")
    @NotBlank(message = "用户账号不能为空", groups = {CreateOrUpdateGroup.CreateGroup.class})
    @Pattern(regexp = "^[a-zA-Z0-9]{2,30}$", message = "用户账号由 数字、字母 组成", groups = {CreateOrUpdateGroup.CreateGroup.class})
    @Size(min = 2, max = 30, message = "用户账号长度为 4-30 个字符", groups = {CreateOrUpdateGroup.CreateGroup.class})
    private String username;

    @Schema(description = "用户昵称", requiredMode = Schema.RequiredMode.REQUIRED, example = "fantas")
    @Size(max = 30, message = "用户昵称长度不能超过30个字符")
    private String nickname;

    @Schema(description = "备注", example = "我是一个用户")
    private String remark;

    @Schema(description = "部门ID", example = "我是一个用户")
    private Long deptId;

    @Schema(description = "岗位id集合", example = "[]")
    private Set<Long> postIds;

    @Schema(description = "角色id集合", example = "[]")
    private Set<Long> roleIds;

    @Schema(description = "用户邮箱", example = "yudao@iocoder.cn")
    @Email(message = "邮箱格式不正确")
    @Size(max = 50, message = "邮箱长度不能超过 50 个字符")
    private String email;

    @Schema(description = "手机号码", example = "15601691300")
    @Pattern(regexp = RegexPool.MOBILE, message = "手机号格式不正确")
    private String mobile;

    @Schema(description = "用户性别，参见 SexEnum 枚举类", example = "1")
    private Integer gender;

    @Schema(description = "用户头像")
    private String avatar;

    @Schema(description = "用户状态")
    @InEnum(value = CommonStatusEnum.class)
    private Integer status;

    // ========== 仅【创建】时，需要传递的字段 ==========

    @Schema(description = "密码", requiredMode = Schema.RequiredMode.REQUIRED, example = "123456")
    @Length(min = 4, max = 16, message = "密码长度为 4-16 位")
    @NotBlank(groups = {CreateOrUpdateGroup.CreateGroup.class}, message = "创建时必须设置密码")
    private String password;

    @AssertTrue(message = "密码不能为空")
    @JsonIgnore
    public boolean isPasswordValid() {
        return id != null // 修改时，不需要传递
                || (ObjectUtil.isAllNotEmpty(password)); // 新增时，必须都传递 password
    }

}
