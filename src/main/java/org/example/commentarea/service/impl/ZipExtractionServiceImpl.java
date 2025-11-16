package org.example.commentarea.service.impl;

import org.example.commentarea.service.ZipExtractionService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * ZIP文件解压服务实现类
 */
@Service
public class ZipExtractionServiceImpl implements ZipExtractionService {

    @Override
    public Path extractZipToDirectory(MultipartFile file, String targetDirectory) throws IOException {
        // 获取资源目录路径
        String resourcePath = "src/main/resources/";
        Path outputPath = Paths.get(resourcePath, targetDirectory);

        // 如果输出目录不存在则创建
        if (!Files.exists(outputPath)) {
            Files.createDirectories(outputPath);
        }

        // 将MultipartFile转换为InputStream并解压
        try (InputStream inputStream = file.getInputStream()) {
            extractZip(inputStream, outputPath.toFile());
        }

        // 获取解压后的第一级目录
        String firstLevelDirectory = getFirstLevelDirectory(outputPath);

        // 返回outpath+解压缩后的第一级目录
        return outputPath.resolve(firstLevelDirectory);
    }

    /**
     * 获取解压后的第一级目录
     * @param outputPath 解压的目标目录路径
     * @return 第一级目录名称
     */
    private String getFirstLevelDirectory(Path outputPath) {
        File[] files = outputPath.toFile().listFiles();
        if (files != null && files.length > 0) {
            // 返回第一个文件或目录的名称
            return files[0].getName();
        }
        return ""; // 如果没有找到任何文件或目录，则返回空字符串
    }

    /**
     * 解压ZIP文件的核心方法
     *
     * @param inputStream ZIP文件输入流
     * @param destDir     目标目录
     * @throws IOException IO异常
     */
    private void extractZip(InputStream inputStream, File destDir) throws IOException {
        // 缓冲区大小
        byte[] buffer = new byte[1024];
        // 创建ZIP输入流
        try (ZipInputStream zis = new ZipInputStream(inputStream)) {
            // 获取第一个ZIP条目
            ZipEntry zipEntry = zis.getNextEntry();

            // 遍历ZIP文件中的所有条目
            while (zipEntry != null) {
                // 创建目标文件
                File newFile = newFile(destDir, zipEntry);

                // 如果是目录则创建目录
                if (zipEntry.isDirectory()) {
                    if (!newFile.isDirectory() && !newFile.mkdirs()) {
                        throw new IOException("无法创建目录 " + newFile);
                    }
                } else {
                    // 如果是文件则创建父目录
                    File parent = newFile.getParentFile();
                    if (!parent.isDirectory() && !parent.mkdirs()) {
                        throw new IOException("无法创建目录 " + parent);
                    }

                    // 写入文件内容
                    try (FileOutputStream fos = new FileOutputStream(newFile)) {
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                }

                // 获取下一个ZIP条目
                zipEntry = zis.getNextEntry();
            }
        }
    }

    /**
     * 安全地创建新文件，防止Zip Slip漏洞
     *
     * @param destinationDir 目标目录
     * @param zipEntry       ZIP条目
     * @return 新文件
     * @throws IOException IO异常
     */
    private File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        // 创建目标文件
        File destFile = new File(destinationDir, zipEntry.getName());

        // 获取规范路径以防止路径遍历攻击
        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        // 检查文件是否在目标目录内，防止Zip Slip漏洞
        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("条目在目标目录外: " + zipEntry.getName());
        }

        return destFile;
    }
}