<template>
  <a-row class="grid-demo">
    <a-col :span="22">
      <a-menu
        mode="horizontal"
        :selected-keys="menu"
        @menu-item-click="doMeauOnclick"
      >
        <a-menu-item
          key="0"
          :style="{ padding: 0, marginRight: '100px' }"
          disabled
        >
          <div class="titleBar">
            <img class="logo" src="../assets/logo.png" alt="" />
            <div class="logo-text">AI答题网</div>
          </div>
        </a-menu-item>
        <a-menu-item v-for="item in visibleMeau" :key="item.path"
          >{{ item.name }}
        </a-menu-item>
      </a-menu>
    </a-col>
    <a-col :span="2" style="margin-top: 1%">
      <a-button v-if="userInfo.loginUser.id" type="primary"
        >{{ userInfo.loginUser.userName ?? "无名" }}
      </a-button>
      <a-button v-else type="primary" href="/user/login">游客 </a-button>
    </a-col>
  </a-row>
</template>

<script setup lang="ts">
//引入路由页面
import { routes } from "@/router/routes";
import { useRouter } from "vue-router";
import { computed, ref } from "vue";
import { useLoginStore } from "@/store/userStore";
import checkAccess from "@/access/checkAccess";
const route = useRouter();
//获取用户信息
const userInfo = useLoginStore();
userInfo.fetchLoginUser();
//定义响应式菜单
const menu = ref(["/"]);
//路由跳转时候更改菜单项
route.afterEach((to) => {
  // console.log(to, "to");
  menu.value = [to.path];
});
//获取未隐藏的菜单 进入页面就加载
const visibleMeau = computed(() => {
  return routes.filter((item) => {
    console.log(item, "item");
    if (item.meta && item.meta.hidden) {
      return false;
    }
    // 根据权限过滤菜单
    console.log(userInfo.loginUser, "userInfo.loginUser");
    if (!checkAccess(userInfo.loginUser, item.meta?.access as string)) {
      return false;
    }
    return true;
  });
});
//点击菜单跳转路由
const doMeauOnclick = (key: string) => {
  route.push({ path: key });
};
</script>
<style scoped>
.grid-demo {
  height: 48px;
  color: var(--color-white);
  line-height: 48px;
  text-align: center;
}

.titleBar {
  display: flex;
  align-items: center;
}

.logo {
  width: 48px;
  height: 48px;
}

.logo-text {
  margin-left: 16px;
}
</style>
