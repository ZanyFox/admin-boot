package com.fz.admin.module.user.model.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Collections;
import java.util.Set;

@Schema(description = "授予用户角色参数")
@Data
public class UserAssignRoleParam {

    @Schema(description = "用户id", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "用户id不能为空")
    private Long userId;

    @Schema(description = "角色id列表", example = "[1,3,5]")
    private Set<Long> roleIds = Collections.emptySet();

}
