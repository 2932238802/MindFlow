<script setup lang="ts">
import { useRouter } from 'vue-router';
import { ref, computed, onMounted, onUnmounted } from 'vue';
import axios from 'axios';

interface Task {
    id: number;
    name: string;
    isdelete: boolean;
    time: string;       // 时间字段，存储为本地时间字符串
    notified?: boolean; // 是否已发送通知，默认值为 false
}

let timer: number | undefined;
const router = useRouter();
const new_task = ref<string>('');
const new_time = ref<string>('');           // 用于绑定时间输入框
const tasks = ref<Task[]>([]);

// 当某些响应式数据发生变化时，所有依赖于这些数据的计算属性、方法和模板都会自动重新计算和更新。
const currentTime = ref<Date>(new Date()); // 当前时间，用于依赖














// A组合 ——————————————————
const ReturnIndex = () => {
    router.push({ name: 'index' });
};
const ToAbout = () => {
    router.push({ name: "about" });
};
const ToSponsor = () => {
    router.push({ name: "sponsor" });
};
// A组合 ——————————————————










// C组合 ——————————————————
const AddTask = async () => {



    let content = new_task.value.trim();
    let time = new_time.value.trim();

    if (content) {
        // 如果用户没有选择时间，使用当前本地时间
        if (!time) {
            const now = new Date();
            const pad = (n: number) => n.toString().padStart(2, '0');
            time = `${now.getFullYear()}-${pad(now.getMonth() + 1)}-${pad(now.getDate())}T${pad(now.getHours())}:${pad(now.getMinutes())}`;
        }

        const new_task_withoutid: Omit<Task, 'id'> = {
            name: content,
            isdelete: false,
            time: time,
            notified: false
        };

        // 发送请求到后端保存任务
        // 发送请求到后端保存任务
        try {
            const response = await axios.post('/api/addtask', new_task_withoutid,{
            headers: {
                'Content-Type': 'application/json'
            }
        });

            if (response.data.error) {
                // 如果返回有 error 字段
                alert(`任务添加失败：${response.data.error}`);
            } else if (response.data.id) {
                // 如果返回有 id，说明任务添加成功：
                const taskid = response.data.id;

                const newtask: Task = {
                    name: content,
                    time: time,
                    isdelete: false,
                    notified: false,
                    id: taskid
                };

                // 使用后端返回的数据，包括生成的id
                tasks.value.push(newtask); 
                // 清空输入框
                new_task.value = '';
                new_time.value = '';
            }
        } catch (error) {
            console.error('添加任务失败', error);
            alert('添加任务时发生错误!');
        } finally {
        }
    }
};
// C组合 ——————————————————



















// D组合 ————————————————————————————————————————————
// 切换软删除
const ToggleDelete = async (id: number) => {
    const task = tasks.value.find(t => t.id === id);
    if (!task) return;
    task.isdelete = !task.isdelete;
    try {
        const response = await axios.put('/api/altertask', {
            id: id
        },{
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (response.data.error)
        {
            alert(response.data.error);
        }
    } catch (err) {
        console.error(' [error] 更新失败', err);
    }
};

// 彻底删除所有被标记的
const ClearDeleted = async () => {
    try {
        const response = await axios.delete('/api/cleartasks');

        if (response.data.message) {
            alert(response.data.message);
            tasks.value = tasks.value.filter(t => !t.isdelete);
        }
        else if(response.data.error)
        {
            alert(`任务删除失败:${response.data.error}`);
        }
    }
    catch (err) {
        console.error(' [error] 清理失败', err);
    }
};

// D组合 ————————————————————————————————————————————


























// E 格式化时间
const formatTime = (time: string): string => {
    const date = new Date(time);
    return date.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
    });
};

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
// E 格式化时间


















// F 组合渲染任务样式
const updateTaskColors = () => {
    currentTime.value = new Date();                         // 更新当前时间，触发重新计算
};
const GetTaskColor = (task: Task): string => {
    // now 是当前的时间
    // tasktime 是任务设置时间
    // diff 是两个时间相差 ms
    const now = currentTime.value;
    const tasktime = new Date(task.time).getTime();
    const diff = tasktime - now.getTime();                  // 剩余时间（毫秒） getTime() 方法返回 Date 对象表示的时间与 UTC 1970年1月1日午夜之间的毫秒数
    if (isNaN(tasktime)) {
        return 'invalid-time';                              // 定义一个类用于标识无效时间
    }

    const one_day = 24 * 60 * 60 * 1000;                     // 1天
    const one_hour = 60 * 60 * 1000;                         // 1小时
    const ten_minutes = 10 * 60 * 1000;                      // 10分钟

    if (diff <= ten_minutes && diff > 0 && !task.notified) {
        // 临近10分钟且未发送通知时
        return 'color-near-ten-min';
    }
    else if (diff <= one_hour && diff > ten_minutes) {
        // 临近1小时
        return 'color-near-hour';
    } else if (diff <= one_day && diff > one_hour) {
        // 临近1天
        return 'color-near-day';
    } else {
        return '';
    }
};





















// G 挂载
onMounted(() => {
    // 每分钟更新一次
    timer = window.setInterval(updateTaskColors, 60000);
    // 获取后端数据
    FetchData();
    updateTaskColors();
});

onUnmounted(() => {
    // clearInterval 清理记时任务
    if (timer !== undefined) {
        clearInterval(timer);
    }
});
// F 组合渲染任务样式












// H 后端对接代码
const FetchData = async () => {
    try {
        const response = await axios.get('/api/loadtask',{
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if(response.data.error)
        {
            alert(response.data.error);
        }
        tasks.value = response.data;
    }
    catch (error) {
        console.error('获取任务失败', error);
    }
};


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
                <input type="text" id="task-input" placeholder="输入新任务..." v-model="new_task" @keyup.enter="AddTask">
                <input type="datetime-local" id="time-input" v-model="new_time" :min="minDateTime">
                <button id="add-task-btn" @click="AddTask">添加任务</button>
            </div>
            <!-- —————————————————— -->

            <!-- D组合 —————————————————— -->
            <button id="clear-task" class="clear-task" @click="ClearDeleted">清理'已删'任务</button>

            <ul id="task-list">
                <li v-for="task in sortedTasks" :key="task.id"
                    :class="[{ 'deleted': task.isdelete }, GetTaskColor(task)]">
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
