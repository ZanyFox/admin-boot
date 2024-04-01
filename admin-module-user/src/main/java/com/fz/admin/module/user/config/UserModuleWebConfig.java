package com.fz.admin.module.user.config;

import com.fz.admin.framework.web.swagger.SwaggerConfig;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class UserModuleWebConfig {

    /**
     * user 模块的 API 分组
     */
    @Bean
    public GroupedOpenApi infraGroupedOpenApi() {
        return SwaggerConfig.buildGroupedOpenApi("user");
    }
}
