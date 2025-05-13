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
    }
    // {
    //     path: '/Index',
    //     name: 'Index',
    //     component: () => import('../view/Index.vue'),
    //     children: [
    //         {
    //             path: 'article/:id',
    //             name: 'Article',
    //             component: () => import('../view/Article.vue'),
    //             props: true,
    //         },
    //     ],
    // },
    // {
    //     path: '/:pathMatch(.*)*',
    //     name: 'NotFound',
    //     component: () => import('../view/NotFound.vue'), // 404 页面
    // },
];

const router = createRouter({
    history: createWebHistory(),
    routes,
});

export default router;
