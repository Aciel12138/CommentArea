package org.example.commentarea.controller;

import org.example.commentarea.entity.RestBean;
import org.example.commentarea.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

/**
 * 实验报告控制器
 */
@RestController
@RequestMapping("/api/report")
public class ReportController {

    @Autowired
    private ReportService reportService;

    /**
     * 上传ZIP文件并生成实验报告
     *
     * @param file 上传的ZIP文件
     * @param reportName 报告名称
     * @return 生成的报告文件
     */
    @PostMapping("/generate")
    public ResponseEntity<Resource> generateReport(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "reportName", defaultValue = "实验报告") String reportName) {
        try {
            // 处理上传的ZIP文件并生成报告
            Path reportPath = reportService.processZipAndGenerateReport(file, reportName);

            // 将生成的报告作为响应返回
            Resource resource = new FileSystemResource(reportPath.toFile());
            
            // 从路径中提取文件名用于下载
            String fileName = reportPath.getFileName().toString();
            
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(reportPath.toFile().length())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}