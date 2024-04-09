package com.fz.admin.module.user;

import com.alibaba.druid.spring.boot3.autoconfigure.DruidDataSourceAutoConfigure;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusInnerInterceptorAutoConfiguration;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fz.admin.framework.common.pojo.PageParam;
import com.fz.admin.framework.mybatis.config.MyBatisPlusConfig;
import com.fz.admin.framework.mybatis.interceptor.SQLLogInterceptor;
import com.fz.admin.framework.mybatis.util.MyBatisUtils;
import com.fz.admin.module.user.mapper.SysUserMapper;
import com.fz.admin.module.user.mapper.SysUserRoleMapper;
import com.fz.admin.module.user.model.entity.SysUser;
import com.fz.admin.module.user.model.entity.SysUserRole;
import com.fz.admin.module.user.model.param.UserAssignRolePageParam;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = UserSqlTest.Application.class)
@ActiveProfiles("unit-test") // 设置使用 application-unit-test 配置文件
public class UserSqlTest {


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

    @Resource
    private SysUserRoleMapper userRoleMapper;

    @Resource
    private SysUserMapper userMapper;

    @Test
    public void testUserRole() {
        SysUserRole userRole = new SysUserRole();
        userRole.setUserId(1000L);
        userRole.setRoleId(1000L);
        userRoleMapper.insertBatch(List.of(userRole));
    }

    @Test
    public void testSelectUsersPageByRoleId() {
        IPage<SysUser> page = MyBatisUtils.buildPage(new PageParam());
        userMapper.selectUserPageByRoleId(page, 2L, new UserAssignRolePageParam());
        System.out.println(page);
    }

    @Test
    public void testSelectNotAssignedRoleUserPage() {
        IPage<SysUser> page = MyBatisUtils.buildPage(new PageParam());
        userMapper.selectNotAssignedRoleUserPage(page, 2L, new UserAssignRolePageParam());
        System.out.println(page);

    }
}
