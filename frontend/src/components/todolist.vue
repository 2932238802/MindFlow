<script setup lang="ts">
import { useRouter } from 'vue-router';
import { ref, computed, onMounted, onUnmounted } from 'vue';
import { ShowCustomModal } from '../ts/model';
import axios from 'axios';

// 组件和参数
interface Task {
    id: number;
    name: string;
    isdelete: boolean;
    time: string;
    notified?: boolean;
    importance: string;
}
const getDefaultTaskTime = (): string => {
    const now = new Date();
    now.setHours(now.getHours() + 1);
    const pad = (n: number) => n.toString().padStart(2, '0');
    return `${now.getFullYear()}-${pad(now.getMonth() + 1)}-${pad(now.getDate())}T${pad(now.getHours())}:${pad(now.getMinutes())}`;
};
let timer: number | undefined;
const router = useRouter();
const new_task = ref<string>('');
const new_time = ref<string>(getDefaultTaskTime());
const new_importance = ref<string>('1');
const tasks = ref<Task[]>([]);
const currentTime = ref<Date>(new Date());

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
const ShowMessage = () => {
    // 展示邮箱界面
}

const AddTask = async () => {
    let content = new_task.value.trim();
    let time = new_time.value.trim();
    let importance = new_importance.value.trim();

    if (content) {
        // 如果用户没有选择时间，使用当前本地时间
        if (!time) {
            time = getDefaultTaskTime()
        }

        const new_task_withoutid: Omit<Task, 'id'> = {
            name: content,
            isdelete: false,
            time: time,
            notified: false,
            importance: '1',
        };

        // 发送请求到后端保存任务
        // 发送请求到后端保存任务
        try {
            const response = await axios.post('/api/addtask', new_task_withoutid, {
                headers: {
                    'Content-Type': 'application/json'
                }
            });

            if (response.data.error) {
                // 如果返回有 error 字段
                ShowCustomModal(`任务添加失败：${response.data.error}`);
            } else if (response.data.id) {
                // 如果返回有 id，说明任务添加成功：
                const taskid = response.data.id;

                const newtask: Task = {
                    name: content,
                    time: time,
                    isdelete: false,
                    notified: false,
                    id: taskid,
                    importance: importance
                };

                // 使用后端返回的数据，包括生成的id
                tasks.value.push(newtask);
                // 清空输入框
                new_task.value = '';
                new_time.value = getDefaultTaskTime();
                new_importance.value = '1'
            }
        } catch (error) {
            console.error('添加任务失败', error);
            ShowCustomModal('添加任务时发生错误!');
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
        }, {
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (response.data.error) {
            ShowCustomModal(response.data.error);
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
            ShowCustomModal(response.data.message);
            tasks.value = tasks.value.filter(t => !t.isdelete);
        }
        else if (response.data.error) {
            ShowCustomModal(`任务删除失败:${response.data.error}`);
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

// 按照时间 和 删除进行排序
const sortedTasks = computed(() => {
    return [...tasks.value].sort((a, b) => {
        if (a.isdelete !== b.isdelete) {
            return a.isdelete ? 1 : -1;
        }

        const timeA = new Date(a.time).getTime();
        const timeB = new Date(b.time).getTime();

        if (isNaN(timeA) && isNaN(timeB)) return 0;
        if (isNaN(timeA)) return 1;
        if (isNaN(timeB)) return -1;
        return timeA - timeB;
    });
});

// 设置时间输入的最小值为当前本地时间
const minDateTime = computed(() => {
    const now = new Date();
    const pad = (n: number) => n.toString().padStart(2, '0');
    return `${now.getFullYear()}-${pad(now.getMonth() + 1)}-${pad(now.getDate())}T${pad(now.getHours())}:${pad(now.getMinutes())}`;
});




// F 组合渲染任务样式
const updateTaskColors = () => {
    currentTime.value = new Date();                         // 更新当前时间，触发重新计算
};
const GetTaskColor = (task: Task): string => {
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

    // 这里加上邮箱信息加载 后端给邮箱发送给信息

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
        const response = await axios.get('/api/loadtask', {
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (response.data.error) {
            ShowCustomModal(response.data.error);
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
            <div class="button-container">
                <button class="return" @click="ReturnIndex">返回</button>
                <button class="about" @click="ToAbout">关于</button>
                <button class="sponsor" @click="ToSponsor">赞助</button>
                <button class="message" @click="ShowMessage">邮箱</button>
            </div>

            <h1>MindFLow</h1>
            <!-- —————————————————— -->

            <!-- B组合 —————————————————— -->
            <p id="empty-message" class="empty-message" v-if="tasks.length === 0">当前没有待办事项</p>
            <!-- —————————————————— -->

            <!-- C组合 —————————————————— -->
            <div class="add-task-form" @submit.prevent="AddTask">
                <input type="text" id="task-input" placeholder="输入新任务..." v-model="new_task">
                <input type="datetime-local" id="time-input" v-model="new_time" :min="minDateTime">
                <button id="add-task-btn" @click="AddTask">添加任务</button>
                <select id="importance" v-model="new_importance">
                    <option value="" disabled>选择重要性</option>
                    <option value="1">重要且紧急</option>
                    <option value="2">重要但不紧急</option>
                    <option value="3">紧急但不重要</option>
                    <option value="4">不重要且不紧急</option>
                </select>
            </div>
            <!-- —————————————————— -->

            <!-- D组合 —————————————————— -->
            <button id="clear-task" class="clear-task" @click="ClearDeleted">清理'已删'任务</button>

            <ul id="task-list">
                <li v-for="task in sortedTasks" :key="task.id" :class="[
                    { 'deleted': task.isdelete },
                    GetTaskColor(task),
                    'importance-' + task.importance
                ]">
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
