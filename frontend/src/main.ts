// main.js

// 导入模块 //
import { createApp } from 'vue';
import router from '../router';
import App from './App.vue';

// 创建app //
const app = createApp(App);
app.use(router);


app.mount('#app');
