package com.fz.admin.module.user;

import com.alibaba.druid.spring.boot3.autoconfigure.DruidDataSourceAutoConfigure;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusInnerInterceptorAutoConfiguration;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fz.admin.framework.common.pojo.TreeSelect;
import com.fz.admin.framework.file.AliYunOSSFileService;
import com.fz.admin.framework.file.FileService;
import com.fz.admin.framework.file.config.AliYunOSSConfiguration;
import com.fz.admin.framework.file.config.AliYunOSSProperties;
import com.fz.admin.framework.mybatis.config.MyBatisPlusConfig;
import com.fz.admin.framework.mybatis.interceptor.SQLLogInterceptor;
import com.fz.admin.framework.mybatis.util.MyBatisUtils;
import com.fz.admin.module.user.mapper.SysUserMapper;
import com.fz.admin.module.user.model.entity.SysPost;
import com.fz.admin.module.user.model.entity.SysUser;
import com.fz.admin.module.user.model.entity.SysUserPost;
import com.fz.admin.module.user.model.param.DeptListParam;
import com.fz.admin.module.user.model.param.UserPageParam;
import com.fz.admin.module.user.service.SysDeptService;
import com.fz.admin.module.user.service.SysPostService;
import com.fz.admin.module.user.service.SysUserService;
import com.fz.admin.module.user.service.impl.SysDeptServiceImpl;
import com.fz.admin.module.user.service.impl.SysPostServiceImpl;
import com.fz.admin.module.user.service.impl.SysUserRoleServiceImpl;
import com.fz.admin.module.user.service.impl.SysUserServiceImpl;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = UserTest.Application.class)
@ActiveProfiles("unit-test") // 设置使用 application-unit-test 配置文件
public class UserTest {

    //
    @Resource
    private SysUserMapper userMapper;


    @Mock
    private List<String> mockList;

    // @Resource
    // private SysMenuMapper menuMapper;

    @Value("${spring.application.name:hhh}")
    private String appName;


    // @Resource
    // private SysUserRoleService userRoleService;


    @Resource
    private ApplicationContext context;

    // @Resource
    // private SysUserService sysUserService;

    // @Resource
    // private SysRoleMapper roleMapper;
    //
    // @Resource
    // private SysUserPostMapper userPostMapper;

    @Resource
    private SysPostService postService;

    @Resource
    private FileService fileService;

    @Resource
    private AliYunOSSProperties aliYunOSSProperties;

    @Resource
    private SysDeptService deptService;

    @Resource
    private SysUserService userService;


    @Import({

            MyBatisPlusConfig.class,
            DataSourceAutoConfiguration.class, // Spring DB 自动配置类
            DataSourceTransactionManagerAutoConfiguration.class, // Spring 事务自动配置类
            DruidDataSourceAutoConfigure.class, // Druid 自动配置类
            MybatisPlusInnerInterceptorAutoConfiguration.class,
            MybatisPlusAutoConfiguration.class, // MyBatis 的自动配置类
            MyBatisPlusConfig.class,
            SQLLogInterceptor.class,
            SysUserRoleServiceImpl.class,
            // SysUserPostMapper.class,
            BCryptPasswordEncoder.class,
            SysUserServiceImpl.class,
            AliYunOSSConfiguration.class,
            AliYunOSSFileService.class,
            SysDeptServiceImpl.class,
            SysPostServiceImpl.class,
            SysUserServiceImpl.class
            // SysUserMapper.class,
            // SysDeptMapper.class
    })
    public static class Application {


    }

    @Test
    public void uploadFile() {



        String path = "E:\\img\\meizhi\\0061yzyggy1h01amb8908j3334445u0y.jpg";


        try(InputStream inputStream = Files.newInputStream(Paths.get(path))) {
            fileService.uploadFile(inputStream, "test.jpg");
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
    }

    @Test
    public void testDept() {

        List<TreeSelect> deptTree = deptService.getDeptTree(new DeptListParam());
        System.out.println(deptTree);
    }



    @Test
    public void testUserPermission() {
        SysUserPost sysUserPost = new SysUserPost();
        sysUserPost.setPostId(100L);
        sysUserPost.setUserId(100L);
        SysUserPost sysUserPost1 = new SysUserPost();
        sysUserPost1.setPostId(200L);
        sysUserPost1.setUserId(200L);

        // userPostMapper.insertBatch(List.of(sysUserPost, sysUserPost1));
    }

    @Test
    public void testUserPage() {

        UserPageParam pageParam = new UserPageParam();

        // pageParam.setPage(2);
        // pageParam.setUsername("子");
        // pageParam.setStatus(0);
        pageParam.setDeptId(101L);

        IPage<SysUser> page = MyBatisUtils.buildPage(pageParam);
        userMapper.selectUserPage(page, pageParam);
        System.out.println(page.getRecords());
    }

    @Test
    public void testPost() {
        // List<SysPost> simplePostList = postService.getSimplePostList();
        // System.out.println(simplePostList);

        List<SysPost> list = postService.lambdaQuery()
                // .select(SysPost::getId, SysPost::getName, SysPost::getOrder)
                // .eq(SysPost::getStatus, CommonStatusEnum.ENABLE.getStatus())
                .list();
        System.out.println(list);
    }

    @Test
    public void testUpdateUserStatus() {
        userService.updateUserStatus(112L, 1);
    }

    @Test
    public void testMock() {

        mockList.add("hello");
        // 验证add("hello") 是否执行过
        verify(mockList).add("hello");
    }
}
