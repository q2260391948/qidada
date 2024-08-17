<template>
  <div class="layout-demo">
    <a-layout style="height: 100vh">
      <a-layout-header class="header">
        <a-space>
          <div class="titleBar" style="text-align: center">
            <img class="logo" src="../assets/logo.png" alt="" />
            <div class="logo-text">答题哟</div>
          </div>
        </a-space>
      </a-layout-header>
      <a-layout-content class="content">
        <a-space direction="vertical" size="large" :style="{ width: '600px' }">
          <a-tabs default-active-key="1">
            <a-tab-pane key="1" title="登录">
              <a-form-item field="name" label="用户名">
                <a-input v-model="form.name" placeholder="请输入用户名" />
              </a-form-item>
              <a-form-item field="password" label="密码">
                <a-input v-model="form.password" placeholder="请输入密码" />
              </a-form-item>
              <a-form-item>
                <a-button type="primary" @click="login">登录</a-button>
              </a-form-item>
            </a-tab-pane>
            <a-tab-pane key="2" title="注册">
              <a-form-item field="name" label="用户名">
                <a-input v-model="form.name" placeholder="请输入用户名" />
              </a-form-item>
              <a-form-item field="password" label="密码">
                <a-input v-model="form.password" placeholder="请输入密码" />
              </a-form-item>
              <a-form-item field="password" label="确认密码">
                <a-input
                  v-model="form.repassword"
                  placeholder="请重新输入密码"
                />
              </a-form-item>
              <a-form-item>
                <a-button type="primary">注册</a-button>
              </a-form-item>
            </a-tab-pane>
          </a-tabs>
        </a-space>
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

const userInfo = useLoginStore();
const layout = ref("horizontal");
const form = reactive({
  name: "",
  password: "",
  repassword: "",
});

const login = () => {
  console.log(form);
  let data = {
    userAccount: form.name,
    userPassword: form.password,
  };
  userLoginUsingPost(data).then((res) => {
    if (res.data.code == 0) {
      window.location.href = "/";
    }
  });
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
  text-align: center;
  background: linear-gradient(to right, #fefefe, #fff);
}

.footer {
  padding: 16px;
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
