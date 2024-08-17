import { defineStore } from "pinia";
import { computed, ref } from "vue";
import { getLoginUserUsingGet } from "@/api/userController";
import AccessEnum from "@/access/accessEnum";

export const useLoginStore = defineStore("counter", () => {
  const loginUser = ref<API.LoginUserVO>({
    userAvatar: "",
    userName: "未登录",
    userRole: "",
  });

  async function fetchLoginUser() {
    const res = await getLoginUserUsingGet();
    if (res.data.code === 0 && res.data.data) {
      loginUser.value = res.data.data;
    } else {
      //标识非首次登录
      loginUser.value.userRole = AccessEnum.NOT_LOGIN;
    }
  }

  function setLoginUser(newLoginUser: API.LoginUserVO) {
    loginUser.value = newLoginUser;
  }

  return { loginUser, setLoginUser, fetchLoginUser };
});
