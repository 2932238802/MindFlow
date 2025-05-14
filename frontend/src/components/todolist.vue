<script setup lang="ts">
import { useRouter } from 'vue-router';
import { ref, computed } from 'vue';

interface Task {
    id: number;
    name: string;
    isdelete: boolean;
    time: string; // 时间字段，存储为本地时间字符串
}

let next_id = 0;
const router = useRouter();
const new_task = ref<string>('');
const new_time = ref<string>(''); // 用于绑定时间输入框
const tasks = ref<Task[]>([]);

// A组合 ——————————————————
const ReturnIndex = () => {
    router.push({ name: 'index' });
}
const ToAbout = () => {
    router.push({ name: "about" });
}
const ToSponsor = () => {
    router.push({ name: "about" });
}
// A组合 ——————————————————

// C组合 ——————————————————
const AddTask = () => {
    let content = new_task.value.trim();
    let time = new_time.value.trim();

    if (content) {
        // 如果用户没有选择时间，使用当前本地时间
        if (!time) {
            const now = new Date();
            const pad = (n: number) => n.toString().padStart(2, '0');
            time = `${now.getFullYear()}-${pad(now.getMonth() + 1)}-${pad(now.getDate())}T${pad(now.getHours())}:${pad(now.getMinutes())}`;
        }

        tasks.value.push({
            id: next_id++,
            name: content,
            isdelete: false,
            time: time
        });

        // 清空输入框
        new_task.value = '';
        new_time.value = '';
    }
}
// C组合 ——————————————————

// D组合 ————————————————————————————————————————————
const ToggleDelete = (id: number) => {
    const task = tasks.value.find(t => t.id === id);
    if (task) task.isdelete = !task.isdelete;
}

const ClearDeleted = () => {
    tasks.value = tasks.value.filter(t => !t.isdelete);
}
// D组合 ————————————————————————————————————————————

// 时间格式化方法
const formatTime = (time: string): string => {
    const date = new Date(time);
    return date.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
    });
}

// 按时间排序的计算属性
const sortedTasks = computed(() => {
    return [...tasks.value].sort((a, b) => new Date(a.time).getTime() - new Date(b.time).getTime());
});

// 设置时间输入的最小值为当前本地时间
const minDateTime = computed(() => {
    const now = new Date();
    const pad = (n: number) => n.toString().padStart(2, '0');
    return `${now.getFullYear()}-${pad(now.getMonth() + 1)}-${pad(now.getDate())}T${pad(now.getHours())}:${pad(now.getMinutes())}`;
});
</script>

<template>
    <body class="todolist">
        <div class="container">
            <!-- A组合 —————————————————— -->
            <h1>Day-Todolist</h1>
            <div class="return" id="return" @click="ReturnIndex"> 返回 </div>
            <div class="about" id="about" @click="ToAbout"> 关于 </div>
            <div class="sponsor" id="sponsor" @click="ToSponsor"> 赞助 </div>
            <!-- —————————————————— -->

            <!-- B组合 —————————————————— -->
            <p id="empty-message" class="empty-message" v-if="tasks.length === 0">当前没有待办事项</p>
            <!-- —————————————————— -->

            <!-- C组合 —————————————————— -->
            <div class="add-task-form">
                <input 
                    type="text" 
                    id="task-input" 
                    placeholder="输入新任务..." 
                    v-model="new_task" 
                    @keyup.enter="AddTask"
                >
                <input 
                    type="datetime-local" 
                    id="time-input" 
                    v-model="new_time"
                    :min="minDateTime"
                >
                <button id="add-task-btn" @click="AddTask">添加任务</button>
            </div>
            <!-- —————————————————— -->

            <!-- D组合 —————————————————— -->
            <button id="clear-task" class="clear-task" @click="ClearDeleted">清理'已删'任务</button>

            <ul id="task-list">
                <li 
                    v-for="task in sortedTasks" 
                    :key="task.id"
                    :class="{ 'deleted': task.isdelete }">
                    <span class="task-name">{{ task.name }}</span>
                    <span class="task-time">{{ formatTime(task.time) }}</span>
                    <button class="delete-btn" @click="ToggleDelete(task.id)">
                        {{ task.isdelete ? '➕' : '✖️' }}
                    </button>
                </li>
            </ul>
            <!-- —————————————————— -->
        </div>
    </body>
</template>

<style scoped>
@import '@assets/todolist.css';
</style>
