package com.xiaozhang.qidada.controller;

import com.xiaozhang.qidada.common.BaseResponse;
import com.xiaozhang.qidada.common.ErrorCode;
import com.xiaozhang.qidada.common.ResultUtils;
import com.xiaozhang.qidada.exception.ThrowUtils;
import com.xiaozhang.qidada.manager.AiManager;
import com.xiaozhang.qidada.model.dto.question.AiGenerateQuestionRequest;
import com.xiaozhang.qidada.model.dto.question.QuestionContentDTO;
import com.xiaozhang.qidada.model.entity.App;
import com.xiaozhang.qidada.model.enums.AppTypeEnum;
import com.xiaozhang.qidada.service.AppService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author:22603
 * @Date:2024/11/13 下午3:41
 */
@RestController
@Slf4j
@RequestMapping("/aiGenerateQuestion")
public class AiGenerateQuestionController {

    @Resource
    private AiManager aiManager;

    @Resource
    private AppService appService;

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


    @PostMapping("/ai_generate")
    public BaseResponse<List<QuestionContentDTO>> generateQuestion(@RequestBody AiGenerateQuestionRequest aiGenerateQuestionRequest){
        Long appId = aiGenerateQuestionRequest.getAppId();
        //判断请求参数是否为空
        ThrowUtils.throwIf(appId == null, ErrorCode.PARAMS_ERROR);
        //根据id查到app信息
        App app = appService.getById(appId);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR);
        //拼接请求信息
        //MBTI 性格测试，
        //【【【快来测测你的 MBTI 性格】】】，
        //测评类，
        //10，
        //3
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append(app.getAppName()).append("\n");
        stringBuilder.append("【【【").append(app.getAppDesc()).append("】】】\n");
        stringBuilder.append(AppTypeEnum.getEnumByValue(app.getAppType()).getText()).append("\n");
        stringBuilder.append(aiGenerateQuestionRequest.getQuestionNumber()).append("\n");
        stringBuilder.append(aiGenerateQuestionRequest.getOptionNumber()).append("\n");
        //调用ai接口
        log.info("拼接参数为:");
        log.info(stringBuilder.toString());
        List<QuestionContentDTO> contentDTOS = aiManager.asyncInvoke(prompt, stringBuilder.toString(), false);
        return ResultUtils.success(contentDTOS);
    }

}
