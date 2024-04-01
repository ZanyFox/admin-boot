package com.fz.admin.module.user.model.param;


import com.fz.admin.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;


@Schema(description = "角色分页")
@Data
public class RolePageParam extends PageParam {

    @Schema(description = "角色名称，模糊匹配", example = "管理员")
    private String name;

    @Schema(description = "角色标识，模糊匹配", example = "admin")
    private String key;

    @Schema(description = "展示状态，参见 CommonStatusEnum 枚举类", example = "1")
    private Integer status;

    @Schema(description = "开始时间", example = "2022-07-01 00:00:00")
    private LocalDateTime beginTime;

    @Schema(description = "结束时间", example = "2022-07-01 00:00:00")
    private LocalDateTime endTime;

}
