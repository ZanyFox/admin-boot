package com.fz.admin.framework.file;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.crypto.digest.DigestUtil;
import com.aliyun.oss.OSS;
import com.fz.admin.framework.file.config.AliYunOSSProperties;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class AliYunOSSFileService implements FileService {



    private final OSS ossClient;

    private final AliYunOSSProperties properties;

    public AliYunOSSFileService(OSS ossClient, AliYunOSSProperties properties) {
        this.ossClient = ossClient;
        this.properties = properties;
    }

    @Override
    public String uploadFile(InputStream inputStream, String filename) {

        String extName = FileUtil.extName(filename);
        filename = DigestUtil.md5Hex(filename + System.currentTimeMillis()) + StrPool.DOT + extName;
        ossClient.putObject(properties.getBucket(), filename, inputStream);

        return String.format("https://%s.%s/%s", properties.getBucket(), properties.getEndpoint(), filename);
    }
}
