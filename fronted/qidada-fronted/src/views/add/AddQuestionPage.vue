<template>
  <div style="margin-top: 5%;width: 80%;margin-left: 10%">
    <div :style="{ display: 'flex',flexWrap: 'wrap'  }">
      <div v-for="(Content , index) in questionContent" :key="index" :style="{ flexBasis: '23%', marginRight: '2%', marginBottom: '2%' }">
        <a-card :style="{ width: '100%', height: '400px' }" :title="`第${index + 1}道题目`" hoverable>
          <template #extra>
            <!-- 第一个按钮，减号按钮，右边添加间距 -->
            <a-button type="primary" v-if="index>0" @click="removeQuestion(index)" :style="{ marginRight: '8px' }">
              <template #icon>
                <icon-minus />
              </template>
            </a-button>

            <!-- 第二个按钮，添加按钮 -->
            <a-button type="primary" @click="addQuestion(index)">
              <template #icon>
                <icon-plus/>
              </template>
            </a-button>
          </template>
          <a-form-item :field="Content.title" tooltip="请输入题目" label="题目名称">
            <a-input
                v-model="Content.title"
                placeholder="请输入题目"
            />
          </a-form-item>
          <a-space>
            <a-button type="primary" @click="addOptions(index)">
              <template #icon>
                <icon-plus/>
              </template>
              <template #default>Add</template>
            </a-button>
            <a-button type="primary" @click="deleteOpentins(index)">
              <template #icon>
                <icon-delete/>
              </template>
              <template #default>Delete</template>
            </a-button>
          </a-space>

          <!-- 设置选项列表容器的固定高度和滚动 -->
          <div :style="{ maxHeight: '240px', overflowY: 'auto', marginTop: '10px' }">
            <div v-for="(option, optionIndex) in Content.options" :key="optionIndex">
              <a-space style="height: 40px; margin-top: 5% ">
                <a-form-item :field="option.key" :label="`选项${optionIndex + 1}`">
                  <a-input v-model="option.key" placeholder="请输入选项描述"/>
                </a-form-item>
                <a-form-item field="option.value" label="选项值">
                  <a-input v-model="option.value" placeholder="请输入选项值"/>
                </a-form-item>
              </a-space>
            </div>
          </div>
        </a-card>
<!--        {{ questionContent }}-->
      </div>
    </div>
    <a-space style="display: flex; justify-content: center; width: 100%;">
      <a-button type="primary" @click="submit()">提交</a-button>
    </a-space>

  </div>
</template>
<script setup lang="ts">
//钩子函数
import {ref, watchEffect} from "vue";
import {useLoginStore} from "@/store/userStore";
import {useRouter} from "vue-router";
import API from "@/api";
import {IconPlus, IconDelete,IconMinus} from '@arco-design/web-vue/es/icon';
import {addQuestionUsingPost, getQuestionVoByIdUsingGet} from "@/api/questionController";
import {defineProps, withDefaults} from "vue/dist/vue";
import {Message} from "@arco-design/web-vue";

const router = useRouter();

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

const loginUserStore = useLoginStore();
let loginUser = loginUserStore.loginUser;
const questionContent = ref<API.QuestionContentDTO[]>([
  {
    options: [],
    title: ''
  }
]);
//添加题目
const addQuestion = (index: any) => {
  questionContent.value.splice(index + 1, 0, {
    title: '',
    options: [],
  })
}

//添加选项
const addOptions = (index: any) => {
  questionContent.value[index].options?.push(
      {
        key: '',
        value: ''
      }
  )
}

//删除题目
const removeQuestion=(index : any)=>{
  questionContent.value.splice(index,1)
}

//删除选项
const deleteOpentins = (index: any) => {
  questionContent.value[index].options?.splice(questionContent.value[index].options?.length - 1, 1)
}
//提交题目
const submit= async ()=>{
  //判断题目是否为空
  const isEmpty=questionContent.value.every(content => {
    return !content.title && (!content.options || content.options.length === 0);
  });
  if (isEmpty){
    Message.error('题目及选项不可为空')
    return
  }
  const res = await addQuestionUsingPost({
    appId: props.id as any,
    questionContent: questionContent.value,
  });
  console.log(res,"题目提交")
  if (res.data.code==0){
    Message.success('题目配置成功!')
  }
}
watchEffect(() => {
  if (loginUser.userName == "未登录" || loginUser.userName == "游客") {
    router.push("/user/login");
  }
  const res=getQuestionVoByIdUsingGet({
    id:props.id as any,
  });
  console.log(res,"获取题目信息")
});
</script>
<style scoped>
.card-demo {
  width: 360px;
  margin-left: 24px;
  transition-property: all;
}

.card-demo:hover {
  transform: translateY(-4px);
}
</style>
