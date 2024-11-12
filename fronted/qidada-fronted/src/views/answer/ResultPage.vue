<template>
  <div class="result-page">
    <a-row>
      <a-col :span="6"></a-col>
      <a-col :span="12">
        <a-card class="result-card">
          <!-- 用户信息区域 -->
          <div class="user-info-section">
            <a-avatar
              :size="64"
              :image-url="resultData.user?.userAvatar"
            />
            <div class="user-details">
              <span class="username">{{
                resultData.user?.userName || "匿名用户"
              }}</span>
              <span class="user-id">ID: {{ resultData.userId }}</span>
            </div>
          </div>

          <!-- 结果标题区域 -->
          <div class="result-header">
            <h1 class="result-title">{{ resultData.resultName }}</h1>
            <div class="result-meta">
              <a-space>
                <a-tag>应用ID: {{ resultData.appId }}</a-tag>
                <a-tag color="blue">
                  应用类型: {{ getAppTypeText(resultData.appType) }}
                </a-tag>
                <a-tag color="green">
                  评分策略:
                  {{ getScoringStrategyText(resultData.scoringStrategy) }}
                </a-tag>
              </a-space>
            </div>
          </div>

          <!-- 分数展示区域 -->
          <div
            class="score-section"
            v-if="
              resultData.resultScore !== undefined &&
              resultData.resultScore !== null
            "
          >
            <div class="score-card">
              <a-progress
                type="circle"
                :percent="normalizedScore"
                :color="{
                  '0%': '#ff4d4f',
                  '50%': '#faad14',
                  '100%': '#52c41a',
                }"
                style="width: 120px"
              />
              <div class="score-details">
                <span class="score-value">{{ resultData.resultScore }}</span>
                <span class="score-label">总分</span>
                <span class="score-grade">{{
                  getScoreGrade(resultData.resultScore)
                }}</span>
              </div>
            </div>
          </div>

          <!-- 结果图片 -->
          <div class="result-image" v-if="resultData.resultPicture">
            <img :src="resultData.resultPicture" alt="结果图片" />
          </div>

          <!-- 结果描述 -->
          <div class="result-description">
            <div class="description-header">
              <icon-file-text /> 评测结果分析
            </div>
            <a-typography-paragraph>
              {{ resultData.resultDesc }}
            </a-typography-paragraph>
          </div>

          <!-- 答题详情 -->
          <div class="answer-details">
            <a-collapse>
              <a-collapse-item header="查看答题详情" key="1">
                <div class="choices-list">
                  <div
                    v-for="(choice, index) in resultData.choices"
                    :key="index"
                    class="choice-item"
                  >
                    <span class="choice-index">问题 {{ index + 1 }}:</span>
                    <span class="choice-value">{{ choice }}</span>
                  </div>
                </div>
              </a-collapse-item>
            </a-collapse>
          </div>

          <!-- 时间信息 -->
          <div class="time-info">
            <a-space direction="vertical" size="mini">
              <span class="time-label">
                <icon-clock-circle />
                完成时间：{{ formatDate(resultData.createTime) }}
              </span>
              <span class="time-label">
                <icon-sync />
                更新时间：{{ formatDate(resultData.updateTime) }}
              </span>
            </a-space>
          </div>

          <!-- 操作按钮 -->
          <div class="action-buttons">
            <a-space>
              <a-button type="primary" @click="retakeTest">重新测试</a-button>
              <a-button @click="shareResult">分享结果</a-button>
            </a-space>
          </div>
        </a-card>
      </a-col>
      <a-col :span="6"></a-col>
    </a-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from "vue";
import { useRoute, useRouter } from "vue-router";
import { Message } from "@arco-design/web-vue";
import { getUserAnswerVoByIdUsingGet } from "@/api/userAnswerController";
import {
  IconFileText,
  IconClockCircle,
  IconSync,
} from "@arco-design/web-vue/es/icon";

interface UserVO {
  // 根据实际情况定义用户字段
  id?: number;
  username?: string;
}

interface UserAnswerVO {
  appId?: number;
  appType?: number;
  choices?: string[];
  createTime?: string;
  id?: number;
  resultDesc?: string;
  resultId?: number;
  resultName?: string;
  resultPicture?: string;
  resultScore?: number;
  scoringStrategy?: number;
  updateTime?: string;
  user?: UserVO;
  userId?: number;
}

const route = useRoute();
const router = useRouter();
const resultData = ref<UserAnswerVO>({});

// 添加分数标准化计算
const normalizedScore = computed(() => {
  if (
    resultData.value.resultScore === undefined ||
    resultData.value.resultScore === null
  ) {
    return 0;
  }
  // 确保分数在 0-100 之间
  return Math.min(Math.max(Number(resultData.value.resultScore), 0), 100);
});

// 格式化日期
const formatDate = (dateString?: string) => {
  if (!dateString) return "";
  const date = new Date(dateString);
  return date.toLocaleString("zh-CN", {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
    hour: "2-digit",
    minute: "2-digit",
  });
};

