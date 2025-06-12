# 🐿️ MindFlow

---

**版本**: 1.0.7
**最后更新**: 2025.6.7
**作者**: LosAngelous

---

## 简介

MindFlow 是一个简单的在线任务清单

## 主要功能

* 记录自己的每日任务

* 对任务的轻重缓急进行标注

## 如何开始

```shell
1. 首先保证自己本地安装好了 npm 和 mvn
2. 然后下载官方的tomcat(Linux) 安装到 backend 目录下面即可 更改名字为tomcat
3. 按照下面指令执行 
cd MindFlow
./script.sh
```

然后访问本地127.0.0.1:8080 即可开始本地部署了
(具体哪个端口号 可以查看 tomcat的server.xml文件 进行修改)

## 技术栈

### 前端

本项目前端基于 **Vue 3** 构建，使用 **Vite** 作为构建工具和开发服务器

* **核心框架**:
  * Vue.js: `^3.5.13`
* **路由**:
  * Vue Router: `^4.5.0`
* **构建工具**:
  * Vite: `^6.3.3`
* **TypeScript 支持**:
  * TypeScript: `~5.7.2`
  * Vue TSC : `^2.2.8`
* **Vite 插件**:
  * @vitejs/plugin-vue: `^5.2.2`
* **类型定义**:
  * @types/node: `^22.15.3`
  * @vue/tsconfig: `^0.7.0`

**主要脚本 (`package.json`)**:

* `dev`: `vite` 
* `build`: `vue-tsc -b && vite build` 
* `preview`: `vite preview`

### 后端

本项目后端采用 **Java** 技术栈，构建为 **Servlet** 应用

* **核心语言**: Java `17`
* **Web 容器/Servlet API**: Servlet API `5.0.0`
* **JSON 处理**:
  * Gson: `2.8.8` 
* **数据库驱动**:
  * MySQL Connector/J: `8.0.33`
* **密码哈希**:
  * JBCrypt: `0.4`
* **构建工具**:
  * Maven
    * Maven WAR 插件: `3.3.2`
