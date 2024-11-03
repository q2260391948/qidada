<template>
  <a-form
    :model="app"
    :style="{
      width: '600px',
      marginTop: '100px',
      marginLeft: 'auto',
      marginRight: 'auto',
    }"
  >
    <h1>创建应用</h1>
    <a-form-item
      field="appName"
      label="应用名称"
      validate-trigger="input"
      required
    >
      <a-input v-model="app.appName" placeholder="请输入应用名称" />
    </a-form-item>
    <a-form-item
      field="appDesc"
      label="应用描述"
      validate-trigger="input"
      required
    >
      <a-textarea v-model="app.appDesc" placeholder="请输入应用描述" />
    </a-form-item>
    <a-form-item
      field="appIcon"
      label="应用图标"
      validate-trigger="input"
      required
    >
      <a-input v-model="app.appIcon" placeholder="请输入应用图标" />
    </a-form-item>
    <a-form-item
      field="appIcon"
      label="应用类型"
      validate-trigger="input"
      required
    >
      <a-select v-model="app.appType" placeholder="请选择应用类型">
        <a-option
          v-for="(appType, key) of APP_TYPE_MAP"
          :key="key"
          :value="Number(key)"
          :label="appType"
        />
      </a-select>
    </a-form-item>
    <a-form-item
      field="scoringStrategy"
      label="评分策略"
      validate-trigger="input"
      required
    >
      <a-select v-model="app.scoringStrategy" placeholder="请选择评分策略">
        <a-option
          v-for="(scoringStrategy, key) of APP_SCORING_STRATEGY_MAP"
          :key="key"
          :value="Number(key)"
          :label="scoringStrategy"
        />
      </a-select>
    </a-form-item>
    <a-form-item>
      <a-button type="primary" style="margin-left: 35%" @click="submit()"
        >提交</a-button
      >
    </a-form-item>
  </a-form>
</template>

<script setup lang="ts">
import {
  ref,
  reactive,
  onMounted,
  withDefaults,
  defineProps,
  watchEffect,
} from "vue";
import { useLoginStore } from "@/store/userStore";
import { useRouter } from "vue-router";
import API from "@/api";
import { APP_SCORING_STRATEGY_MAP, APP_TYPE_MAP } from "@/constant/app";
import { addAppUsingPost } from "@/api/appController";
import { Message } from "@arco-design/web-vue";

//接收路由里的参数
interface Props {
  id: string;
}

const app = ref({
  appDesc: "",
  appIcon: "",
  appName: "",
  appType: 0,
  scoringStrategy: 0,
} as API.AppAddRequest);
//拿到用户信息
const loginUserStore = useLoginStore();
let loginUser = loginUserStore.loginUser;
//路由route
const useRoute = useRouter();
//从路由中加载参数
const props = withDefaults(defineProps<Props>(), {
  id: () => {
    return "";
  },
});

const submit = async () => {
  const res = await addAppUsingPost(app.value);
  console.log(res, "添加结果");
  if (res.data && res.data.code === 0) {
    Message.success("操作成功");
    //两秒后跳转
    setTimeout(() => {
      useRoute.push(`/app/detail/${res.data.data}`);
    }, 2000);
  } else {
    Message.error("操作失败，" + res.data.message);
  }
};

//钩子函数
watchEffect(() => {
  if (loginUser.userName == "未登录") {
    useRoute.push("/user/login");
  }
});
</script>

<style scoped></style>
