package org.example.commentarea.utils;

import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Doc {

    /**
     * 生成实验报告DOCX文档
     * 
     * @param extractedDir 解压后的目录路径
     * @param outputPath 输出文件路径
     * @throws IOException 当文件操作出现错误时抛出
     */
    public static void generateReport(Path extractedDir, Path outputPath) throws IOException {
        try (XWPFDocument document = new XWPFDocument()) {
            // 构造代码和图片目录的完整路径
            Path codeDir = extractedDir.resolve("代码");
            Path imageDir = extractedDir.resolve("图片");
            
            // 检查目录是否存在，如果不存在则使用解压目录本身
            if (!Files.exists(codeDir) || !Files.isDirectory(codeDir)) {
                codeDir = extractedDir;
            }
            if (!Files.exists(imageDir) || !Files.isDirectory(imageDir)) {
                imageDir = extractedDir;
            }
            
            // 写入代码部分
            writeCodeSection(document, codeDir);
            
            // 写入图片部分
            writeImageSection(document, imageDir);
            
            // 保存文档
            try (FileOutputStream out = new FileOutputStream(outputPath.toFile())) {
                document.write(out);
            }
        }
    }

    private static void writeCodeSection(XWPFDocument document, Path codeDir) throws IOException {
        // 添加标题段落
        XWPFParagraph title = document.createParagraph();
        title.setAlignment(ParagraphAlignment.LEFT);
        XWPFRun titleRun = title.createRun();
        titleRun.setText("实验代码:");
        titleRun.setBold(true);
        titleRun.setFontSize(14);

        // 获取所有代码文件并排序
        File[] codeFiles = codeDir.toFile().listFiles((dir, name) -> name.endsWith(".c") || name.endsWith(".cpp") || name.endsWith(".java"));
        if (codeFiles != null) {
            Arrays.sort(codeFiles, Comparator.comparing(File::getName));

            for (int i = 0; i < codeFiles.length; i++) {
                File codeFile = codeFiles[i];

                // 添加代码文件名段落
                XWPFParagraph namePara = document.createParagraph();
                XWPFRun nameRun = namePara.createRun();
                nameRun.setText((i+1) + "." + codeFile.getName());
                nameRun.setBold(false);

                // 添加代码内容段落（带语法高亮）
                String codeContent = Files.readString(codeFile.toPath());
                addHighlightedCode(document, codeContent);

                // 添加空行
                document.createParagraph();
            }
        }
    }

    private static void addHighlightedCode(XWPFDocument document, String code) {
        XWPFParagraph codePara = document.createParagraph();
        XWPFRun codeRun = codePara.createRun();
        codeRun.setFontFamily("Courier New");
        codeRun.setFontSize(10);

        // 简单的C语言代码高亮实现
        String[] lines = code.split("\n");
        for (int i = 0; i < lines.length; i++) {
            if (i > 0) {
                // 为每一行创建新段落以保持格式
                codePara = document.createParagraph();
                codeRun = codePara.createRun();
                codeRun.setFontFamily("Courier New");
                codeRun.setFontSize(10);
            }

            String line = lines[i];
            // 高亮注释（绿色）
            if (line.contains("//")) {
                String[] parts = line.split("//", 2);
                codeRun.setText(parts[0]);
                codeRun = codePara.createRun();
                codeRun.setFontFamily("Courier New");
                codeRun.setFontSize(10);
                codeRun.setColor("008000"); // 绿色
                codeRun.setText("//" + parts[1]);
                codeRun = codePara.createRun();
                codeRun.setFontFamily("Courier New");
                codeRun.setFontSize(10);
            }
            // 高亮字符串（蓝色）
            else if (line.contains("\"")) {
                Pattern pattern = Pattern.compile("\".*?\"");
                Matcher matcher = pattern.matcher(line);
                int lastEnd = 0;

                while (matcher.find()) {
                    // 添加引号前的文本
                    if (matcher.start() > lastEnd) {
                        codeRun.setText(line.substring(lastEnd, matcher.start()));
                        codeRun = codePara.createRun();
                        codeRun.setFontFamily("Courier New");
                        codeRun.setFontSize(10);
                    }

                    // 添加带颜色的字符串
                    codeRun.setColor("0000FF"); // 蓝色
                    codeRun.setText(matcher.group());
                    codeRun = codePara.createRun();
                    codeRun.setFontFamily("Courier New");
                    codeRun.setFontSize(10);

                    lastEnd = matcher.end();
                }

                // 添加引号后的文本
                if (lastEnd < line.length()) {
                    codeRun.setText(line.substring(lastEnd));
                }
            }
            // 高亮关键字（粗体）
            else {
                String[] keywords = {"#include", "int", "void", "return", "if", "else", "for", "while", "printf", "sleep", "fork", "execlp", "wait", "exit"};
                boolean processed = false;

                for (String keyword : keywords) {
                    if (line.contains(keyword)) {
                        Pattern pattern = Pattern.compile("\\b" + keyword + "\\b");
                        Matcher matcher = pattern.matcher(line);
                        int lastEnd = 0;

                        while (matcher.find()) {
                            // 添加关键字前的文本
                            if (matcher.start() > lastEnd) {
                                codeRun.setText(line.substring(lastEnd, matcher.start()));
                                codeRun = codePara.createRun();
                                codeRun.setFontFamily("Courier New");
                                codeRun.setFontSize(10);
                            }

                            // 添加粗体关键字
                            codeRun.setBold(true);
                            codeRun.setText(matcher.group());
                            codeRun = codePara.createRun();
                            codeRun.setFontFamily("Courier New");
                            codeRun.setFontSize(10);
                            codeRun.setBold(false);

                            lastEnd = matcher.end();
                        }

                        // 添加关键字后的文本
                        if (lastEnd < line.length()) {
                            codeRun.setText(line.substring(lastEnd));
                        }

                        processed = true;
                        break;
                    }
                }

                // 如果没有关键字，直接添加文本
                if (!processed) {
                    codeRun.setText(line);
                }
            }
        }
    }

    private static void writeImageSection(XWPFDocument document, Path imageDir) throws IOException {
        // 添加标题段落
        XWPFParagraph title = document.createParagraph();
        title.setAlignment(ParagraphAlignment.LEFT);
        XWPFRun titleRun = title.createRun();
        titleRun.setText("实验运行结果:");
        titleRun.setBold(true);
        titleRun.setFontSize(14);

        // 获取所有图片文件并排序
        File[] imageFiles = imageDir.toFile().listFiles((dir, name) ->
                name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg"));

        StringBuilder imageNames = new StringBuilder();
        if (imageFiles != null) {
            Arrays.sort(imageFiles, Comparator.comparing(File::getName));

            for (int i = 0; i < imageFiles.length; i++) {
                if (i > 0) imageNames.append("，");
                imageNames.append(imageFiles[i].getName());
            }
        }

        // 添加图片名称列表
        XWPFParagraph namesPara = document.createParagraph();
        XWPFRun namesRun = namesPara.createRun();
        namesRun.setText(imageNames.toString() + " 如图一、二、……所示");

        // 添加图片和图注
        if (imageFiles != null) {
            Arrays.sort(imageFiles, Comparator.comparing(File::getName));

            for (int i = 0; i < imageFiles.length; i++) {
                File imageFile = imageFiles[i];

                // 添加空段落
                document.createParagraph();

                // 添加图片段落（居中）
                XWPFParagraph imagePara = document.createParagraph();
                imagePara.setAlignment(ParagraphAlignment.CENTER);
                XWPFRun imageRun = imagePara.createRun();

                // 插入图片
                try (FileInputStream fis = new FileInputStream(imageFile)) {
                    imageRun.addPicture(fis,
                            getImageFormat(imageFile.getName()),
                            imageFile.getName(),
                            Units.toEMU(400),
                            Units.toEMU(200));
                } catch (Exception e) {
                    // 如果无法插入图片，则添加文字说明
                    imageRun.setText("[无法加载图片: " + imageFile.getName() + "]");
                }

                // 添加图注段落（居中）
                XWPFParagraph captionPara = document.createParagraph();
                captionPara.setAlignment(ParagraphAlignment.CENTER);
                XWPFRun captionRun = captionPara.createRun();
                captionRun.setText("图" + convertToChineseNumeral(i+1));
            }
        }
    }

    private static int getImageFormat(String imageName) {
        String lowerName = imageName.toLowerCase();
        if (lowerName.endsWith(".png")) {
            return XWPFDocument.PICTURE_TYPE_PNG;
        } else if (lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg")) {
            return XWPFDocument.PICTURE_TYPE_JPEG;
        } else {
            return XWPFDocument.PICTURE_TYPE_PNG; // 默认
        }
    }

    private static String convertToChineseNumeral(int number) {
        String[] chineseNumerals = {"零", "一", "二", "三", "四", "五", "六", "七", "八", "九", "十"};
        if (number <= 10) {
            return chineseNumerals[number];
        } else if (number < 20) {
            return "十" + (number % 10 == 0 ? "" : chineseNumerals[number % 10]);
        } else {
            return String.valueOf(number); // 对于大于19的数字，使用阿拉伯数字
        }
    }
}