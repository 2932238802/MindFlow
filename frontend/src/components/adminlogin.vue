<script setup lang="ts">

import axios from 'axios';
import { reactive } from 'vue';
import { useRouter } from 'vue-router';
import { ShowCustomModal } from '../ts/model';

const router = useRouter();

//———————————————— 组件 ——————————————————//
interface login_form_state {
    user_name: string,
    password: string,
}
const login_data = reactive<login_form_state>({
    user_name: '',
    password: '',
});


//———————————————— 业务函数 ——————————————————//
/**
 * SubmitLogin 提交登录信息
 * SubmitRegister 提交注册信息
 */
const SubmitLogin = async (event: Event) => {

    event.preventDefault();
    console.log("Sending login data:", {
        "user_name": login_data.user_name,
        "password": login_data.password,
    });

    console.log("Headers:", {
        'Content-Type': 'application/json',
    });

    try {
        const response = await axios.post('/api/adminlogin', {
            user_name: login_data.user_name,
            password: login_data.password
        }, {
            headers: {
                'Content-Type': 'application/json'
            }
        });

        // 200 更加适合登录
        // 201 一般是资源创建成功
        if (response.status === 200) {
            localStorage.setItem('UserName', login_data.user_name);
            sessionStorage.setItem('UserName', login_data.user_name);
            router.push({ name: 'adminindex' }); 
        } else {
            ShowCustomModal(response.data.message || '登录失败，请检查响应');
        }
    } catch (error: any) {
        if (axios.isAxiosError(error)) {
            ShowCustomModal(error.response?.data.message || '登录失败，请重试  ');
        } else {
            ShowCustomModal('An unexpected error occurred!');
        }
    }
};


const Return = ()=>
{
    router.push({name:"login"})
}
</script>


<template>
<body>
    <div class="login-container">
        <h1>MindAdmin</h1>
        <form id="loginForm">
            <div class="form-group">
                <label for="username">用户名:</label>
                <input type="text" id="username" name="username" required autocomplete="username"  v-model="login_data.user_name">
            </div>
            <div class="form-group">
                <label for="password">密码:</label>
                <input type="password" id="password" name="password" required autocomplete="current-password" v-model="login_data.password">
            </div>
            <div>
                    <a href="#" class="adminlogin" @click="Return">返回登录</a>
            </div>
            <button type="submit"  @click="SubmitLogin">登 录</button>
            
        </form>
    </div>
</body>
</template>
<style scoped>
@import '@assets/adminlogin.css';

</style>
