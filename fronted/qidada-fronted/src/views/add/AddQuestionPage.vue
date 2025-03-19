<template>
  <div class="page-container">
    <!-- 顶部操作区 -->
    <div class="action-header">
      <div class="app-info">
        <h2>{{ appData.appName }}</h2>
        <p class="app-desc">{{ appData.appDesc }}</p>
      </div>
      <a-button type="primary" status="success" @click="showAIDrawer">
        <template #icon>
          <icon-robot />
        </template>
        AI生成题目
      </a-button>
    </div>

    <!-- 题目列表区域 -->
    <div class="questions-grid">
      <a-card
        v-for="(Content, index) in questionContent"
        :key="index"
        class="question-card"
        :title="`第${index + 1}道题目`"
        hoverable
      >
        <template #extra>
          <a-space>
            <a-button
              type="primary"
              v-if="index > 0"
              @click="removeQuestion(index)"
              size="small"
            >
              <template #icon>
                <icon-minus />
              </template>
            </a-button>
            <a-button type="primary" @click="addQuestion(index)" size="small">
              <template #icon>
                <icon-plus />
              </template>
            </a-button>
          </a-space>
        </template>

        <a-form-item field="title" label="题目内容">
          <a-input
            v-model="Content.title"
            placeholder="请输入题目内容"
            allow-clear
          />
        </a-form-item>

        <div class="options-header">
          <span class="options-title">选项列表</span>
          <a-space>
            <a-button type="outline" size="small" @click="addOptions(index)">
              <template #icon>
                <icon-plus />
              </template>
              添加选项
            </a-button>
            <a-button
              type="outline"
              status="danger"
              size="small"
              @click="deleteOpentins(index)"
            >
              <template #icon>
                <icon-delete />
              </template>
              删除选项
            </a-button>
          </a-space>
        </div>

        <div class="options-list">
          <div
            v-for="(option, optionIndex) in Content.options"
            :key="optionIndex"
            class="option-item"
          >
            <div class="option-header">
              <div class="option-index-group">
                <span class="option-index">选项 {{ optionIndex + 1 }}</span>
