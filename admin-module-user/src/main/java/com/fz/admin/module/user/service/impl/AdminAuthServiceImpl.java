package com.fz.admin.module.user.service.impl;

import com.fz.admin.framework.common.enums.ServRespCode;
import com.fz.admin.framework.common.exception.ServiceException;
import com.fz.admin.framework.common.pojo.LoginUser;
import com.fz.admin.framework.common.util.Json;
import com.fz.admin.framework.redis.constant.RedisCacheConstant;
import com.fz.admin.module.user.service.AdminAuthService;
import com.fz.admin.module.user.service.SysUserService;
import com.fz.admin.module.user.model.entity.SysUser;
import com.fz.admin.module.user.model.param.PasswordLoginParam;
import com.fz.admin.module.user.model.vo.AuthLoginRespVO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
public class AdminAuthServiceImpl implements AdminAuthService {

    @Value("${admin.captcha.enable}")
    private Boolean captchaEnable;

    @Value("${admin.token.expired-time}")
    private Duration tokenExpiredTime;


    private final StringRedisTemplate stringRedisTemplate;

    private final PasswordEncoder passwordEncoder;

    private final SysUserService sysUserService;

    public AdminAuthServiceImpl(SysUserService sysUserService, StringRedisTemplate stringRedisTemplate,
                                PasswordEncoder passwordEncoder) {
        this.sysUserService = sysUserService;
        this.stringRedisTemplate = stringRedisTemplate;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AuthLoginRespVO passwordLogin(PasswordLoginParam param) {

        // TODO 校验验证码

        SysUser sysUser = sysUserService.lambdaQuery()
                .eq(SysUser::getUsername, param.getUsername())
                .one();

        if (sysUser == null)
            throw new ServiceException(ServRespCode.USER_LOGIN_ACCOUNT_NOT_EXIST);

        boolean isMatches = passwordEncoder.matches(param.getPassword(), sysUser.getPassword());
        if (!isMatches)
            throw new ServiceException(ServRespCode.USER_LOGIN_PASSWORD_WRONG);


        String token = UUID.randomUUID().toString().replace("-", "");
        LoginUser loginUser = new LoginUser();
        loginUser.setId(sysUser.getId());
        loginUser.setExpiredAt(tokenExpiredTime.toMillis() + System.currentTimeMillis());


        stringRedisTemplate.opsForValue().set(RedisCacheConstant.USER_TOKEN_PREFIX + token, Json.toJsonString(loginUser), tokenExpiredTime);
        Instant expiredTime = Instant.now().plusMillis(tokenExpiredTime.toMillis());
        return new AuthLoginRespVO(sysUser.getId(), token, String.valueOf(expiredTime.toEpochMilli()));
    }


}
