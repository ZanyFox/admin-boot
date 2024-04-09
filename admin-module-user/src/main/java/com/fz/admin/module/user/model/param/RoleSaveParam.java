package com.fz.admin.module.user.model.param;

import com.fz.admin.framework.common.enums.CommonStatusEnum;
import com.fz.admin.framework.web.validation.group.CreateOrUpdateGroup;
import com.fz.admin.framework.web.validation.validator.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Schema(description = "角色创建/更新参数")
@Data
public class RoleSaveParam {

    /**
     * 更新时需要
     */
    @Schema(description = "角色编号", example = "0")
    @NotNull(message = "角色id不能为空", groups = CreateOrUpdateGroup.UpdateGroup.class)
    private Long id;

    @Schema(description = "角色名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "")
    @NotBlank(message = "角色名称不能为空")
    @Size(max = 30, message = "角色名称长度不能超过30个字符")
    private String name;

    @Schema(description = "角色key", requiredMode = Schema.RequiredMode.REQUIRED, example = "")
    @NotBlank(message = "角色key不能为空")
    @Size(max = 100, message = "角色key长度不能超过100个字符")
    private String key;

    @Schema(description = "显示顺序不能为空", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    @NotNull(message = "显示顺序不能为空")
    private Integer order;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    @InEnum(CommonStatusEnum.class)
    private Integer status;

    @Schema(description = "备注", example = "")
    private String remark;

    @Schema(description = "菜单id集合", example = "[]")
    private Set<Long> menuIds;

}
