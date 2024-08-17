<template>
  <div class="layout-demo">
    <a-layout style="height: 100vh">
      <a-layout-header class="header">
        <GlobalHeader />
      </a-layout-header>
      <a-layout-content class="content">
        <router-view />
      </a-layout-content>
      <a-layout-footer class="footer"> By 程序员小张</a-layout-footer>
    </a-layout>
    <br />
  </div>
</template>

<script setup lang="ts">
import GlobalHeader from "@/components/GlobalHeader.vue";
import { useLoginStore } from "@/store/userStore";

const userInfo = useLoginStore();
console.log(userInfo, "userInfo");
</script>
<style scoped>
#basicLayout .header {
  padding: 16px;
  box-shadow: #eee 1px 1px 5px;
}

.content {
  margin-top: 16px;
  background: linear-gradient(to right, #fefefe, #fff);
}
.footer {
  color: #999;
  font-size: 12px;
  text-align: center;
}
</style>
