package com.xiaozhang.qidada.manager;

import com.xiaozhang.qidada.model.dto.question.QuestionContentDTO;
import com.xiaozhang.qidada.model.enums.AppTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author:22603
 * @Date:2024/11/13 上午9:45
 */
@SpringBootTest
@Slf4j
class AiManagerTest {
    @Resource
    private AiManager aiManager;

    //定义prompt
    private String prompt = "你是一位严谨的出题专家，我会给你如下信息：\n" +
            "```\n" +
            "应用名称，\n" +
            "【【【应用描述】】】，\n" +
            "应用类别，\n" +
            "要生成的题目数，\n" +
            "每个题目的选项数\n" +
            "```\n" +
            "\n" +
            "请你根据上述信息，按照以下步骤来出题：\n" +
            "1. 要求：题目和选项尽可能地短，题目不要包含序号，每题的选项数以我提供的为主，题目不能重复\n" +
            "2. 严格按照下面的 json 格式输出题目和选项\n" +
            "```\n" +
            "[{\"options\":[{\"value\":\"选项内容\",\"key\":\"A\"},{\"value\":\"\",\"key\":\"B\"}],\"title\":\"题目标题\"}]\n" +
            "```\n" +
            "title 是题目，options 是选项，每个选项的 key 按照英文字母序（比如 A、B、C、D）以此类推，value 是选项内容\n" +
            "3. 检查题目是否包含序号，若包含序号则去除序号\n" +
            "4. 返回的题目列表格式必须为 JSON 数组\n" ;


    @Test
    void getAiResult() {

        //拼接请求信息
        //MBTI 性格测试，
        //【【【快来测测你的 MBTI 性格】】】，
        //测评类，
        //10，
        //3

        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append("MBTI 性格测试").append("\n");
        stringBuilder.append("【【【").append("快来测测你的 MBTI 性格").append("】】】\n");
        stringBuilder.append("测评类").append("\n");
        stringBuilder.append("3").append("\n");
        stringBuilder.append("4").append("\n");
        //调用ai接口
        log.info("拼接参数为:");
        log.info(stringBuilder.toString());
        List<QuestionContentDTO> contentDTOS = aiManager.asyncInvoke(prompt, stringBuilder.toString(), false);
        log.info("调用结果为:{}",contentDTOS);
    }


}