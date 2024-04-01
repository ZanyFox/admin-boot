package com.fz.admin.boot;


import com.fz.admin.framework.common.pojo.PageResult;
import com.fz.admin.module.user.model.entity.SysUser;
import com.fz.admin.module.user.model.param.UserPageParam;
import com.fz.admin.module.user.service.SysUserRoleService;
import com.fz.admin.module.user.service.SysUserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.redis.core.RedisTemplate;


@Slf4j
@EnableAspectJAutoProxy(exposeProxy = true)
@SpringBootApplication(scanBasePackages = {"com.fz.admin"})
public class AdminBootApplication implements CommandLineRunner {

    @Resource
    private RedisTemplate<String ,Object> redisTemplate;

    @Resource
    private SysUserRoleService userRoleService;

    @Resource
    private SysUserService sysUserService;

    public static void main(String[] args) {
        SpringApplication.run(AdminBootApplication.class);
    }

    @Override
    public void run(String... args) throws Exception {

        UserPageParam param = new UserPageParam();
        param.setPage(2);
        param.setSize(5);

        PageResult<SysUser> result = sysUserService.page(param);
        System.out.println(result);
    }
}
