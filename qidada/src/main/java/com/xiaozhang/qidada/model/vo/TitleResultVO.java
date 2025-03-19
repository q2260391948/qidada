package com.xiaozhang.qidada.model.vo;

import cn.hutool.json.JSONUtil;
import com.xiaozhang.qidada.model.dto.question.QuestionContentDTO;
import com.xiaozhang.qidada.model.entity.Question;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 题目视图
 *
 */
@Data
public class TitleResultVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 题目
     */
    private String title;

    /**
     * 答案描述
     */
    private String result;

}
