package com.xiaozhang.qidada.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class QRCodeGenerator {
    public static void generateQRCode(String content, String path, int width, int height) throws Exception {
        // 设置二维码的相关参数
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        // 创建二维码矩阵
        BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);

        // 创建二维码图像
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, bitMatrix.get(x, y) ? Color.BLACK.getRGB() : Color.WHITE.getRGB());
            }
        }

        // 保存二维码图像到文件
        ImageIO.write(image, "PNG", new File(path));
    }

    public static void main(String[] args) {
        try {
            generateQRCode("https://www.example.com", "F:\\qrcode.png", 300, 300);
            System.out.println("二维码生成成功！");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
