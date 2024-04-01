package com.fz.admin.module.user.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "登录响应实体")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthLoginRespVO {


    @Schema(description = "用户id", requiredMode = Schema.RequiredMode.REQUIRED, example = "")
    private Long userId;

    @Schema(description = "访问令牌", requiredMode = Schema.RequiredMode.REQUIRED, example = "")
    private String token;


    @Schema(description = "过期时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private String expiresTime;

}
