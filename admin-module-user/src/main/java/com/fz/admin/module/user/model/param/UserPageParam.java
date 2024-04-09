package com.fz.admin.module.user.model.param;


import com.fz.admin.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;



@Schema(description = "用户列表分页参数")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPageParam extends PageParam {

    @Schema(description = "用户账号，模糊匹配", example = "")
    private String username;

    @Schema(description = "手机号码，模糊匹配", example = "")
    private String mobile;

    @Schema(description = "展示状态", example = "1")
    private Integer status;

    @Schema(description = "开始时间", example = "[2022-07-01 00:00:00, 2022-07-01 23:59:59]")
    private LocalDateTime beginTime;

    @Schema(description = "结束时间", example = "[2022-07-01 00:00:00, 2022-07-01 23:59:59]")
    private LocalDateTime endTime;

    @Schema(description = "部门id", example = "0")
    private Long deptId;

}
