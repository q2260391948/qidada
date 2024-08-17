import { RouteRecordRaw } from "vue-router";
import HomeView from "@/views/HomeView.vue";
import AccessEnum from "@/access/accessEnum";
import NoAuth from "@/views/NoAuth.vue";

export const routes: Array<RouteRecordRaw> = [
  {
    path: "/",
    name: "home",
    component: HomeView,
  },
  {
    path: "/admin",
    name: "管理页面",
    component: HomeView,
    meta: {
      access: AccessEnum.ADMIN,
      hidden: false,
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
    path: "/about",
    name: "about",
    component: () =>
      import(/* webpackChunkName: "about" */ "../views/AboutView.vue"),
  },
  {
    path: "/test",
    name: "test",
    component: HomeView,
    meta: {
      //hidden隐藏不可见
      hidden: true,
    },
  },
  {
    path: "/user/login",
    name: "login",
    component: () => import("@/layouts/UserLayout.vue"),
    meta: {
      //hidden隐藏不可见
      hidden: true,
    },
  },
];
