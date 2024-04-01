package com.fz.admin.framework.mybatis.config;


import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
@MapperScan(basePackages = {"com.fz.admin.**.mapper"})
public class MyBatisPlusConfig {

    @Order(100)
    @Bean
    public PaginationInnerInterceptor paginationInnerInterceptor() {

        return new PaginationInnerInterceptor();
    }


    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new MybatisPlusMetaObjectHandler();
    }
}
