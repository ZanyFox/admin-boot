package com.fz.admin.framework.web.config;

import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static com.fz.admin.framework.common.constant.WebFilterOrder.CORS_FILTER;

@Configuration(proxyBeanMethods = false)
public class WebMvcConfig implements WebMvcConfigurer {

    private final static String API_PREFIX = "/api";

    private final static String API_CONTROLLER_PACKAGE = "**.controller.app.**";

    private final static String ADMIN_API_PREFIX = "/api/admin";

    private final static String ADMIN_API_CONTROLLER_PACKAGE = "**.controller.admin.**";

    /*
     * 配置路径前缀
     * */
    @Override
    public void configurePathMatch(@NonNull PathMatchConfigurer configurer) {

        AntPathMatcher antPathMatcher = new AntPathMatcher(".");

        configurer.addPathPrefix(API_PREFIX, clazz -> clazz.isAnnotationPresent(RestController.class)
                && antPathMatcher.match(API_CONTROLLER_PACKAGE, clazz.getPackage().getName()));

        configurer.addPathPrefix(ADMIN_API_PREFIX, clazz -> clazz.isAnnotationPresent(RestController.class)
                && antPathMatcher.match(ADMIN_API_CONTROLLER_PACKAGE, clazz.getPackage().getName()));
    }


    /**
     * 创建 CorsFilter Bean，解决跨域问题
     */
    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilterBean() {
        // 创建 CorsConfiguration 对象
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("*"); // 设置访问源地址
        config.addAllowedHeader("*"); // 设置访问源请求头
        config.addAllowedMethod("*"); // 设置访问源请求方法
        // 创建 UrlBasedCorsConfigurationSource 对象
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // 对接口配置跨域设置

        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new CorsFilter(source));
        bean.setOrder(CORS_FILTER);
        return bean;
    }

    /**
     * 创建 RestTemplate 实例
     *
     * @param restTemplateBuilder {@link RestTemplateAutoConfiguration#restTemplateBuilder}
     */
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder.build();
    }
}
