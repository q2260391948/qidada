package com.xiaozhang.qidada.service;

import com.xiaozhang.qidada.service.impl.QrCodeServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

@SpringBootTest
public class QrCodeServiceTest {

    @Resource
    private QrCodeService qrCodeService;

    @Test
    public void testGenerateQrCodeDocument() throws Exception {
        // 创建一个模拟的 HttpServletResponse
        MockHttpServletResponse response = new MockHttpServletResponse();
        
        // 测试URL
        String testUrl = "https://www.example.com/test";
        
        // 调用服务生成文档
        qrCodeService.generateQrCodeDocument(testUrl, response);
        
        // 获取生成的文档内容
        byte[] documentContent = response.getContentAsByteArray();
        
        // 确保文档内容不为空
        assert documentContent != null && documentContent.length > 0;
        
        // 创建输出目录（如果不存在）
        String outputDir = "generated-docs";
        Files.createDirectories(Paths.get(outputDir));
        
        // 将文档保存到本地文件
        String filePath = outputDir + File.separator + "test-qrcode.docx";
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(documentContent);
        }
        
        // 验证文件是否成功创建
        File generatedFile = new File(filePath);
        assert generatedFile.exists();
        System.out.println("文档已生成在: " + generatedFile.getAbsolutePath());
    }
} 