<!--                <a-input-->
<!--                  v-model="option.key"-->
<!--                  placeholder="选项序号（如：A、B、C）"-->
<!--                  class="option-key-input"-->
<!--                >-->
<!--                  <template #prefix>-->
<!--                    <icon-tag />-->
<!--                  </template>-->
<!--                </a-input>-->
              </div>
              <a-tag color="arcoblue">得分: {{ option.score || 0 }}</a-tag>
            </div>

            <div class="option-content">
              <!-- 选项内容和得分 -->
              <div class="option-row">
                <div class="input-group" style="width: 160px">
                  <div class="field-label">选项序号</div>
                  <a-input
                    v-model="option.key"
                    placeholder="选项序号（如：A、B、C）"
                    class="option-key-input"
                  >
                    <template #prefix>
                      <icon-tag />
                    </template>
                  </a-input>
                </div>
                <div class="input-group" style="width: 200px">
                  <div class="field-label">选项得分</div>
                  <a-input-number
                    v-model="option.score"
                    placeholder="0-100"
                    :min="0"
                    :max="100"
                    class="option-score-input"
                  >
                    <template #prefix>
                      <icon-star />
                    </template>
                  </a-input-number>
                </div>
              </div>

              <!-- 第二行：选项内容 -->
              <div class="input-group">
                <div class="field-label">选项内容</div>
                <a-textarea
                  v-model="option.value"
                  placeholder="请输入选项的具体内容描述"
                  :auto-size="{ minRows: 2, maxRows: 4 }"
                  class="option-result-input"
                  allow-clear
                />
              </div>

              <!-- 第三行：选项结果 -->
              <div class="input-group">
                <div class="field-label">选项结果描述</div>
                <a-textarea
                  v-model="option.result"
                  placeholder="请描述选择该选项后的结果分析"
                  :auto-size="{ minRows: 3, maxRows: 6 }"
                  class="option-result-input"
                  allow-clear
                />
              </div>
            </div>
          </div>
        </div>
      </a-card>
    </div>

    <!-- 底部提交按钮 -->
    <div class="submit-section">
      <a-button type="primary" size="large" @click="submit">
        保存题目配置
      </a-button>
    </div>

    <!-- AI生成抽屉 -->
    <a-drawer
      :visible="visible"
      @cancel="handleCancel"
      @ok="handleOk"
      :width="500"
      title="AI生成题目"
    >
      <template #title>
        <div style="display: flex; align-items: center">
          <icon-robot
            style="margin-right: 8px; font-size: 20px; color: #165dff"
          />
          <span>AI生成题目</span>
        </div>
      </template>

      <a-form :model="aiForm" layout="vertical">
        <a-form-item label="应用名称">
          <a-input disabled v-model="appData.appName" readonly />
        </a-form-item>
        <a-form-item label="应用描述">
          <a-textarea disabled v-model="appData.appDesc" readonly />
        </a-form-item>
        <a-form-item
          field="questionCount"
          label="题目数量"
          :rules="[{ required: true, message: '请输入题目数量' }]"
        >
          <a-input-number
            v-model="aiForm.questionCount"
            placeholder="请输入要生成的题目数量"
            :min="1"
            :max="10"
            style="width: 100%"
          />
        </a-form-item>
        <a-form-item
          field="optionCount"
          label="每题选项数量"
          :rules="[{ required: true, message: '请输入选项数量' }]"
        >
          <a-input-number
            v-model="aiForm.optionCount"
            placeholder="请输入每题的选项数量"
            :min="2"
            :max="6"
            style="width: 100%"
          />
        </a-form-item>
      </a-form>

      <template #footer>
        <a-space>
          <a-button @click="handleCancel">取消</a-button>
          <a-button type="primary" :loading="generating" @click="handleOk">
            {{ generating ? "生成中..." : "开始生成" }}
          </a-button>
        </a-space>
      </template>
    </a-drawer>
  </div>
</template>

<script setup lang="ts">
//钩子函数
import { ref, watchEffect } from "vue";
import { useLoginStore } from "@/store/userStore";
import { useRoute, useRouter } from "vue-router";
import API from "@/api";
import {
  IconPlus,
  IconDelete,
  IconMinus,
  IconRobot,
} from "@arco-design/web-vue/es/icon";
import {
  addQuestionUsingPost,
  editQuestionUsingPost,
  getQuestionByAppIdUsingGet,
  getQuestionVoByIdUsingGet,
} from "@/api/questionController";
import { defineProps, withDefaults } from "vue/dist/vue";
import { Message } from "@arco-design/web-vue";
import { generateQuestionUsingPost } from "@/api/aiGenerateQuestionController";

const router = useRouter();

interface Props {
  id: string;
}

// 使用withDefaults函数为defineProps函数定义的props添加默认值
const props = withDefaults(defineProps<Props>(), {
  // 为id属性添加默认值，默认值为空字符串
  id: () => {
    return "";
  },
});

const loginUserStore = useLoginStore();
let loginUser = loginUserStore.loginUser;
const questionId = ref("");
const appData = ref<API.AppVO>({});
const questionContent = ref<API.QuestionContentDTO[]>([
  {
    title: "",
    options: [
      {
        key: "A",
        value: "",
        score: 0,
        result: "",
      },
    ],
  },
]);
//添加题目
const addQuestion = (index: any) => {
  questionContent.value.splice(index + 1, 0, {
    title: "",
    options: [
      {
        key: "A",
        value: "",
        score: 0,
        result: "",
      },
    ],
  });
};

//添加选项
const addOptions = (index: any) => {
  questionContent.value[index].options?.push({
    key: "",
    value: "",
    score: 0,
    result: "",
  });
};

//删除题目
const removeQuestion = (index: any) => {
  questionContent.value.splice(index, 1);
};

