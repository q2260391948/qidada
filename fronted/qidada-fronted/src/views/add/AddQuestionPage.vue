<template>
  <div>
    <a-form :model="form" :style="{ width: '600px' }" @submit="handleSubmit">
      <a-form-item
        field="name"
        tooltip="Please enter username"
        label="Username"
      >
        <a-input
          v-model="form.name"
          placeholder="please enter your username..."
        />
      </a-form-item>
      <a-form-item field="post" label="Post">
        <a-input v-model="form.post" placeholder="please enter your post..." />
      </a-form-item>
      <a-form-item field="isRead">
        <a-checkbox v-model="form.isRead"> I have read the manual </a-checkbox>
      </a-form-item>
      <a-form-item>
        <a-button html-type="submit">Submit</a-button>
      </a-form-item>
    </a-form>
    {{ form }}
  </div>
</template>
<script setup lang="ts">
//钩子函数
import { ref, watchEffect } from "vue";
import { useLoginStore } from "@/store/userStore";
import { useRouter } from "vue-router";
import API from "@/api";
const router = useRouter();

const loginUserStore = useLoginStore();
let loginUser = loginUserStore.loginUser;

const form = ref({
  appId: "",
  createTime: "",
  id: "",
  questionContent: "",
  updateTime: "",
  user: "",
  userId: "",
} as API.QuestionVO);
const handleSubmit = (data) => {
  console.log(data);
};
watchEffect(() => {
  console.log("666666666666");
  if (loginUser.userName == "未登录" || loginUser.userName == "游客") {
    router.push("/user/login");
  }
});
</script>
<style scoped></style>
