<template>
  <div class="layout-demo">
    <a-layout style="height: 100vh">
      <a-layout-header class="header" style="display: flex; justify-content: center; align-items: center; height: 64px;">
        <a-space style="margin-left: -30%">
          <div class="titleBar" style="display: flex; align-items: center; text-align: center;">
            <img class="logo" src="../assets/logo.png" alt="" style="margin-right: 8px;" />
            <div class="logo-text">AI答题网</div>
          </div>
        </a-space>
      </a-layout-header>

      <a-layout-content class="content" style="display: flex;width: 100%">
        <!-- 左侧 Banner 区域 -->
        <div style="flex:6; background-position: center;">
          <img src="../assets/260.png" style="height: 100% ; width:100%"  />
        </div>

        <!-- 右侧登录注册区域 -->
        <div style="flex: 4; display: flex; justify-content: center; align-items: center; padding: 20px;">
          <a-space direction="vertical" size="large" :style="{ width: '400px' }">
            <a-tabs default-active-key="1">
              <a-tab-pane key="1" title="登录">
                <a-form :model="form" :layout="layout">
                  <a-form-item field="name" label="用户名">
                    <a-input v-model="form.name" placeholder="请输入用户名" />
                  </a-form-item>
                  <a-form-item field="password" label="密码">
                    <a-input-password v-model="form.password" placeholder="请输入密码" />
                  </a-form-item>
                  <a-form-item>
                    <a-button type="primary" @click="login">登录</a-button>
                  </a-form-item>
                </a-form>
              </a-tab-pane>
              <a-tab-pane key="2" title="注册">
                <a-form :model="form" :layout="layout">
                  <a-form-item field="name" label="用户名">
                    <a-input v-model="form.name" placeholder="请输入用户名" />
                  </a-form-item>
                  <a-form-item field="password" label="密码">
                    <a-input v-model="form.password" placeholder="请输入密码" />
                  </a-form-item>
                  <a-form-item field="repassword" label="确认密码">
                    <a-input v-model="form.repassword" placeholder="请重新输入密码" />
                  </a-form-item>
                  <a-form-item>
                    <a-button type="primary">注册</a-button>
                  </a-form-item>
                </a-form>
              </a-tab-pane>
            </a-tabs>
          </a-space>
        </div>
      </a-layout-content>

      <a-layout-footer class="footer"> By 程序员小张</a-layout-footer>
    </a-layout>


    <br />
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from "vue";
import { userLoginUsingPost } from "@/api/userController";
import { useLoginStore } from "@/store/userStore";
import { Message } from "@arco-design/web-vue";
import { useRouter } from "vue-router";
const router = useRouter();
const userInfo = useLoginStore();
const layout = ref("horizontal");
const form = reactive({
  name: "",
  password: "",
  repassword: "",
});

const login = async () => {
  console.log(form);
  let data = {
    userAccount: form.name,
    userPassword: form.password,
  };
  const res = await userLoginUsingPost(data);
  if (res.data.code === 0) {
    await userInfo.fetchLoginUser();
    Message.success("登录成功");
    await router.push({
      path: "/",
      replace: true,
    });
  } else {
    Message.error("登录失败，" + res.data.message);
  }
};
</script>
<style scoped>
#basicLayout .header {
  padding: 16px;
  text-align: center;
  box-shadow: #eee 1px 1px 5px;
}

.content {
  margin-bottom: 28px;
  padding: 20px;
  text-align: center;
  background: linear-gradient(to right, #fefefe, #fff);
}

.footer {
  margin-top: -2%;
  padding: 32px;
  text-align: center;
  background: #efefef;
}

.titleBar {
  display: flex;
  align-items: center;
  text-align: center;
}

.logo {
  width: 48px;
  height: 48px;
}

.logo-text {
  margin-left: 16px;
}
</style>
