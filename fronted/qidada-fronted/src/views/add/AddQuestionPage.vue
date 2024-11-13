<template>
  <div style="margin-top: 5%; width: 80%; margin-left: 10%">
    <div style="margin-bottom: 20px; text-align: right">
      <a-button type="primary" status="success" @click="showAIDrawer">
        <template #icon>
          <icon-robot />
        </template>
        AI生成题目
      </a-button>
    </div>

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

    <div :style="{ display: 'flex', flexWrap: 'wrap' }">
      <div
        v-for="(Content, index) in questionContent"
        :key="index"
        :style="{ flexBasis: '23%', marginRight: '2%', marginBottom: '2%' }"
      >
        <a-card
          :style="{ width: '100%', height: '400px' }"
          :title="`第${index + 1}道题目`"
          hoverable
        >
          <template #extra>
            <!-- 第一个按钮，减号按钮，右边添加间距 -->
            <a-button
              type="primary"
              v-if="index > 0"
              @click="removeQuestion(index)"
              :style="{ marginRight: '8px' }"
            >
              <template #icon>
                <icon-minus />
              </template>
            </a-button>

            <!-- 第二个按钮，添加按钮 -->
            <a-button type="primary" @click="addQuestion(index)">
              <template #icon>
                <icon-plus />
              </template>
            </a-button>
          </template>
          <a-form-item
            :field="Content.title"
            tooltip="请输入题目"
            label="题目名称"
          >
            <a-input v-model="Content.title" placeholder="请输入题目" />
          </a-form-item>
          <a-space>
            <a-button type="primary" @click="addOptions(index)">
              <template #icon>
                <icon-plus />
              </template>
              <template #default>Add</template>
            </a-button>
            <a-button type="primary" @click="deleteOpentins(index)">
              <template #icon>
                <icon-delete />
              </template>
              <template #default>Delete</template>
            </a-button>
          </a-space>

          <!-- 设置选项列表容器的固定高度和滚动 -->
          <div
            :style="{
              maxHeight: '240px',
              overflowY: 'auto',
              marginTop: '10px',
            }"
          >
            <div
              v-for="(option, optionIndex) in Content.options"
              :key="optionIndex"
            >
              <a-space style="height: 40px; margin-top: 5%">
                <a-form-item
                  :field="option.key"
                  :label="`选项${optionIndex + 1}`"
                >
                  <a-input v-model="option.key" placeholder="请输入选项描述" />
                </a-form-item>
                <a-form-item field="option.value" label="选项值">
                  <a-input v-model="option.value" placeholder="请输入选项值" />
                </a-form-item>
              </a-space>
            </div>
          </div>
        </a-card>
        <!--        {{ questionContent }}-->
      </div>
    </div>
    <a-space style="display: flex; justify-content: center; width: 100%">
      <a-button type="primary" @click="submit()">提交</a-button>
    </a-space>
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
    options: [],
    title: "",
  },
]);
//添加题目
const addQuestion = (index: any) => {
  questionContent.value.splice(index + 1, 0, {
    title: "",
    options: [],
  });
};

//添加选项
const addOptions = (index: any) => {
  questionContent.value[index].options?.push({
    key: "",
    value: "",
  });
};

//删除题目
const removeQuestion = (index: any) => {
  questionContent.value.splice(index, 1);
};

//删除选项
const deleteOpentins = (index: any) => {
  questionContent.value[index].options?.splice(
    questionContent.value[index].options?.length - 1,
    1
  );
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
  if (res.data.code == 0 && res.data.data?.length > 0) {
    questionContent.value = JSON.parse(res.data.data?.questionContent);
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
    // TODO: 调用后端AI生成接口)
    const res = await generateQuestionUsingPost({
      appId: props.id as any,
      questionNumber: aiForm.value.questionCount,
      optionNumber: aiForm.value.optionCount,
    });
    if (res.data.code == 0 && res.data.data?.length > 0) {
      Message.success("题目生成成功！");
      //更新题目列表
      questionContent.value = res.data.data;
      visible.value = false;
    } else {
      Message.error("题目生成失败，请重试");
      visible.value = true;
    }
  } catch (error) {
    Message.error("题目生成失败，" + error);
  } finally {
    generating.value = false;
  }
};

// 获取应用信息
watchEffect(async () => {
  if (props.id) {
    // TODO: 调用获取应用详情接口
    // const res = await getAppDetail(props.id);
    // appInfo.value = res.data;

    // 临时模拟数据
    appInfo.value = {
      name: "示例应用",
      description: "这是一个示例应用的描述信息",
    };
  }
});
</script>
<style scoped>
.card-demo {
  width: 360px;
  margin-left: 24px;
  transition-property: all;
}

.card-demo:hover {
  transform: translateY(-4px);
}

.arco-drawer-header {
  border-bottom: 1px solid var(--color-neutral-3);
}

.arco-drawer-footer {
  padding: 16px 24px;
  border-top: 1px solid var(--color-neutral-3);
}
</style>
