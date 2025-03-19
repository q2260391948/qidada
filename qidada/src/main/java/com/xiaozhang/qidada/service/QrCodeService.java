package com.xiaozhang.qidada.service;

import javax.servlet.http.HttpServletResponse;

public interface QrCodeService {
    void generateQrCodeDocument(String url, HttpServletResponse response) throws Exception;
} 