import axios from "axios";

// 创建一个axios实例
const myAxios = axios.create({
  baseURL: "http://localhost:8101/",
  timeout: 3000,
  //携带cooike
  withCredentials: true,
});

// 添加请求拦截器
myAxios.interceptors.request.use(
  function (config) {
    // 在发送请求之前做些什么
    return config;
  },
  function (error) {
    // 对请求错误做些什么
    return Promise.reject(error);
  }
);

// 添加响应拦截器
myAxios.interceptors.response.use(
  function (response) {
    // console.log(response, "response");
    const { data } = response;
    // 如果返回的状态码是401，则跳转到登录页面
    if (data.code == 40100) {
      //请求失败 请求接口是获取
      if (
        !response.request.responseURL.includes("user/get/login") &&
        !window.location.pathname.includes("/user/login")
      ) {
        //跳转到登录页面
        window.location.href = `/user/login?redirect=${window.location.href}`;
      }
    }

    // 对响应数据做点什么
    return response;
  },
  function (error) {
    // 对响应错误做点什么
    return Promise.reject(error);
  }
);

export default myAxios;
