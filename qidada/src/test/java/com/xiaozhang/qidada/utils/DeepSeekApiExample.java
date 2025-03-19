package com.xiaozhang.qidada.utils;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import java.io.IOException;

public class DeepSeekApiExample {

    public static void main(String[] args) {
        String apiEndpoint = "https://api.deepseek.com/chat/completions";
        String apiKey = "sk-03f25dfc8c454833bc1b645dd5905267"; // 替换为你的实际API Key

        // 创建JSON请求体
        String requestBody = "{" +
                "  \"model\": \"deepseek-chat\"," +
                "  \"messages\": [" +
                "    {" +
                "      \"role\": \"system\"," +
                "      \"content\": \"你是一个编程大师\" " +
                "    }," +
                "    {" +
                "      \"role\": \"user\"," +
                "      \"content\": \"我当初学习的是java，入职后如今却来做前端开发了，导致我java都有些忘记了，我现在不知道该怎么办，其实我的初心还是java开发!\" " +
                "    }" +
                "  ]," +
                "  \"stream\": false" +
                "}";

        try {
            // 创建请求
            String responseStr = HttpUtil.createPost(apiEndpoint)
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .body(requestBody)
                    .execute().toString();

            // 解析响应体中的JSON
            JSONObject response = JSONUtil.parseObj(responseStr);
            if (response.containsKey("choices") && !response.getJSONArray("choices").isEmpty()) {
                JSONObject choice = response.getJSONArray("choices").getJSONObject(0);
                if (choice.containsKey("message") && choice.getJSONObject("message").containsKey("content")) {
                    String content = choice.getJSONObject("message").getStr("content");
                    System.out.println(content);
                } else {
                    System.err.println("Content node not found in the response.");
                }
            } else {
                System.err.println("Choices node not found in the response.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
