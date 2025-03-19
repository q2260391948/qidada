package com.xiaozhang.qidada.service.impl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.xiaozhang.qidada.service.QrCodeService;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

@Service
public class QrCodeServiceImpl implements QrCodeService {

    @Override
    public void generateQrCodeDocument(String url, HttpServletResponse response) throws Exception {
        // 1. 生成二维码图片
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(url, BarcodeFormat.QR_CODE, 200, 200);
        BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
        
        // 将二维码图片转换为字节数组
        ByteArrayOutputStream qrImageBytes = new ByteArrayOutputStream();
        ImageIO.write(qrImage, "PNG", qrImageBytes);
        
        // 2. 创建Word文档
        XWPFDocument document = new XWPFDocument();
        
        // 添加标题
        XWPFParagraph titleParagraph = document.createParagraph();
        XWPFRun titleRun = titleParagraph.createRun();
        titleRun.setText("分享文档");
        titleRun.setFontSize(20);
        titleRun.setBold(true);
        
        // 添加二维码图片
        XWPFParagraph imageParagraph = document.createParagraph();
        XWPFRun imageRun = imageParagraph.createRun();
        imageRun.addPicture(
            new ByteArrayInputStream(qrImageBytes.toByteArray()),
            XWPFDocument.PICTURE_TYPE_PNG,
            "QR Code",
            Units.toEMU(200),
            Units.toEMU(200)
        );
        
        // 添加URL文本
        XWPFParagraph urlParagraph = document.createParagraph();
        XWPFRun urlRun = urlParagraph.createRun();
        urlRun.setText("分享链接: " + url);
        
        // 3. 设置响应头
        response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        response.setHeader("Content-Disposition", "attachment; filename=shared-qrcode.docx");
        
        // 4. 输出文档
        document.write(response.getOutputStream());
        document.close();
    }
} 