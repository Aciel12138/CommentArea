package org.example.commentarea.service.impl;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.example.commentarea.service.ReportService;
import org.example.commentarea.service.ZipExtractionService;
import org.example.commentarea.utils.Doc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * 实验报告服务实现类
 */
@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ZipExtractionService zipExtractionService;

    @Override
    public Path processZipAndGenerateReport(MultipartFile file, String reportName) throws IOException {
        // 先解压ZIP文件到临时目录
        Path extractedDir = zipExtractionService.extractZipToDirectory(file, "temp-extracted");
        
        // 确保报告目录存在
        Path reportsDir = Paths.get("src/main/resources/reports");
        if (!java.nio.file.Files.exists(reportsDir)) {
            java.nio.file.Files.createDirectories(reportsDir);
        }

        // 生成随机文件名以避免冲突
        String randomFileName = reportName + "_" + UUID.randomUUID().toString().substring(0, 8);
        Path reportPath = reportsDir.resolve(randomFileName + ".docx");

        // 使用Doc工具类生成报告
        Doc.generateReport(extractedDir, reportPath);

        return reportPath;
    }
}