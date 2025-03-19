package com.xiaozhang.qidada.manager;

import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.xiaozhang.qidada.exception.BusinessException;
import com.xiaozhang.qidada.model.dto.question.QuestionContentDTO;
import com.zhipu.oapi.ClientV4;
import com.zhipu.oapi.Constants;
import com.zhipu.oapi.service.v4.model.*;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

import static com.xiaozhang.qidada.common.ErrorCode.AIOPERATION_ERROR;

/**
 * @author:22603
 * @Date:2024/11/13 上午9:29
 */
@Component
@Data
public class AiManager {

    private static final Logger log = LoggerFactory.getLogger(AiManager.class);
    @Resource
    private ClientV4 client;

    // 稳定的随机数
    private static final float STABLE_TEMPERATURE = 0.05f;

    // 不稳定的随机数
    private static final float UNSTABLE_TEMPERATURE = 0.99f;


    private static @NotNull List<ChatMessage> getChatMessages(String systemText, String userText) {
        List<ChatMessage> messages = new ArrayList<>();
        if (StringUtils.isNotEmpty(systemText)) {
            ChatMessage systemMsg = new ChatMessage(ChatMessageRole.SYSTEM.value(), systemText);
            messages.add(systemMsg);
        }
        if (StringUtils.isNotEmpty(userText)) {
            ChatMessage chatMessage = new ChatMessage(ChatMessageRole.USER.value(), userText);
            messages.add(chatMessage);
        }
        return messages;
    }

    /**
     * 通用调用
     */
    public List<QuestionContentDTO> asyncInvoke(String systemText, String userText, Boolean isStream) {
        List<ChatMessage> messages = getChatMessages(systemText, userText);
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(Constants.ModelChatGLM4)
                .stream(isStream)
                .temperature(STABLE_TEMPERATURE)
                .invokeMethod(Constants.invokeMethod)
                .messages(messages)
                .build();
        try {
            ModelApiResponse invokeModelApiResp = client.invokeModelApi(chatCompletionRequest);
            if (invokeModelApiResp.isSuccess() && invokeModelApiResp.getCode() == 200) {
                ModelData data = invokeModelApiResp.getData();
                String content = (String) data.getChoices().get(0).getMessage().getContent();
                //根据[ 和 ] 截取响应内容
                int start = content.indexOf("[");
                int end = content.lastIndexOf("]")+1;
                content = content.substring(start, end);
                log.info("截取内容:{}",content);
                return JSONUtil.toList(content, QuestionContentDTO.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(AIOPERATION_ERROR.getCode(), e.getMessage());
        }
        return null;
    }


    /**
     * 默认同步调用
     */
    public List<QuestionContentDTO> defaultInvoke(String requestContent) {
        List<ChatMessage> chatMessages = getChatMessages(null, requestContent);
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(Constants.ModelChatGLM4)
                .stream(Boolean.FALSE)
                .invokeMethod(Constants.invokeMethod)
                .messages(chatMessages)
                .build();
        try {
            ModelApiResponse invokeModelApiResp = client.invokeModelApi(chatCompletionRequest);
            if (invokeModelApiResp.isSuccess() && invokeModelApiResp.getCode() == 200) {
                ModelData data = invokeModelApiResp.getData();
                String content = (String) data.getChoices().get(0).getMessage().getContent();
                //根据[ 和 ] 截取响应内容
                int start = content.indexOf("[");
                int end = content.indexOf("]") + 1;
                content = content.substring(start, end);
                return JSONUtil.toList(content, QuestionContentDTO.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(AIOPERATION_ERROR.getCode(), e.getMessage());
        }
        return null;
    }


}
