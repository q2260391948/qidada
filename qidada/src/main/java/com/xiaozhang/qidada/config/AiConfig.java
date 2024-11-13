package com.xiaozhang.qidada.config;

import com.zhipu.oapi.ClientV4;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author:22603
 * @Date:2024/11/13 上午9:21
 */
@Configuration
@ConfigurationProperties(prefix = "ai")
@Data
public class AiConfig {

    private String apiKey;

    /**
     * 初始化生成Bean对象
     * @return
     */
    @Bean
    public ClientV4 initClient() {
        return new ClientV4.Builder(apiKey).build();
    }

}
