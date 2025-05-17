// 导入函数 //
import { createRouter, createWebHistory } from 'vue-router';

// 注册模块 //
const routes = [
    {
        path: '/',
        name: 'index',
        component: () => import('../src/components/index.vue'),
    },
    {
        path: '/login',
        name: 'login',
        component: () => import('../src/components/login.vue'),
    },
    {
        path:'/todolist',
        name:'todolist',
        component:() => import('../src/components/todolist.vue'),
    },
    {
        path:'/about',
        name:'about',
        component:() => import('../src/components/about.vue'),
    },
    {
        path:'/sponsor',
        name:'sponsor',
        component:() => import('../src/components/sponsor.vue'),
    },
    {
        path:'/adminlogin',
        name:'adminlogin',
        component:() => import('../src/components/adminlogin.vue'),
    },
    {
        path:'/adminindex',
        name:'adminindex',
        component:()=>import('../src/components/adminindex.vue') // <<<--- 修正这里
    },
];

const router = createRouter({
    history: createWebHistory(),
    routes, 
});

export default router;
