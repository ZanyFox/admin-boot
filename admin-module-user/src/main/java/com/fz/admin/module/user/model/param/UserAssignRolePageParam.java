package com.fz.admin.module.user.model.param;

import com.fz.admin.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "未被分配角色用户分页参数")
@Data
public class UserAssignRolePageParam extends PageParam {


    @Schema(description = "用户账号，模糊匹配", example = "")
    private String username;

    @Schema(description = "手机号码，模糊匹配", example = "")
    private String mobile;

}
