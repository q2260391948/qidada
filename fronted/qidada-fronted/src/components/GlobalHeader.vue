<template>
  <!-- 添加一个导航栏占位 -->
  <div class="header-placeholder"></div>
  <div class="header-container">
    <a-row class="grid-demo" align="center">
      <a-col :span="22">
        <a-menu
          mode="horizontal"
          :selected-keys="menu"
          @menu-item-click="doMeauOnclick"
        >
          <a-menu-item key="0" :style="{ marginRight: '100px' }" disabled>
            <div class="titleBar">
              <img class="logo" src="../assets/logo.png" alt="" />
              <div class="logo-text">AI答题网</div>
            </div>
          </a-menu-item>
          <a-menu-item v-for="item in visibleMeau" :key="item.path">
            {{ item.name }}
          </a-menu-item>
        </a-menu>
      </a-col>
      <a-col :span="2">
        <!-- 用户信息下拉菜单 -->
        <div class="user-section">
          <a-dropdown v-if="userInfo.loginUser.id" trigger="click">
            <a-button type="text">
              <a-avatar
                :size="32"
                :image-url="userInfo.loginUser.userAvatar || ''"
              >
                {{ userInfo.loginUser.userName?.[0] || "游" }}
              </a-avatar>
              <span class="username">{{
                userInfo.loginUser.userName || "无名"
              }}</span>
              <icon-down />
            </a-button>
            <template #content>
              <a-doption @click="showUserProfile">
                <icon-user /> 个人资料
              </a-doption>
              <a-doption @click="handleLogout">
                <icon-export /> 退出登录
              </a-doption>
            </template>
          </a-dropdown>
          <a-button v-else type="primary" @click="goToLogin">
            <icon-user /> 游客
          </a-button>
        </div>
      </a-col>
    </a-row>

    <!-- 个人资料抽屉 -->
    <a-drawer
      :visible="profileDrawerVisible"
      @cancel="profileDrawerVisible = false"
      @ok="updateUserProfile"
      :width="500"
      title="个人资料"
    >
      <a-form :model="userProfile" @submit="updateUserProfile">
        <a-form-item field="avatar" label="头像">
          <div class="avatar-upload">
            <a-avatar
              :size="80"
              :image-url="userProfile.userAvatar"
            />
            <a-upload>
              <a-button type="primary">更换头像</a-button>
            </a-upload>
          </div>
        </a-form-item>
        <a-form-item field="userName" label="用户名">
          <a-input v-model="userProfile.userName" placeholder="请输入用户名" />
        </a-form-item>
        <a-form-item field="email" label="邮箱">
          <a-input v-model="userProfile.email" placeholder="请输入邮箱" />
        </a-form-item>
        <a-form-item field="phone" label="手机号">
          <a-input v-model="userProfile.phone" placeholder="请输入手机号" />
        </a-form-item>
      </a-form>
    </a-drawer>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from "vue";
import { useRouter } from "vue-router";
import { routes } from "@/router/routes";
import { useLoginStore } from "@/store/userStore";
import checkAccess from "@/access/checkAccess";
import { Message } from "@arco-design/web-vue";
import { IconUser, IconExport, IconDown } from "@arco-design/web-vue/es/icon";
import { updateUserUsingPost } from "@/api/userController";

const router = useRouter();
const userInfo = useLoginStore();
const menu = ref(["/"]);
const profileDrawerVisible = ref(false);

// 用户资料表单数据
const userProfile = ref({
  id:userInfo.loginUser.id,
  userName: userInfo.loginUser.userName,
  userAvatar: userInfo.loginUser.userAvatar,
  email: userInfo.loginUser.email,
  phone: userInfo.loginUser.phone,
});

// 路由跳转时更改菜单项
router.afterEach((to) => {
  menu.value = [to.path];
});

// 获取未隐藏的菜单
const visibleMeau = computed(() => {
  return routes.filter((item) => {
    if (item.meta?.hidden) {
      return false;
    }
    if (!checkAccess(userInfo.loginUser, item.meta?.access as string)) {
      return false;
    }
    return true;
  });
});

// 点击菜单跳转路由
const doMeauOnclick = (key: string) => {
  router.push({ path: key });
};

// 跳转到登录页
const goToLogin = () => {
  router.push("/user/login");
};

// 显示个人资料抽屉
const showUserProfile = () => {
  // 重置表单数据
  userProfile.value = {
    id:userInfo.loginUser.id,
    userName: userInfo.loginUser.userName,
    userAvatar: userInfo.loginUser.userAvatar,
    email: userInfo.loginUser.email,
    phone: userInfo.loginUser.phone,
  };
  profileDrawerVisible.value = true;
};

// 更新用户资料
const updateUserProfile = async () => {
  try {
    const res = await updateUserUsingPost(userProfile.value);
    if (res.data.code === 0) {
      Message.success("个人资料更新成功");
      userInfo.fetchLoginUser(); // 刷新用户信息
      profileDrawerVisible.value = false;
    } else {
      Message.error("更新失败：" + res.data.message);
    }
  } catch (error) {
    Message.error("更新失败，请重试");
  }
};

// 退出登录
const handleLogout = async () => {
  try {
    await userInfo.userLogout();
    Message.success("退出成功");
    router.push("/user/login");
  } catch (error) {
    Message.error("退出失败，请重试");
  }
};
</script>

<style scoped>
/* 添加占位样式 */
.header-placeholder {
  width: 100%;
  height: 56px;
}

.header-container {
  position: fixed;
  top: 0;
  z-index: 100;
  width: 100%;
  background-color: var(--color-bg-2);
  border-bottom: 1px solid var(--color-neutral-3);
}

.grid-demo {
  height: 56px;
  line-height: 56px;
  text-align: center;
}

.titleBar {
  display: flex;
  align-items: center;
  height: 56px;
}

.logo {
  width: 40px;
  height: 40px;
}

.logo-text {
  margin-left: 12px;
  font-weight: 500;
  font-size: 18px;
}

.user-section {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  height: 56px;
}

.username {
  margin: 0 4px;
  color: var(--color-text-1);
}

.avatar-upload {
  display: flex;
  gap: 16px;
  align-items: center;
}

:deep(.arco-drawer-header) {
  border-bottom: 1px solid var(--color-neutral-3);
}

:deep(.arco-drawer-footer) {
  border-top: 1px solid var(--color-neutral-3);
}

/* 修改菜单样式覆盖 */
:deep(.arco-menu-horizontal .arco-menu-inner) {
  padding: 0;
  overflow: hidden; /* 防止出现滚动条 */
}

:deep(.arco-menu-horizontal) {
  overflow: hidden; /* 防止出现滚动条 */
}

/* 如果需要调整菜单项的间距，可以单独设置 */
:deep(.arco-menu-item) {
  padding: 0 20px;
}

/* 为了保持 logo 区域的样式，可以单独设置 */
:deep(.arco-menu-item:first-child) {
  margin-right: 100px;
  padding: 0;
}

/* 移除菜单边框 */
:deep(.arco-menu) {
  border: none !important;
}

:deep(.arco-menu-horizontal) {
  border: none !important;
}

:deep(.arco-menu-light) {
  border: none !important;
}

:deep(.arco-menu-item) {
  border: none !important;
}
</style>
