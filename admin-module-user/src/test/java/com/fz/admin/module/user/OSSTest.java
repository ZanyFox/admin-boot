package com.fz.admin.module.user;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.crypto.digest.DigestUtil;
import com.fz.admin.framework.file.AliYunOSSFileService;
import com.fz.admin.framework.file.FileService;
import com.fz.admin.framework.file.config.AliYunOSSConfiguration;
import com.fz.admin.framework.file.config.AliYunOSSProperties;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = OSSTest.Application.class)
@ActiveProfiles("unit-test") // 设置使用 application-unit-test 配置文件

public class OSSTest {


    @Import({
            AliYunOSSConfiguration.class,
            AliYunOSSProperties.class,
            AliYunOSSFileService.class,
    })
    public static class Application {


    }



    @Resource
    private FileService fileService;

    @Test
    public void testUpload() {
        String path = "E:\\img\\meizhi\\0061yzyggy1h01amb8908j3334445u0y.jpg";

        String filename = "0061yzyggy1h01amb8908j3334445u0y.jpg";
        String extName = FileUtil.extName(filename);
        filename = DigestUtil.md5Hex(filename + System.currentTimeMillis()) + StrPool.DOT + extName;
        System.out.println(filename);

        try (InputStream inputStream = Files.newInputStream(Paths.get(path))) {

            String s = fileService.uploadFile(inputStream, filename);
            System.out.println(s);
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
    }
}
