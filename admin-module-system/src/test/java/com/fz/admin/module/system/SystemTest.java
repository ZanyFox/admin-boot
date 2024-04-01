package com.fz.admin.module.system;


import com.alibaba.druid.spring.boot3.autoconfigure.DruidDataSourceAutoConfigure;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusInnerInterceptorAutoConfiguration;
import com.fz.admin.framework.mybatis.config.MyBatisPlusConfig;
import com.fz.admin.framework.mybatis.interceptor.SQLLogInterceptor;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = SystemTest.Application.class)
@ActiveProfiles("unit-test") // 设置使用 application-unit-test 配置文件

public class SystemTest {

    @Import({

            MyBatisPlusConfig.class,
            DataSourceAutoConfiguration.class, // Spring DB 自动配置类
            DataSourceTransactionManagerAutoConfiguration.class, // Spring 事务自动配置类
            DruidDataSourceAutoConfigure.class, // Druid 自动配置类
            MybatisPlusInnerInterceptorAutoConfiguration.class,
            MybatisPlusAutoConfiguration.class, // MyBatis 的自动配置类
            MyBatisPlusConfig.class,
            SQLLogInterceptor.class,
    })
    public static class Application {
    }
}
