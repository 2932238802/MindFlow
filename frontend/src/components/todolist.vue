<script setup lang="ts">
import { useRouter } from 'vue-router';
import {ref} from 'vue'

interface Task{
    id:number;
    name:string;
}

let next_id = 0; 
const router = useRouter()
const new_task = ref<string>('');
const tasks = ref<Task[]>([
]);


const ReturnIndex = ()=>
{
    router.push({ name: 'index' });
}

const RemoveTask = (id: number) => {
    tasks.value = tasks.value.filter(task => task.id !== id);
};

const AddTask = () =>
{
    let content = new_task.value.trim()
     if (content) {
        tasks.value.push({ id: next_id++, name: content });
        new_task.value = ''; // 清空输入框
    }
}


</script>

<template>

    <body class="todolist">
        <div class="container">

            <!-- A组合 —————————————————— -->
            <h1>MindFlow</h1>
            <div class="return" id="return" @click="ReturnIndex"> 返回 </div>
            <div class="about" id="about"> 关于 </div>
            <div class="sponsor" id="sponsor"> 赞助 </div>
            <!-- —————————————————— -->

            <!-- B组合 —————————————————— -->
            <p id="empty-message" class="empty-message" style="display: none;">当前没有待办事项</p>
            <!-- —————————————————— -->

            <!-- C组合 —————————————————— -->
            <div class="add-task-form">
                <input 
                type="text" 
                id="task-input" 
                placeholder="输入新任务..."
                v-model="new_task"
                >

                <button id="add-task-btn" @click="AddTask">添加任务</button>
            </div>
            <!-- —————————————————— -->

            <!-- D组合 —————————————————— -->
            <ul id="task-list">
                <li v-for="task in tasks" :key="task.id">
                    <span>{{task.name}}</span>
                    <button class="delete-btn" @click="RemoveTask(task.id)">✖️</button>
                </li>
            </ul>
            <button id="clear-task" class="clear-task">清理'已删'任务</button>
            <!-- —————————————————— -->

        </div>
    </body>
</template>

<style scoped>
@import '@assets/todolist.css';
</style>
