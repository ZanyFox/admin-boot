package com.fz.admin.framework.web.swagger;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Swagger 配置属性
 *
 *
 */
@ConfigurationProperties("swagger")
@Data
public class SwaggerProperties {

    /**
     * 标题
     */
    private String title;
    /**
     * 描述
     */
    private String description;
    /**
     * 作者
     */
    private String author;
    /**
     * 版本
     */
    private String version;
    /**
     * url
     */
    private String url;
    /**
     * email
     */
    private String email;

    /**
     * license
     */
    private String license;

    /**
     * license-url
     */
    private String licenseUrl;

}
