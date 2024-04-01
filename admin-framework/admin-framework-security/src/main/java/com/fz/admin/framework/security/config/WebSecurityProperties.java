package com.fz.admin.framework.security.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collections;
import java.util.List;

@ConfigurationProperties(prefix = "admin.security")
@Data
public class WebSecurityProperties {

    /**
     * HTTP 请求时，访问令牌的请求 Header
     */
    private String tokenHeader = "Authorization";

    /**
     * 免登录的 URL 列表
     */
    private List<String> permitAllUrls = Collections.emptyList();

    /**
     * PasswordEncoder 加密复杂度，越高开销越大
     */
    private Integer passwordEncoderLength = 4;
}
