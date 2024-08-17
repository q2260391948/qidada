import { useLoginStore } from "@/store/userStore";
import AccessEnum from "@/access/accessEnum";
import checkAccess from "@/access/checkAccess";
import router from "@/router";

router.beforeEach(async (to, from, next) => {
  const loginUserStore = useLoginStore();
  let loginUser = loginUserStore.loginUser;
  console.log("登陆用户信息", loginUser);
  //如果曾经登录过再次尝试获取用户信息
  if (loginUser && !loginUser.userRole) {
    // 加 await 是为了等用户登录成功之后，再执行后续的代码
    await loginUserStore.fetchLoginUser();
    loginUser = loginUserStore.loginUser;
  }
  const needAccess = (to.meta?.access as string) ?? AccessEnum.NOT_LOGIN;
  // 要跳转的页面必须要登陆
  if (needAccess !== AccessEnum.NOT_LOGIN) {
    // 如果没登陆，跳转到登录页面
    if (
      !loginUser ||
      !loginUser.userRole ||
      loginUser.userRole === AccessEnum.NOT_LOGIN
    ) {
      next(`/user/login?redirect=${to.fullPath}`);
      return;
    }
    // 如果已经登陆了，但是权限不足，那么跳转到无权限页面
    if (!checkAccess(loginUser, needAccess)) {
      next("/noAuth");
      return;
    }
  }
  next();
});
