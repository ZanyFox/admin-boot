package com.fz.admin.module.user.model.param;


import com.fz.admin.framework.common.enums.DataScopeEnum;
import com.fz.admin.framework.web.validation.validator.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Collections;
import java.util.Set;

@Schema(description = "授予角色数据权限参数")
@Data
public class RoleUpdateDataScopeParam {

    @Schema(description = "角色id", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "角色编号不能为空")
    private Long roleId;

    @Schema(description = "数据范围，参见 DataScopeEnum 枚举类", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "数据范围不能为空")
    @InEnum(value = DataScopeEnum.class, message = "数据范围必须是 {value}")
    private Integer dataScope;

    @Schema(description = "部门编号列表，只有范围类型为 DEPT_CUSTOM 时，该字段才需要", example = "[]")
    private Set<Long> dataScopeDeptIds = Collections.emptySet();

}
