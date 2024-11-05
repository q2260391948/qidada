import { RouteRecordRaw } from "vue-router";
import HomeView from "@/views/HomeView.vue";
import AccessEnum from "@/access/accessEnum";
import NoAuth from "@/views/NoAuth.vue";
import MdEditor from "@/components/MdEditor.vue";
import MdViewer from "@/components/MdViewer.vue";
import PictureUploader from "@/components/PictureUploader.vue";
import AdminUserPage from "@/views/admin/AdminUserPage.vue";
import AdminAppPage from "@/views/admin/AdminAppPage.vue";
import AdminQuestionPage from "@/views/admin/AdminQuestionPage.vue";
import AdminScoringResultPage from "@/views/admin/AdminScoringResultPage.vue";
import AdminUserAnswerPage from "@/views/admin/AdminUserAnswerPage.vue";
import AddAppPage from "@/views/add/AddAppPage.vue";
import AppDetailPage from "@/views/app/AppDetailPage.vue";
import AddQuestionPage from "@/views/add/AddQuestionPage.vue";
import AddScoringResultPage from "@/views/add/AddScoringResultPage.vue";
export const routes: Array<RouteRecordRaw> = [
  {
    path: "/",
    name: "首页",
    component: HomeView,
  },
  {
    path: "/add/app",
    name: "添加应用",
    props: true,
    component: AddAppPage,
  },
  {
    path: "/add/question/:id",
    name: "设置题目",
    props: true,
    component: AddQuestionPage,
    meta: {
      hidden: true,
    },
  },
  {
    path: "/add/scoringResult/:id",
    name: "设置评分",
    props: true,
    component: AddScoringResultPage,
    meta: {
      hidden: true,
    },
  },
  {
    path: "/admin",
    name: "管理页面",
    component: HomeView,
    meta: {
      access: AccessEnum.ADMIN,
      // hidden: true,
    },
  },
  {
    path: "/admin/user",
    name: "用户管理",
    component: AdminUserPage,
    meta: {
      access: AccessEnum.ADMIN,
      // hidden: true,
    },
  },
  {
    path: "/admin/app",
    name: "应用管理",
    component: AdminAppPage,
    meta: {
      access: AccessEnum.ADMIN,
    },
  },
  {
    path: "/admin/question",
    name: "题目管理",
    component: AdminQuestionPage,
    meta: {
      access: AccessEnum.ADMIN,
    },
  },
  {
    path: "/admin/scoring_result",
    name: "评分管理",
    component: AdminScoringResultPage,
    meta: {
      access: AccessEnum.ADMIN,
    },
  },
  {
    path: "/admin/user_answer",
    name: "回答管理",
    component: AdminUserAnswerPage,
    meta: {
      access: AccessEnum.ADMIN,
    },
  },
  {
    path: "/app/app:id",
    name: "修改应用",
    props: true,
    component: AddAppPage,
    meta: {
      hidden: true,
    },
  },
  {
    path: "/app/detail/:id",
    name: "应用详情页",
    props: true,
    component: AppDetailPage,
    meta: {
      hidden: true,
    },
  },
  {
    path: "/noAuth",
    name: "noAuth",
    component: NoAuth,
    meta: {
      hidden: true,
    },
  },
  {
    path: "/mdEditor",
    name: "mdEditor",
    component: MdEditor,
    meta: {
      hidden: true,
    },
  },
  {
    path: "/mdViewer",
    name: "mdViewer",
    component: MdViewer,
    meta: {
      hidden: true,
    },
  },
  {
    path: "/pictureUpload",
    name: "图片上传",
    component: PictureUploader,
    meta: {
      hidden: true,
    },
  },

  {
    path: "/about",
    name: "关于",
    component: () =>
      import(/* webpackChunkName: "about" */ "../views/AboutView.vue"),
  },
  {
    path: "/user",
    name: "用户",
    meta: {
      hidden: true,
    },
    children: [
      {
        path: "/user/login",
        name: "用户注册",
        component: () => import("@/layouts/UserLayout.vue"),
        meta: {
          //hidden隐藏不可见
          hidden: true,
        },
      },
    ],
  },
];
