package com.fz.admin.module.user.model.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Collections;
import java.util.Set;

@Schema(description = "角色授权参数")
@Data
public class RoleAssignMenuParam {

    @Schema(description = "角色id", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "角色编号不能为空")
    private Long roleId;

    @Schema(description = "菜单id列表", example = "1,3,5")
    private Set<Long> menuIds = Collections.emptySet(); // 兜底

}
