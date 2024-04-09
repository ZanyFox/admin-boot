package com.fz.admin.module.user.model.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Set;

@Schema(description = "撤销用户角色参数")
@Data
public class RoleRevokeParam {

    @Schema(description = "角色id", example = "0")
    @NotNull
    private Long roleId;

    // @Schema(description = "用户id", example = "0")
    // @Range(message = "用户id不合法")
    // private Long userId;

    @Schema(description = "用户id列表", example = "[]")
    private Set<Long> userIds;
}
