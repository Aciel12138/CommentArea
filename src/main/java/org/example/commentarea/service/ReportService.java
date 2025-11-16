package org.example.commentarea.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

/**
 * 实验报告服务接口
 */
public interface ReportService {

    /**
     * 生成实验报告
     *
     * @param extractedDirPath 解压后的目录路径
     * @param reportName 报告名称
     * @return 生成的报告文件路径
     * @throws IOException 当文件操作出现错误时抛出
     */


    /**
     * 处理上传的ZIP文件并生成实验报告
     *
     * @param file 上传的ZIP文件
     * @param reportName 报告名称
     * @return 生成的报告文件路径
     * @throws IOException 当文件操作出现错误时抛出
     */
    Path processZipAndGenerateReport(MultipartFile file, String reportName) throws IOException;
}