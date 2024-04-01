package com.fz.admin.module.user.model.param;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema(description = "账号密码登录参数")
@Data
public class PasswordLoginParam {


    @Schema(description = "账号", requiredMode = Schema.RequiredMode.REQUIRED, example = "admin")
    @NotBlank
    private String username;

    @Schema(description = "密码", requiredMode = Schema.RequiredMode.REQUIRED, example = "123456")
    @NotBlank
    private String password;

    @Schema(description = "验证码，验证码开启时，需要传递", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "0")
    private String captchaCode;

}
