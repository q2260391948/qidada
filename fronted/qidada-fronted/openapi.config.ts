const { generateService } = require("@umijs/openapi");

generateService({
  // 请求库的路径，根据项目实际情况修改 默认使用request
  requestLibPath: "import request from '@/request'",
  schemaPath: "http://localhost:8101/api/v2/api-docs",
  //生成在src目录下
  serversPath: "./src",
});
