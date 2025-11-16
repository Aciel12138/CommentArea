package org.example.commentarea.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

/**
 * ZIP文件解压服务接口
 */
public interface ZipExtractionService {
    /**
     * 解压上传的ZIP文件到指定目录
     *
     * @param file 要解压的ZIP文件
     * @param targetDirectory 目标目录
     * @return 解压后的目录路径
     * @throws IOException 当解压过程中发生IO错误时抛出
     */
    Path extractZipToDirectory(MultipartFile file, String targetDirectory) throws IOException;
}