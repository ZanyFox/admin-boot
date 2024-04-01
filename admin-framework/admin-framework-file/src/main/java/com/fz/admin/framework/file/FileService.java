package com.fz.admin.framework.file;

import java.io.InputStream;

public interface FileService {

    String uploadFile(InputStream inputStream, String filename);
}
