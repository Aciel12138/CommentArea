package org.example.commentarea.service;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ReportServiceTest {

    @Autowired
    private ReportService reportService;

    @Test
    public void testProcessZipAndGenerateDocx() throws Exception {
        // Load test zip file from resources
        ClassPathResource resource = new ClassPathResource("多进程的实现.zip");
        InputStream inputStream = resource.getInputStream();
        
        // Create MultipartFile from the zip file
        MultipartFile multipartFile = new MockMultipartFile(
                "file",
                "多进程的实现.zip",
                "application/zip",
                inputStream
        );

        // Create mock HTTP response
        MockHttpServletResponse response = new MockHttpServletResponse();
        
        // Generate report
        reportService.processZipAndGenerateDocx(multipartFile, response);
        
        // Check that response has content
        assertNotNull(response.getContentAsByteArray());
        assertTrue(response.getContentAsByteArray().length > 0);
        
        // Check response headers
        assertEquals("application/vnd.openxmlformats-officedocument.wordprocessingml.document", 
                     response.getContentType());
        assertEquals("attachment; filename=experiment_report.docx", 
                     response.getHeader("Content-Disposition"));
        
        // Parse the result as a DOCX document
        try (XWPFDocument document = new XWPFDocument(new java.io.ByteArrayInputStream(response.getContentAsByteArray()))) {
            List<XWPFParagraph> paragraphs = document.getParagraphs();
            
            // Check that document has content
            assertFalse(paragraphs.isEmpty());
            
            // Check that document contains some expected content
            boolean hasCodeSection = false;
            boolean hasImageSection = false;
            
            for (XWPFParagraph paragraph : paragraphs) {
                String text = paragraph.getText();
                if (text != null && text.contains("实验代码")) {
                    hasCodeSection = true;
                }
                if (text != null && text.contains("实验运行结果")) {
                    hasImageSection = true;
                }
            }
            
            // At least one section should be present
            assertTrue(hasCodeSection || hasImageSection, "Document should contain either code or image section");
        }
        
        // Close the input stream
        inputStream.close();
    }
}