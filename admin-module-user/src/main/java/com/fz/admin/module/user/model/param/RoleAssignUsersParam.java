package com.fz.admin.module.user.model.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Set;

@Schema(description = "分配用户参数")
@Data
public class RoleAssignUsersParam {

    @Schema(description = "角色id", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "角色id不能为空")
    private Long roleId;

    @Schema(description = "用户id集合", requiredMode = Schema.RequiredMode.REQUIRED, example = "[]")
    @NotEmpty(message = "用户id集合不能为空")
    private Set<Long> userIds;
}