//删选项
const deleteOpentins = (index: any) => {
  const options = questionContent.value[index].options;
  if (options && options.length > 1) {
    options.splice(options.length - 1, 1);
  } else {
    Message.warning("每道题目至少需要保留一个选项");
  }
};
//提交题目
const submit = async () => {
  //判断题目是否为空
  const isEmpty = questionContent.value.every((content) => {
    return !content.title && (!content.options || content.options.length === 0);
  });
  if (isEmpty) {
    Message.error("题目及选项不可为空");
    return;
  }
  let res;
  if (questionId.value && questionId.value != "") {
    res = await editQuestionUsingPost({
      id: questionId.value as any,
      questionContent: questionContent.value,
    });
  } else {
    res = await addQuestionUsingPost({
      appId: props.id as any,
      questionContent: questionContent.value,
    });
  }
  console.log(res.data, "题目提交结果");
  if (res.data.code == 0) {
    Message.success("题目配置成功!");
  }
};

watchEffect(async () => {
  const res = await getQuestionByAppIdUsingGet({
    appId: props.id as any,
  });
  if (res.data.code == 0) {
    questionContent.value = JSON.parse(res.data.data?.questionContent);
    console.log(questionContent.value,"题目")
    questionId.value = res.data.data?.id;
  }
  //从路由中拿到应用信息
  appData.value = JSON.parse(
    window.sessionStorage.getItem("appData:" + props.id)
  );
});

watchEffect(() => {
  if (loginUser.userName == "未登录" || loginUser.userName == "游客") {
    Message.info("未登录");
    router.push("/user/login");
  }
});

// 新增AI生成相关的响应式变量
const visible = ref(false);
const generating = ref(false);
const appInfo = ref({
  name: "",
  description: "",
});
const aiForm = ref({
  questionCount: 1,
  optionCount: 4,
});

// 显示抽屉
const showAIDrawer = () => {
  visible.value = true;
};

// 取消生成
const handleCancel = () => {
  visible.value = false;
};

// 确认生成
const handleOk = async () => {
  if (!aiForm.value.questionCount || !aiForm.value.optionCount) {
    Message.error("请填写完整的生成参数");
    return;
  }
  generating.value = true;
  try {
    const res = await generateQuestionUsingPost({
      appId: props.id as any,
      questionNumber: aiForm.value.questionCount,
      optionNumber: aiForm.value.optionCount,
    });
    if (res.data.code === 0 && res.data.data) {
      Message.success("题目生成成功！");
      // 正确解析和追加新生成的题目
      const newQuestions = Array.isArray(res.data.data)
        ? res.data.data
        : [res.data.data];
      questionContent.value = [...questionContent.value, ...newQuestions];
      visible.value = false;
    } else {
      Message.error("题目生成失败：" + (res.data.message || "未知错误"));
    }
  } catch (error) {
    Message.error("生成失败：" + error);
  } finally {
    generating.value = false;
  }
};
</script>

<style scoped>
.page-container {
  max-width: 1200px;
  margin: 24px auto;
  padding: 0 24px;
}

.action-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;
  padding: 16px 24px;
  background: var(--color-bg-2);
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.app-info {
  flex: 1;
}

.app-info h2 {
  margin: 0;
  color: var(--color-text-1);
  font-size: 20px;
}

.app-desc {
  margin: 8px 0 0;
  color: var(--color-text-3);
  font-size: 14px;
}

.questions-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(600px, 1fr));
  gap: 24px;
  margin-bottom: 24px;
}

.question-card {
  height: auto;
  min-height: 300px;
  background: var(--color-bg-2);
  border-radius: 8px;
  transition: transform 0.3s, box-shadow 0.3s;
}

.question-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  transform: translateY(-4px);
}

.options-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin: 16px 0;
  padding-bottom: 8px;
  border-bottom: 1px solid var(--color-neutral-3);
}

.options-title {
  color: var(--color-text-1);
  font-weight: 500;
}

