package com.fz.admin.module.user.model.param;


import com.fz.admin.framework.common.enums.CommonStatusEnum;
import com.fz.admin.framework.common.pojo.PageParam;
import com.fz.admin.framework.web.validation.validator.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "岗位分页查询")
@Data
public class PostPageParam extends PageParam {

    @Schema(description = "岗位编码，模糊匹配", example = "")
    private String code;

    @Schema(description = "岗位名称，模糊匹配", example = "")
    private String name;

    @Schema(description = "展示状态，参见 CommonStatusEnum 枚举类", example = "0")
    @InEnum(value = CommonStatusEnum.class)
    private Integer status;

}
