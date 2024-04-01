package com.fz.admin.module.system.model.param;


import com.fz.admin.framework.common.enums.CommonStatusEnum;
import com.fz.admin.framework.common.pojo.PageParam;
import com.fz.admin.framework.web.validation.validator.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(description = "字典类型分页查询参数")
@Data
public class DictDataPageParam extends PageParam {

    @Schema(description = "字典标签", example = "芋道")
    @Size(max = 100, message = "字典标签长度不能超过100个字符")
    private String label;

    @Schema(description = "字典类型，模糊匹配", example = "sys_common_sex")
    @Size(max = 100, message = "字典类型类型长度不能超过100个字符")
    private String dictType;

    @Schema(description = "展示状态，参见 CommonStatusEnum 枚举类", example = "0")
    @InEnum(value = CommonStatusEnum.class, message = "修改状态必须是 {value}")
    private Integer status;

}
