<template>
  <a-row class="grid-demo" style="margin-top: 6%">
    <a-col :span="6">
    </a-col>
    <a-col :span="12">
      <a-card title="Arco Card" :bordered="false" :style="{ width: '100%' }">

      </a-card>
    </a-col>
    <a-col :span="6">
    </a-col>
  </a-row>
</template>
<script setup lang="ts">
import {ref, withDefaults, defineProps, watchEffect, computed} from "vue";
interface Props{
  id:string
}
//获取页面url参数
const props=withDefaults(defineProps<Props>(),{
  id:()=>{
    return ""
  }
})

watchEffect(()=>{

})

</script>
<style scoped>

</style>