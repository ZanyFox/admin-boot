package com.fz.admin.module.user;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.fastjson2.JSON;
import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.common.comm.SignVersion;
import com.aliyun.oss.model.PutObjectResult;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;


public class OSSTest {

    String accessKeyId = "";

    String accessKeySecret = "";

    String bucket = "my-test01-mall";

    String endpoint = "https://oss-cn-hangzhou.aliyuncs.com";

    String region = "cn-hangzhou";

    @Test
    public void testUpload() {

        ClientBuilderConfiguration clientBuilderConfiguration = new ClientBuilderConfiguration();
        clientBuilderConfiguration.setSignatureVersion(SignVersion.V4);

        OSS ossClient = OSSClientBuilder.create()
                .endpoint(endpoint)
                .credentialsProvider(new DefaultCredentialProvider(accessKeyId, accessKeySecret))
                .clientConfiguration(clientBuilderConfiguration)
                .region(region)
                .build();

        String path = "E:\\img\\meizhi\\0061yzyggy1h01amb8908j3334445u0y.jpg";

        String filename =  "0061yzyggy1h01amb8908j3334445u0y.jpg";
        String extName = FileUtil.extName(filename);
        filename = DigestUtil.md5Hex(filename + System.currentTimeMillis()) + StrPool.DOT + extName;
        System.out.println(filename);

        try(InputStream inputStream = Files.newInputStream(Paths.get(path))) {

            PutObjectResult putObjectResult = ossClient.putObject(bucket, filename, inputStream);
            System.out.println(JSON.toJSONString(putObjectResult));
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
    }
}
