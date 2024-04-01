package com.fz.admin.module.user.controller.admin;


import com.fz.admin.framework.common.pojo.ServRespEntity;
import com.fz.admin.module.user.model.param.PasswordLoginParam;
import com.fz.admin.module.user.model.vo.AuthLoginRespVO;
import com.fz.admin.module.user.service.AdminAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.PermitAll;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.fz.admin.framework.common.pojo.ServRespEntity.success;

@Tag(name = "用户认证")
@RestController
@RequestMapping("/system/auth")
@AllArgsConstructor
public class AuthController {

    private AdminAuthService adminAuthService;

    @PostMapping("/login")
    @PermitAll
    @Operation(summary = "使用账号密码登录")
    public ServRespEntity<AuthLoginRespVO> login(@RequestBody @Validated PasswordLoginParam param) {

        return success(adminAuthService.passwordLogin(param));
    }

}