.options-list {
  margin-top: 16px;
  padding: 16px;
  background: var(--color-fill-1);
  border-radius: 8px;
}

.option-item {
  margin-bottom: 16px;
  padding: 16px;
  background: var(--color-bg-2);
  border-radius: 8px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
}

.option-item:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  transform: translateY(-2px);
}

.option-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid var(--color-neutral-3);
}

.option-index-group {
  display: flex;
  gap: 12px;
  align-items: center;
}

.option-index {
  min-width: 60px;
  color: var(--color-text-1);
  font-weight: 500;
}

.option-key-input {
  width: 160px;
  height: 32px;
}

.option-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
  padding: 16px;
  background: var(--color-fill-1);
  border-radius: 8px;
}

.option-row {
  display: flex;
  gap: 16px;
  align-items: flex-start;
}

.input-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.field-label {
  color: var(--color-text-2);
  font-weight: 500;
  font-size: 14px;
}

.option-value-input,
.option-result-input {
  width: 100%;
  font-size: 14px;
  line-height: 1.6;
  background-color: var(--color-bg-2);
  border-radius: 4px;
}

:deep(.arco-textarea-wrapper) {
  min-height: auto !important;
  background-color: var(--color-bg-2);
  border: 1px solid var(--color-neutral-3);
  transition: all 0.2s;
}

:deep(.arco-textarea) {
  min-height: auto !important;
  padding: 8px 12px;
}

:deep(.arco-textarea-wrapper:hover) {
  border-color: rgb(var(--primary-6));
}

:deep(.arco-textarea-wrapper:focus-within) {
  border-color: rgb(var(--primary-6));
  box-shadow: 0 0 0 2px rgba(var(--primary-6), 0.1);
}

.options-list::-webkit-scrollbar {
  width: 8px;
}

.options-list::-webkit-scrollbar-thumb {
  background-color: var(--color-neutral-3);
  border-radius: 4px;
}

.options-list::-webkit-scrollbar-track {
  background-color: var(--color-neutral-1);
  border-radius: 4px;
}

.question-card {
  transition: all 0.3s ease;
}

.question-card:hover {
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.08);
  transform: translateY(-4px);
}

:deep(.arco-btn) {
  transition: all 0.3s ease;
}

:deep(.arco-btn:hover) {
  transform: translateY(-2px);
}

.submit-section {
  display: flex;
  justify-content: center;
  padding: 24px 0;
  background: var(--color-bg-2);
  border-radius: 8px;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.06);
}

:deep(.arco-drawer-header) {
  border-bottom: 1px solid var(--color-neutral-3);
}

:deep(.arco-drawer-footer) {
  padding: 16px 24px;
  border-top: 1px solid var(--color-neutral-3);
}

:deep(.arco-form-item-label) {
  font-weight: 500;
}

:deep(.arco-input-wrapper),
:deep(.arco-textarea-wrapper) {
  transition: all 0.3s ease;
}

:deep(.arco-input-wrapper:focus-within),
:deep(.arco-textarea-wrapper:focus-within) {
  transform: translateY(-1px);
}

:deep(.arco-textarea-wrapper),
:deep(.arco-input-wrapper),
:deep(.arco-input-number) {
  height: 36px !important;
}

:deep(.arco-textarea) {
  padding-top: 4px;
  padding-bottom: 4px;
  resize: none;
}

.option-value-input,
.option-score-input {
  display: flex;
  align-items: center;
  width: 100%;
}

:deep(.arco-textarea-word-limit) {
  margin-top: 4px;
  color: var(--color-text-3);
  font-size: 12px;
}

:deep(.arco-textarea)::-webkit-scrollbar {
  width: 4px;
}

:deep(.arco-textarea)::-webkit-scrollbar-thumb {
  background-color: var(--color-neutral-3);
  border-radius: 2px;
}

:deep(.arco-textarea)::-webkit-scrollbar-track {
  background-color: var(--color-neutral-1);
  border-radius: 2px;
}
</style>
