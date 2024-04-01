package com.fz.admin.boot;


import com.fz.admin.module.user.mapper.SysUserMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest
public class AdminBootTest {

    @Autowired
    private ApplicationContext context;

    @Test
    public void testApplication() {

        Assertions.assertNotNull(context.getBean(SysUserMapper.class));
    }
}
