<script setup lang="ts">

import { useRouter } from 'vue-router';
const router = useRouter();

function HandleLogin() {
    router.push({ name: 'login' })
}

function HandleAbout() {
    console.log('显示关于信息...');
    alert('关于页面待实现');
}

function HandleSponsor() {
    console.log('跳转到赞助页面...');
    alert('赞助链接待实现');
}
</script>

<template>
    <div class="start-screen">
        <h1>MindFlow</h1>
        <p>lets start!</p>

        <div class="options-menu">
            <button type="button" @click="HandleLogin()">
                登录 / 注册
            </button>
            <button type="button" @click="HandleAbout()">
                关于 MindFlow
            </button>
            <button type="button" @click="HandleSponsor()">
                赞助我们
            </button>
        </div>
    </div>
</template>

<style scoped>
@import '@assets/index.css';
</style>
