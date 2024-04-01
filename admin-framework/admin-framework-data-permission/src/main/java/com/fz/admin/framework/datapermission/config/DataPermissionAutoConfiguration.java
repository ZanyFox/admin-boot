package com.fz.admin.framework.datapermission.config;


import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.fz.admin.framework.datapermission.aop.DataPermissionAnnotationAdvisor;
import com.fz.admin.framework.datapermission.db.DataPermissionDatabaseInterceptor;
import com.fz.admin.framework.datapermission.rule.DataPermissionRule;
import com.fz.admin.framework.datapermission.rule.DataPermissionRuleFactory;
import com.fz.admin.framework.datapermission.rule.DataPermissionRuleFactoryImpl;
import com.fz.admin.framework.mybatis.util.MyBatisUtils;
import org.springframework.context.annotation.Bean;

import java.util.List;

/**
 * 数据权限的自动配置类
 *
 *
 */
// @Configuration(proxyBeanMethods = false)
public class DataPermissionAutoConfiguration {

    @Bean
    public DataPermissionRuleFactory dataPermissionRuleFactory(List<DataPermissionRule> rules) {
        return new DataPermissionRuleFactoryImpl(rules);
    }

    @Bean
    public DataPermissionDatabaseInterceptor dataPermissionDatabaseInterceptor(MybatisPlusInterceptor interceptor,
                                                                               DataPermissionRuleFactory ruleFactory) {
        // 创建 DataPermissionDatabaseInterceptor 拦截器
        DataPermissionDatabaseInterceptor inner = new DataPermissionDatabaseInterceptor(ruleFactory);
        // 添加到 interceptor 中
        // 需要加在首个，主要是为了在分页插件前面。这个是 MyBatis Plus 的规定
        MyBatisUtils.addInterceptor(interceptor, inner, 0);
        return inner;
    }

    @Bean
    public DataPermissionAnnotationAdvisor dataPermissionAnnotationAdvisor() {
        return new DataPermissionAnnotationAdvisor();
    }


}
