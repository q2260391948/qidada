<template>
  <a-space
    direction="vertical"
    :size="16"
    style="display: block; margin-top: 5%"
  >
    <a-row class="grid-demo" style="width: 100%">
      <a-col :span="4"></a-col>
      <a-col flex="auto" :span="8">
        <div class="cardStyle">
          <h1>应用详情</h1>
          <h2>应用名称：{{ appData.appName }}</h2>
          <p>应用描述：{{ appData.appDesc }}</p>
          <p>应用类型：{{ APP_TYPE_MAP[appData.appType] }}</p>
          <p>
            评分策略：{{ APP_SCORING_STRATEGY_MAP[appData.scoringStrategy] }}
          </p>
          <p>
            <a-space>
              作者：
              <div :style="{ display: 'flex', alignItems: 'center' }">
                <a-avatar
                  :size="24"
                  :image-url="appData.user?.userAvatar"
                  :style="{ marginRight: '8px' }"
                />
                <a-typography-text
                  >{{ appData.user?.userName ?? "无名" }}
                </a-typography-text>
              </div>
            </a-space>
          </p>
          <p>
            创建时间：{{
              dayjs(appData.createTime).format("YYYY-MM-DD HH:mm:ss")
            }}
          </p>
          <p>
            更新时间：{{
              dayjs(appData.updateTime).format("YYYY-MM-DD HH:mm:ss")
            }}
          </p>
        </div>
        <a-space class="cardStyle">
          <a-button type="primary" @click="toDoAnswer()">开始答题</a-button>
          <a-button v-if="isMyApp" @click="toAddQuestion()">设置题目</a-button>
          <a-button v-if="isMyApp" type="dashed" @click="toScoringResult()"
            >设置评分
          </a-button>
          <a-button v-if="isMyApp" type="outline" @click="toUpdateApp()"
            >修改应用
          </a-button>
          <a-button type="text">分享应用</a-button>
        </a-space>
      </a-col>
      <a-col :span="8">
        <a-image
          :src="appData.appIcon"
          :width="'100%'"
          :height="'auto'"
          :style="{
            maxWidth: '100%',
            maxHeight: '300px',
            objectFit: 'contain',
          }"
        />
      </a-col>
      <a-col :span="4"></a-col>
    </a-row>
  </a-space>
  <!--  {{ appData }}-->
</template>

<script setup lang="ts">
import { ref, withDefaults, defineProps, watchEffect, computed } from "vue";
import { getAppVoByIdUsingGet } from "@/api/appController";
import message from "@arco-design/web-vue/es/message";
import API from "@/api";
import { APP_SCORING_STRATEGY_MAP, APP_TYPE_MAP } from "../../constant/app";
import { useLoginStore } from "@/store/userStore";
import dayjs from "dayjs";
import { useRouter } from "vue-router";

const router = useRouter();

//接收路由里的参数
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
//获取应用信息
const appData = ref<API.AppVO>({});
const loadData = async () => {
  if (!props.id) {
    return;
  }
  const res = await getAppVoByIdUsingGet({
    id: props.id as any,
  });
  console.log(res, "res");
  if (res.data.code === 0) {
    appData.value = res.data.data as any;
  } else {
    message.error("获取数据失败，" + res.data.message);
  }
};
const toAddQuestion = () => {
  console.log(appData.value, "appData.value");
  window.sessionStorage.setItem(
    "appData:" + appData.value.id,
    JSON.stringify(appData.value)
  );
  router.push({
    path: `/add/question/${props.id}`,
  });
};
const toScoringResult = () => {
  router.push(`/add/scoringResult/${props.id}`);
};
const toDoAnswer = () => {
  router.push(`/do/answer/${props.id}`);
};

const toUpdateApp = () => {
  router.push(`/update/app/${props.id}`);
};
//确保登录用户信息和app信息更新时同步更新isMyApp的值
const isMyApp = computed(() => {
  return loginUser?.id == appData.value.userId;
});
watchEffect(() => {
  loadData();
});
const loginUserStore = useLoginStore();
let loginUser = loginUserStore.loginUser;
</script>

<style scoped>
.cardStyle {
  //margin-left: 30%; //width: 80%;
}
</style>
