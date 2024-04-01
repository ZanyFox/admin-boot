package com.fz.admin.framework.file.config;

import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.common.comm.SignVersion;
import com.fz.admin.framework.common.constant.Constants;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(AliYunOSSProperties.class)
public class AliYunOSSConfiguration {


    @Bean
    public OSS ossClient(AliYunOSSProperties aliyunOSSProperties) {


        ClientBuilderConfiguration clientBuilderConfiguration = new ClientBuilderConfiguration();
        clientBuilderConfiguration.setSignatureVersion(SignVersion.V4);

        return OSSClientBuilder.create()
                .endpoint(Constants.HTTPS_PREFIX + aliyunOSSProperties.getEndpoint())
                .credentialsProvider(new DefaultCredentialProvider(aliyunOSSProperties.getAccessKeyId(), aliyunOSSProperties.getAccessKeySecret()))
                .clientConfiguration(clientBuilderConfiguration)
                .region(aliyunOSSProperties.getRegion())
                .build();
    }
}
