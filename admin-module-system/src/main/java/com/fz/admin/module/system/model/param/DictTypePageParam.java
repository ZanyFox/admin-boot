package com.fz.admin.module.system.model.param;


import com.fz.admin.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;


@Schema(description = "字典类型分页查询参数")
@Data
public class DictTypePageParam extends PageParam {

    @Schema(description = "字典类型名称，模糊匹配", example = "")
    private String name;

    @Schema(description = "字典类型，模糊匹配", example = "sys_common_sex")
    @Size(max = 100, message = "字典类型类型长度不能超过100个字符")
    private String type;

    @Schema(description = "展示状态，参见 CommonStatusEnum 枚举类", example = "0")
    private Integer status;

    @Schema(description = "开始时间", example = "[2022-07-01 00:00:00, 2022-07-01 23:59:59]")
    private LocalDateTime beginTime;


    @Schema(description = "结束时间", example = "[2022-07-01 00:00:00, 2022-07-01 23:59:59]")
    private LocalDateTime endTime;


}
