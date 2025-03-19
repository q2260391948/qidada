package com.xiaozhang.qidada.scoring;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xiaozhang.qidada.manager.AiManager;
import com.xiaozhang.qidada.model.dto.question.QuestionContentDTO;
import com.xiaozhang.qidada.model.entity.App;
import com.xiaozhang.qidada.model.entity.Question;
import com.xiaozhang.qidada.model.entity.ScoringResult;
import com.xiaozhang.qidada.model.entity.UserAnswer;
import com.xiaozhang.qidada.model.vo.QuestionVO;
import com.xiaozhang.qidada.model.vo.TitleResultVO;
import com.xiaozhang.qidada.service.QuestionService;
import com.xiaozhang.qidada.service.ScoringResultService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 测评类应用AI评分策略
 *
 */
@ScoringStrategyConfig(appType = 1, scoringStrategy = 1)
public class AiScoreScoringStrategy implements ScoringStrategy {

    @Resource
    private QuestionService questionService;


    @Resource
    private AiManager aiManager;

    @Resource
    private ScoringResultService scoringResultService;

    private String systemPrompt="你是一位严谨的判题专家，我会给你如下信息：\n" +
            "```\n" +
            "应用名称，\n" +
            "【【【应用描述】】】，\n" +
            "题目和用户回答的列表：格式为 [{\"title\": \"题目\",\"answer\": \"用户回答\"}]\n" +
            "```\n" +
            "\n" +
            "请你根据上述信息，按照以下步骤来对用户进行评价：\n" +
            "1. 要求：需要给出一个明确的评价结果，包括评价名称（尽量简短）和评价描述（尽量详细，大于 200 字）\n" +
            "2. 严格按照下面的 json 格式输出评价名称和评价描述\n" +
            "```\n" +
            "{\"resultName\": \"评价名称\", \"resultDesc\": \"评价描述\"}\n" +
            "```\n" +
            "3. 返回格式必须为 JSON 对象\n" ;


    private String userPrompt="MBTI 性格测试，\n" +
            "【【【快来测测你的 MBTI 性格】】】，\n" +
            "[{\"title\": \"你通常更喜欢\",\"answer\": \"独自工作\"}, {\"title\": \"当安排活动时\",\"answer\": \"更愿意随机应变\"}]\n";



    @Override
    public UserAnswer doScore(List<String> choices, App app) throws Exception {
        Long appId = app.getId();
        // 1. 获取应用的所有问题
        List<Question> questions = questionService.list(Wrappers.<Question>lambdaQuery().eq(Question::getAppId, appId));
        Question question = questions.get(0);
        //2.获取所有题目和选项
        List<TitleResultVO> titleResultVOS=new ArrayList<>();
        List<QuestionContentDTO> contentDTOList = JSONUtil.toList(question.getQuestionContent(), QuestionContentDTO.class);
        for (int i = 0; i < choices.size(); i++) {
            String choice=choices.get(i);
            //标题
            String title = contentDTOList.get(i).getTitle();
            TitleResultVO resultVO=new TitleResultVO();
            resultVO.setTitle(title);
            resultVO.setResult(choice);
            titleResultVOS.add(resultVO);
        }


//        aiManager

        // 4. 构造返回值，填充答案对象的属性
        UserAnswer userAnswer = new UserAnswer();
//        userAnswer.setAppId(appId);
//        userAnswer.setAppType(app.getAppType());
//        userAnswer.setScoringStrategy(app.getScoringStrategy());
//        userAnswer.setChoices(JSONUtil.toJsonStr(choices));
//        userAnswer.setResultId(maxScoringResult.getId());
//        userAnswer.setResultName(maxScoringResult.getResultName());
//        userAnswer.setResultDesc(maxScoringResult.getResultDesc());
//        userAnswer.setResultPicture(maxScoringResult.getResultPicture());
//        userAnswer.setResultScore(totalScore);
        return userAnswer;
    }
}
