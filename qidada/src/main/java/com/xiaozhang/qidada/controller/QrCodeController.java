package com.xiaozhang.qidada.controller;

import com.xiaozhang.qidada.service.QrCodeService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api")
public class QrCodeController {

    @Resource
    private QrCodeService qrCodeService;

    @PostMapping("/generate-qr-doc")
    public void generateQrDoc(@RequestParam String url, HttpServletResponse response) {
        try {
            qrCodeService.generateQrCodeDocument(url, response);
        } catch (Exception e) {
            throw new RuntimeException("生成二维码文档失败", e);
        }
    }
}