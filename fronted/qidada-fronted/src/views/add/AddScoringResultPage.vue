<template>
  <div style="width: 80%; margin: 0 auto">
    <a-form
        :model="form"
        :style="{ width: '600px', marginTop: '5%' }"
        @submit="handleSubmit"
    >
      <H2>创建评分结果</H2>
      <a-form-item field="name" label="应用ID">
        {{ id }}
      </a-form-item>
      <a-form-item field="resultName" label="结果名称">
        <a-input v-model="form.resultName" placeholder="请输入结果名称"/>
      </a-form-item>
      <a-form-item field="resultDesc" label="结果描述">
        <a-input v-model="form.resultDesc" placeholder="请输入结果描述"/>
      </a-form-item>
      <a-form-item field="resultPicture" label="结果图标">
        <a-input v-model="form.resultPicture" placeholder="请上传结果图标"/>
      </a-form-item>
      <a-form-item field="resultProp" label="结果集">
        <a-input-tag
            v-model="form.resultProp"
            :style="{ width: '380px' }"
            placeholder="输入后请按回车键确认"
            :max-tag-count="3"
            allow-clear
        />
      </a-form-item>
      <a-form-item field="resultScoreRange" label="得分范围">
        <a-input v-model="form.resultScoreRange" placeholder="请输入得分范围"/>
      </a-form-item>
      <a-space style="justify-content: center">
        <a-button type="primary" @click="addScoringResult()">提交</a-button>
      </a-space>
    </a-form>
    <H2>管理评分结果</H2>
    <!-- 父组件通过props将函数initDate传递给子组件 ，子组件进行调用 -->
    <ScoringResultPage :appId="id" :initDate="initDate" ref="tableRef"></ScoringResultPage>
  </div>
</template>
<script setup lang="ts">
import {ref} from "vue";
import {defineProps, withDefaults} from "vue/dist/vue";
import {
  addScoringResultUsingPost,
  deleteScoringResultUsingPost, editScoringResultUsingPost,
} from "@/api/scoringResultController";
import {Message} from "@arco-design/web-vue";
import ScoringResultPage from "@/views/add/components/ScoringResultPage.vue";
import API from "@/api";
import message from "@arco-design/web-vue/es/message";

//接收参数
interface Props {
  id: string;
}

const props = withDefaults(defineProps<Props>(), {
  id: () => {
    return "";
  },
});

let form = ref({
  appId: props.id,
  resultDesc: "",
  resultName: "",
  resultPicture: "",
  resultProp: [],
  resultScoreRange: "",
  id: ""
});

const tableRef = ref(null);
const addScoringResult = async () => {
  console.log(form.value);
  //判空
  if (form.value.resultName === "") {
    Message.info("请输入结果名称");
    return;
  }
  if (form.value.resultDesc === "") {
    Message.info("请输入结果描述");
    return;
  }
  if (form.value.resultPicture === "") {
    Message.info("请上传结果图标");
    return;
  }
  if (form.value.resultProp.length === 0) {
    Message.info("请输入结果集");
    return;
  }
  if (form.value.resultScoreRange === "") {
    Message.info("请输入得分范围");
    return;
  }
  let res;
  console.log(form.value.id,"id")
  if (form.value.id) {
    res = await editScoringResultUsingPost(form.value);
  } else {
    res = await addScoringResultUsingPost(form.value);
  }
  console.log(res.data.data, "res");
  if (res.data.code === 0) {
    Message.success("配置成功");
  } else {
    Message.error("配置失败" + res.data.message);
  }
  console.log(tableRef.value,"table")
  if (tableRef.value) {
    tableRef.value.loadData()
    form.value={
      appId: props.id,
      resultDesc: "",
      resultName: "",
      resultPicture: "",
      resultProp: [],
      resultScoreRange: "",
      id: ""
    }
  }
};
const initDate = async (record: API.ScoringResultVO) => {
  console.log(record, "record 子组件数据传递给父组件");
  form.value = record as any;
  form.value.resultProp = JSON.parse(record.resultProp as any)
};
const handleSubmit = (data) => {
  console.log(data);
};
</script>
<style scoped></style>