// 获取结果数据
const fetchResultData = async () => {
  try {
    const answerId = route.params.id;
    const res = await getUserAnswerVoByIdUsingGet({
      id: Number(answerId),
    });

    if (res.data.code === 0 && res.data.data) {
      resultData.value = res.data.data;
      // 确保 resultScore 是数字类型
      if (
        resultData.value.resultScore !== undefined &&
        resultData.value.resultScore !== null
      ) {
        resultData.value.resultScore = Number(resultData.value.resultScore);
      }
    } else {
      Message.error("获取结果数据失败");
    }
  } catch (error) {
    Message.error("获取结果数据出错");
    console.error(error);
  }
};

// 重新测试
const retakeTest = () => {
  router.push(`/answer/${resultData.value.appId}`);
};

// 分享结果
const shareResult = () => {
  // 复制当前页面链接
  const url = window.location.href;
  navigator.clipboard.writeText(url).then(() => {
    Message.success("结果链接已复制到剪贴板");
  });
};

// 添加工具函数
const getAppTypeText = (type?: number) => {
  const typeMap: Record<number, string> = {
    0: "得分类",
    1: "测评类",
    // 添加更多类型
  };
  return typeMap[type as number] || "未知类型";
};

const getScoringStrategyText = (strategy?: number) => {
  const strategyMap: Record<number, string> = {
    0: "自定义",
    1: "AI",
    // 添加更多策略
  };
  return strategyMap[strategy as number] || "默认策略";
};

const getScoreGrade = (score?: number) => {
  if (score === undefined || score === null) return "未评分";
  if (score >= 90) return "优秀";
  if (score >= 80) return "良好";
  if (score >= 70) return "中等";
  if (score >= 60) return "及格";
  return "不及格";
};

onMounted(() => {
  fetchResultData();
});
</script>

<style scoped>
.result-page {
  min-height: 100vh;
  padding: 40px 0;
  background-color: var(--color-fill-2);
}

.result-card {
  padding: 24px;
  background-color: var(--color-bg-2);
  border-radius: 8px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

.result-header {
  margin-bottom: 32px;
  text-align: center;
}

.result-title {
  margin-bottom: 24px;
  color: var(--color-text-1);
  font-size: 28px;
}

.result-score {
  display: flex;
  flex-direction: column;
  gap: 16px;
  align-items: center;
}

.score-text {
  color: var(--color-text-1);
  font-weight: 500;
  font-size: 18px;
}

.result-image {
  margin: 24px 0;
  text-align: center;
}

.result-image img {
  max-width: 100%;
  max-height: 300px;
  border-radius: 8px;
}

.result-description {
  margin: 24px 0;
  padding: 20px;
  font-size: 16px;
  line-height: 1.6;
  background-color: var(--color-fill-2);
  border-radius: 8px;
}

.answer-details {
  margin: 24px 0;
}

.choices-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.choice-item {
  display: flex;
  gap: 12px;
  padding: 8px;
  background-color: var(--color-fill-2);
  border-radius: 4px;
}

.choice-index {
  min-width: 80px;
  color: var(--color-text-3);
}

.choice-value {
  color: var(--color-text-1);
  font-weight: 500;
}

.time-info {
  margin: 24px 0;
  color: var(--color-text-3);
  font-size: 14px;
}

.action-buttons {
  display: flex;
  gap: 16px;
  justify-content: center;
  margin-top: 32px;
}

.time-label {
  display: flex;
  gap: 8px;
  align-items: center;
}

/* 添加新的样式 */
.user-info-section {
  display: flex;
  gap: 16px;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 20px;
  border-bottom: 1px solid var(--color-neutral-3);
}

.user-details {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.username {
  color: var(--color-text-1);
  font-weight: 500;
  font-size: 18px;
}

.user-id {
  color: var(--color-text-3);
  font-size: 14px;
}

.result-meta {
  margin-top: 16px;
}

.score-section {
  display: flex;
  justify-content: center;
  margin: 32px 0;
}

.score-card {
  display: flex;
  gap: 24px;
  align-items: center;
  padding: 24px;
  background: var(--color-fill-2);
  border-radius: 8px;
}

.score-details {
  display: flex;
  flex-direction: column;
  gap: 8px;
  align-items: flex-start;
}

.score-value {
  color: rgb(var(--primary-6));
  font-weight: 600;
  font-size: 32px;
}

.score-label {
  color: var(--color-text-3);
  font-size: 14px;
}

.score-grade {
  padding: 4px 12px;
  color: var(--color-text-1);
  font-weight: 500;
  font-size: 16px;
  background: var(--color-fill-3);
  border-radius: 4px;
}

.description-header {
  display: flex;
  gap: 8px;
  align-items: center;
  margin-bottom: 16px;
  color: var(--color-text-1);
  font-weight: 500;
  font-size: 16px;
}
</style>
