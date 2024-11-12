<template>
  <a-row class="grid-demo" style="margin-top: 6%">
    <a-col :span="6"></a-col>
    <a-col :span="12">
      <a-card :bordered="false" :style="{ width: '100%' }">
        <!-- 进度展示区域 -->
        <div class="progress-section">
          <a-progress
              type="circle"
              :percent="progressPercent"
              :style="{ width: '120px' }"
              :color="{
              '0%': 'rgb(var(--primary-6))',
              '100%': 'rgb(var(--success-6))',
            }"
          />
          <div class="progress-info">
            <span class="progress-text">当前进度</span>
            <span class="progress-count">
              {{ answeredCount }}/{{ totalQuestions }}
            </span>
          </div>
        </div>

        <!-- 题目内容 -->
        <div class="question-content" v-if="currentQuestion">
          <h2 class="question-title">
            {{ currentIndex + 1 }}. {{ currentQuestion.title }}
          </h2>

          <!-- 选项列表 -->
          <a-radio-group v-model="selectedAnswer" class="options-list">
            <a-radio
                v-for="(option, index) in currentQuestion.options"
                :key="index"
                :value="option.key"
                class="option-item"
            >
              {{ option.key }}. {{ option.value }}
            </a-radio>
          </a-radio-group>
        </div>

        <!-- 操作按钮 -->
        <div class="action-buttons">
          <a-button
              type="secondary"
              @click="prevQuestion"
              :disabled="currentIndex === 0"
          >
            上一题
          </a-button>
          <a-button
              type="primary"
              @click="nextQuestion"
              v-if="currentIndex < totalQuestions - 1"
              :disabled="!canNext"
          >
            下一题
          </a-button>
          <a-button
              type="primary"
              status="success"
              @click="submitAnswers"
              v-else
              :disabled="!canSubmit"
          >
            提交答案
          </a-button>
        </div>
      </a-card>
    </a-col>
    <a-col :span="6"></a-col>
  </a-row>
</template>

<script setup lang="ts">
import {ref, withDefaults, defineProps, watchEffect, computed} from "vue";
import {getQuestionByAppIdUsingGet} from "@/api/questionController";
import API from "@/api";
import {Message} from "@arco-design/web-vue";
import {addUserAnswerUsingPost} from "@/api/userAnswerController";
import {useRouter} from "vue-router";
const router = useRouter();
interface Props {
  id: string;
}

const props = withDefaults(defineProps<Props>(), {
  id: () => {
    return "";
  },
});

interface QuestionOption {
  key: string;
  value: string;
  score?: number; // score 字段不会展示
}

interface QuestionContentDTO {
  title: string;
  options: QuestionOption[];
}

const questionContent = ref<QuestionContentDTO[]>([
  {
    options: [],
    title: "",
  },
]);

// 当前题目索引
const currentIndex = ref(0);
// 选中的答案
const selectedAnswer = ref("");
// 所有答案
const answers = ref<string[]>([]);

// 计算属性
const currentQuestion = computed(
    () => questionContent.value[currentIndex.value]
);
const totalQuestions = computed(() => questionContent.value.length);
const answeredCount = computed(() => {
  return answers.value.filter((answer) => answer !== "").length;
});
const progressPercent = computed(() => {
  // 如果当前题目已选择答案，计入进度
  const currentAnswered = selectedAnswer.value ? 1 : 0;
  const previousAnswered = answers.value
      .slice(0, currentIndex.value)
      .filter((answer) => answer !== "").length;

  return (previousAnswered + currentAnswered) / totalQuestions.value;
});

// 添加提交按钮状态控制
const canSubmit = computed(() => {
  // 检查是否所有题目都已答（包括当前题目）
  const allPreviousAnswered = answers.value
      .slice(0, currentIndex.value)
      .every((answer) => answer !== "");
  return allPreviousAnswered && selectedAnswer.value !== "";
});

const canNext = computed(() => {
  // 检查当前题目是否已选择答案
  return selectedAnswer.value !== "";
});

// 加载题目数据
watchEffect(async () => {
  if (!props.id) return;

  const res = await getQuestionByAppIdUsingGet({
    appId: props.id as any,
  });

  if (res.data.code === 0 && res.data.data?.questionContent) {
    questionContent.value = JSON.parse(res.data.data.questionContent);
    // 初始化答案数组和当前选择
    answers.value = new Array(questionContent.value.length).fill("");
    selectedAnswer.value = "";
    currentIndex.value = 0;
  } else {
    Message.error("题目加载异常");
  }
});

// 上一题
const prevQuestion = () => {
  if (currentIndex.value > 0) {
    // 保存当前答案的key值
    answers.value[currentIndex.value] = selectedAnswer.value;
    currentIndex.value--;
    // 恢复上一题的答案
    selectedAnswer.value = answers.value[currentIndex.value] || "";
  }
};

// 下一题
const nextQuestion = () => {
  if (currentIndex.value < totalQuestions.value - 1) {
    // 保存当前答案的key值
    answers.value[currentIndex.value] = selectedAnswer.value;
    currentIndex.value++;
    // 恢复下一题的答案
    selectedAnswer.value = answers.value[currentIndex.value] || "";
  }
};

// 提交答案
const submitAnswers = async () => {
  // 保存最后一题的答案
  answers.value[currentIndex.value] = selectedAnswer.value;

  // 再次检查所有题目是否已答
  if (answers.value.some((answer) => !answer)) {
    Message.warning("请确保所有题目都已作答");
    return;
  }

  // TODO: 调用提交答案接口
  console.log("提交的答案", answers.value)
  const res = await addUserAnswerUsingPost({
    appId: props.id as any,
    choices: answers.value
  })
  if (res.data.code==0){
    Message.success("答案提交成功！");
    await router.push(`/result/answer/${res.data.data}`);
  }else {
    Message.error("答案提交异常！");
  }
};

// 添加获取选项值的工具函数
const getOptionValueByKey = (
    options: QuestionOption[],
    key: string
): string => {
  const option = options.find((opt) => opt.key === key);
  return option ? option.value : "";
};

const getOptionKeyByValue = (
    options: QuestionOption[],
    value: string
): string => {
  const option = options.find((opt) => opt.value === value);
  return option ? option.key : "";
};
</script>

<style scoped>
.progress-section {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 20px;
  padding: 20px 0 40px;
  border-bottom: 1px solid var(--color-neutral-3);
}

.progress-info {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  margin-left: 24px;
}

.progress-text {
  margin-bottom: 4px;
  color: var(--color-text-2);
  font-size: 16px;
}

.progress-count {
  color: rgb(var(--primary-6));
  font-weight: 600;
  font-size: 24px;
}

.question-content {
  min-height: 300px;
  padding: 20px 0;
}

.question-title {
  margin-bottom: 30px;
  font-weight: 500;
  font-size: 18px;
}

.options-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.option-item {
  padding: 10px;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.option-item:hover {
  background-color: #f5f5f5;
}

.action-buttons {
  display: flex;
  justify-content: space-between;
  margin-top: 40px;
}
</style>
