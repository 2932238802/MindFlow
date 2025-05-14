<script setup lang="ts">

import axios from 'axios';
import { reactive, ref } from 'vue';
import { useRouter } from 'vue-router';


//———————————————— 组件参数 ——————————————————//
const activeForm = ref<'login' | 'register'>('login');
const router = useRouter();

// 这主要是为了利用 TypeScript 带来的静态类型检查和代码可维护性优势
// 才定义了 interface 组件
interface login_form_state {
    user_name: string,
    password: string,
    remember: boolean
}
const login_data = reactive<login_form_state>({
    user_name: '',
    password: '',
    remember: false,
});

interface register_form_state {
    user_name: string,
    email: string,
    password: string,
    password_confirm: string,
}
const register_data = reactive<register_form_state>({
    user_name: '',
    email: '',
    password: '',
    password_confirm: '',
});


//———————————————— 组件函数 ——————————————————//
/**
 * 切换 登录和注册状态
 */
function showLogin() {
    activeForm.value = 'login';
}
function showRegister() {
    activeForm.value = 'register';
}
const Sleep = (time: number): Promise<void> => {
    return new Promise(resolve => {
        setTimeout(resolve, time);
    }
    );
};


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
        "remember": login_data.remember, 
    });

    console.log("Headers:", {
        'Content-Type': 'application/json',
    });

    try {
        const response = await axios.post('/api/login', {
            user_name: login_data.user_name,
            password: login_data.password
        }, {
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (response.status === 200) {
            localStorage.setItem('UserName', login_data.user_name);
            sessionStorage.setItem('UserName', login_data.user_name);

            Sleep(1000).then(() => { 
                alert("登录成功!");
                router.push({ name: 'todolist' }); 
            });
        } else {
            alert(response.data.message || '登录失败，请检查响应。');
        }
    } catch (error: any) {
        if (axios.isAxiosError(error)) {
            alert(error.response?.data.message || '登录失败，请重试。');
        } else {
            alert('An unexpected error occurred!');
        }
    }
};


const SubmitRegister = async (event: Event) => {

    event.preventDefault();

    try {
        // 验证密码与确认密码是否匹配
        if (register_data.password !== register_data.password_confirm) {
            alert('密码与确认密码不匹配。');
            return;
        }

        // 发送 POST 请求到后端注册 API
        const response = await axios.post('/api/register', {
            "user_name": register_data.user_name,
            "email": register_data.email,
            "password": register_data.password,
        });

        // 处理成功响应 //
        if (response.status === 201) {
            alert('注册成功！请登录。');
            showLogin();
        }
    }

    catch (error: any) {
        if (axios.isAxiosError(error)) {
            alert(error.response?.data.message || '注册失败，请重试。');
        } else {
            alert('发生意外错误。');
        }
    }
};


</script>
<template>
    <div class="container">
        <div class="form-container">
            <!-- 切换按钮 -->
            <div class="form-toggle">
                <button id="login-btn" class="toggle-btn" :class="{ active: activeForm === 'login' }"
                    @click="showLogin">
                    登录
                </button>
                <button id="register-btn" class="toggle-btn" :class="{ active: activeForm === 'register' }"
                    @click="showRegister">
                    注册
                </button>
            </div>

            <form id="login-form" class="form" method="POST" :class="{ active: activeForm === 'login' }">
                <h2>账户登录</h2>
                <div class="input-group">
                    <label for="login-username">用户名 / 邮箱</label>
                    <input type="text" id="login-username" name="username" v-model="login_data.user_name" required>
                </div>
                <div class="input-group">
                    <label for="login-password">密码</label>
                    <input type="password" id="login-password" name="password" v-model="login_data.password" required>
                </div>
                <div class="form-options">
                    <div class="remember-me">
                        <input type="checkbox" id="remember" name="remember">
                        <label for="remember">记住我</label>
                    </div>
                    <a href="#" class="forgot-password">忘记密码?</a>
                </div>
                <button type="submit" class="submit-btn" @click="SubmitLogin">登 录</button>
            </form>

            <form id="register-form" class="form" method="POST" :class="{ active: activeForm === 'register' }">
                <h2>创建账户</h2>
                <div class="input-group">
                    <label for="register-username">用户名</label>
                    <input type="text" id="register-username" name="register_username" v-model="register_data.user_name"
                        required>
                </div>
                <div class="input-group">
                    <label for="register-email">邮箱</label>
                    <input type="email" id="register-email" name="register_email" v-model="register_data.email"
                        required>
                </div>
                <div class="input-group">
                    <label for="register-password">设置密码</label>
                    <input type="password" id="register-password" name="register_password"
                        v-model="register_data.password" required>
                </div>
                <div class="input-group">
                    <label for="register-confirm-password">确认密码</label>
                    <input type="password" id="register-confirm-password" name="register_confirm_password"
                        v-model="register_data.password_confirm" required>
                </div>
                <button type="submit" class="submit-btn" @click="SubmitRegister">注 册</button>
            </form>
        </div>
    </div>
</template>
<style scoped>
@import '@assets/login.css';
</style>
