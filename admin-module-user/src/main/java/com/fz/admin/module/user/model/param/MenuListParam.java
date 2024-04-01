package com.fz.admin.module.user.model.param;

import com.fz.admin.framework.common.enums.CommonStatusEnum;
import com.fz.admin.framework.web.validation.validator.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "菜单列表查询参数")
@Data
public class MenuListParam {

    @Schema(description = "菜单名称，模糊匹配", example = "芋道")
    private String name;

    @Schema(description = "展示状态，参见 CommonStatusEnum 枚举类", example = "0")
    @InEnum(value = CommonStatusEnum.class)
    private Integer status;

}
