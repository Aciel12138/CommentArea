package org.example.commentarea.utils;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class unzipTheFile {
    /**
     * 从资源目录解压ZIP文件的方法
     */
    public void extractZipFromResources() {
        // ZIP文件名
        String zipFileName = "多进程的实现.zip";
        // 输出文件夹名
        String outputFolder = "extracted-files";

        try {
            // 从资源目录获取ZIP文件输入流
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(zipFileName);

            // 检查文件是否存在
            if (inputStream == null) {
                System.out.println("在资源目录中找不到 " + zipFileName);
                return;
            }

            // 如果输出目录不存在则创建
            Path outputPath = Paths.get(outputFolder);
            if (!Files.exists(outputPath)) {
                Files.createDirectory(outputPath);
            }

            // 解压ZIP文件到指定目录
            extractZip(inputStream, outputPath.toFile());

            System.out.println("成功解压 " + zipFileName + " 到 " + outputFolder + " 目录");

        } catch (Exception e) {
            // 处理解压过程中可能出现的异常
            System.err.println("解压文件时出错: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 解压上传的ZIP文件到指定目录
     * 
     * @param file 上传的ZIP文件
     * @param targetDirectory 目标目录
     * @return 解压后的目录路径
     * @throws IOException 当解压过程中发生IO错误时抛出
     */
    public Path extractUploadedZip(MultipartFile file, String targetDirectory) throws IOException {
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
        
        return outputPath;
    }

    /**
     * 解压ZIP文件的核心方法
     * @param inputStream ZIP文件输入流
     * @param destDir 目标目录
     * @throws IOException IO异常
     */
    private void extractZip(InputStream inputStream, File destDir) throws IOException {
        // 缓冲区大小
        byte[] buffer = new byte[1024];
        // 创建ZIP输入流
        ZipInputStream zis = new ZipInputStream(inputStream);
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
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
            }

            // 获取下一个ZIP条目
            zipEntry = zis.getNextEntry();
        }

        // 关闭ZIP条目和ZIP输入流
        zis.closeEntry();
        zis.close();
    }

    /**
     * 安全地创建新文件，防止Zip Slip漏洞
     * @param destinationDir 目标目录
     * @param zipEntry ZIP条目
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