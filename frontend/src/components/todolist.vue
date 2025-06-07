<script setup lang="ts">
// --------------------------------------------------
// IMPORTS
// --------------------------------------------------
import { useRouter } from 'vue-router';
import { ref, computed, onMounted, onUnmounted } from 'vue';
import { ShowCustomModal } from '../ts/model'; // 确保路径 '../ts/model' 正确
import axios from 'axios';

// --------------------------------------------------
// 接口
// --------------------------------------------------
interface Task {
    id: number;
    name: string;
    isdelete: boolean;
    time: string;
    notified?: boolean;
    importance: string;
}

interface Email {
    id: number;
    user_id: number;
    message: string;
    sent_at: string;
}

// --------------------------------------------------
// 更新时间
// --------------------------------------------------
const getDefaultTaskTime = (): string => {
    const now = new Date();
    now.setHours(now.getHours() + 1);
    const pad = (n: number) => n.toString().padStart(2, '0');
    return `${now.getFullYear()}-${pad(now.getMonth() + 1)}-${pad(now.getDate())}T${pad(now.getHours())}:${pad(now.getMinutes())}`;
};

// --------------------------------------------------
// 参数变量
// --------------------------------------------------
const router = useRouter();
let timer: number | undefined; // 用于定时器

// --- Task related state ---
const new_task = ref<string>('');
const new_time = ref<string>(getDefaultTaskTime());
const new_importance = ref<string>('1'); // 默认重要性 '1' 对应 "重要且紧急"
const tasks = ref<Task[]>([]);
const currentTime = ref<Date>(new Date()); // 用于动态计算任务颜色

// --- Mailbox related state ---
const ismailboxvisible = ref<boolean>(false);
const isloadingemails = ref<boolean>(false);
const emails = ref<Email[]>([]);

// --------------------------------------------------
// 计算的属性
// --------------------------------------------------
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

const minDateTime = computed(() => {
    const now = new Date();
    const pad = (n: number) => n.toString().padStart(2, '0');
    return `${now.getFullYear()}-${pad(now.getMonth() + 1)}-${pad(now.getDate())}T${pad(now.getHours())}:${pad(now.getMinutes())}`;
});

// --------------------------------------------------
// 导航方法
// --------------------------------------------------
const ReturnIndex = () => {
    router.push({ name: 'index' });
};

// --------------------------------------------------
// 任务处理
// --------------------------------------------------
const FetchData = async () => {
    try {
        const response = await axios.get('/api/loadtask', {
            headers: { 'Content-Type': 'application/json' }
        });
        if (response.data.error) {
            ShowCustomModal(response.data.error);
            tasks.value = [];
        } else {
            tasks.value = response.data as Task[];
        }
    } catch (error) {
        ShowCustomModal(`获取任务失败: ${error instanceof Error ? error.message : String(error)}`);
        tasks.value = [];
    }
};

const AddTask = async () => {
    const content = new_task.value.trim();
    let time = new_time.value.trim();
    const importance = new_importance.value.trim(); // 使用选择的重要性

    if (!content) {
        ShowCustomModal('任务内容不能为空！');
        return;
    }

    if (!time) {
        time = getDefaultTaskTime();
    }

    const newTaskPayload: Omit<Task, 'id' | 'notified'> & { notified: boolean } = {
        name: content,
        isdelete: false,
        time: time,
        notified: false,
        importance: importance,
    };

    try {
        const response = await axios.post('/api/addtask', newTaskPayload, {
            headers: { 'Content-Type': 'application/json' }
        });

        if (response.data.error) {
            ShowCustomModal(`任务添加失败：${response.data.error}`);
        } else if (response.data.id) {
            const addedTask: Task = {
                ...newTaskPayload,
                id: response.data.id,
            };
            tasks.value.push(addedTask);
            new_task.value = '';
            new_time.value = getDefaultTaskTime();
            new_importance.value = '1'; // 重置为默认值
        } else {
            ShowCustomModal('任务添加失败，未返回有效ID ');
        }
    } catch (error) {
        console.error('添加任务失败:', error);
        ShowCustomModal(`添加任务时发生错误: ${error instanceof Error ? error.message : String(error)}`);
    }
};

const ToggleDelete = async (id: number) => {
    const task = tasks.value.find(t => t.id === id);
    if (!task) return;

    const newDeleteState = !task.isdelete;

    try {
        const response = await axios.put('/api/altertask', { id: id, isdelete: newDeleteState }, { // 假设后端也接收 isdelete 状态
            headers: { 'Content-Type': 'application/json' }
        });

        if (response.data.error) {
            ShowCustomModal(`更新任务失败：${response.data.error}`);
        } else {
            task.isdelete = newDeleteState;
        }
    } catch (err) {
        console.error('更新任务失败:', err);
        ShowCustomModal(`更新任务时发生错误: ${err instanceof Error ? err.message : String(err)}`);
    }
};

const ClearDeleted = async () => {
    try {
        const response = await axios.delete('/api/cleartasks');
        if (response.data.message) {
            ShowCustomModal(response.data.message);
            tasks.value = tasks.value.filter(t => !t.isdelete);
        } else if (response.data.error) {
            ShowCustomModal(`任务删除失败: ${response.data.error}`);
        } else {
            ShowCustomModal('清理已删任务操作完成，但未收到明确消息 ');
            tasks.value = tasks.value.filter(t => !t.isdelete);
        }
    } catch (err) {
        console.error('清理任务失败:', err);
        ShowCustomModal(`清理已删任务时发生错误: ${err instanceof Error ? err.message : String(err)}`);
    }
};

const formatTime = (timeStr: string): string => {
    if (!timeStr) return '无效时间';
    const date = new Date(timeStr);
    if (isNaN(date.getTime())) return '无效时间';
    return date.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
    });
};

const GetTaskColor = (task: Task): string => {
    if (task.isdelete) return '';
    const now = currentTime.value.getTime();
    const taskTime = new Date(task.time).getTime();

    if (isNaN(taskTime)) {
        return 'invalid-time';
    }

    const diff = taskTime - now;
    const one_day = 24 * 60 * 60 * 1000;
    const one_hour = 60 * 60 * 1000;
    const ten_minutes = 10 * 60 * 1000;

    if (diff <= 0) {
        return task.notified ? '' : 'color-near-ten-min';
    }
    if (diff <= ten_minutes && !task.notified) {
        return 'color-near-ten-min';
    } else if (diff <= one_hour) {
        return 'color-near-hour';
    } else if (diff <= one_day) {
        return 'color-near-day';
    }
    return '';
};



// --------------------------------------------------
// 邮箱处理
// --------------------------------------------------
const ToggleMailbox = () => {
    ismailboxvisible.value = !ismailboxvisible.value;
    // 首次打开邮箱且无数据时加载
    if (ismailboxvisible.value && emails.value.length === 0) {
        FetchMessage();
    }
};

const FetchMessage = async (showNotification: boolean = false) => {
    isloadingemails.value = true;
    try {
        const response = await axios.get('/api/loadmessages');
        if (response.data.error) 
        {
            ShowCustomModal(`获取邮件失败: ${response.data.error}`);
            emails.value = [];
        }
        else if (Array.isArray(response.data)) 
        {
            emails.value = response.data as Email[];
            if (showNotification && emails.value.length > 0) 
            {
                ShowCustomModal(`已成功加载 ${emails.value.length} 封邮件 `);
            } 
            else if (showNotification && emails.value.length === 0) 
            {
                ShowCustomModal(`邮箱为空`);
            }
        } 
        else {
            ShowCustomModal('获取邮件失败：响应数据格式不正确 ');
            emails.value = [];
        }
    } 
    catch (error: any) {
        if (error.response && error.response.status === 401) 
        {
            ShowCustomModal('会话已过期或未登录，请重新登录');
            ismailboxvisible.value = false; 
        } 
        else {
            ShowCustomModal(`获取邮件时发生错误: ${error.message || String(error)}`);
        }
        emails.value = [];
    } finally {
        isloadingemails.value = false;
    }
};

const ShowMessageDetails = (emailId: number) => { 
    const emailToShow = emails.value.find(e => e.id === emailId); 
    if (emailToShow) {
        ShowCustomModal(
            `邮件ID: ${emailToShow.id}\n` +
            `发送时间: ${formatTime(emailToShow.sent_at)}\n\n` +
            `内容:\n${emailToShow.message}`
        );
    } else {
        ShowCustomModal('未找到该邮件的详细信息 ');
    }
};


// --------------------------------------------------
// 更爱颜色样式
// --------------------------------------------------
const updateTaskColors = () => {
    currentTime.value = new Date();
};




// --------------------------------------------------
// 挂载
// --------------------------------------------------
onMounted(() => {
    FetchData();        // 页面加载时获取任务列表
    updateTaskColors(); // 初始调用以设置颜色并获取邮件 (根据 updateTaskColors 内部逻辑)
    timer = window.setInterval(updateTaskColors, 60000);
});

onUnmounted(() => {
    if (timer !== undefined) {
        clearInterval(timer);
    }
});

</script>


<template>

    <body class="todolist">
        <div class="container">
            <!-- A组合 —————————————————— -->
            <div class="button-container">
                <button class="return" @click="ReturnIndex">返回</button>
                <button class="message" @click="ToggleMailbox">邮箱</button>
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


        <transition name="mailbox-modal">
            <div class="mailbox-overlay" v-if="ismailboxvisible" @click.self="ToggleMailbox">
                <div class="mailbox-container">
                    <div class="mailbox-header">
                        <h3>邮箱</h3>
                        <button @click="FetchMessage(true)" :disabled="isloadingemails" class="refresh-mailbox-btn"
                            title="刷新邮件">🔄</button>
                        <button class="close-mailbox-btn" @click="ToggleMailbox">&times;</button>
                    </div>

                    <div class="mailbox-content">
                        <div v-if="isloadingemails" class="loading-indicator">
                            正在加载邮件...
                        </div>
                        <div v-else-if="emails.length === 0" class="empty-mailbox">
                            邮箱是空的 
                        </div>
                        <ul v-else class="email-list">
                            <li v-for="email in emails" :key="email.id" class="email-item"
                                @click="ShowMessageDetails(email.id)">
                                <span class="email-subject">邮件ID: {{ email.id }}</span> <!-- 实际应用中可能是邮件主题 -->
                                <span class="email-time">{{ formatTime(email.sent_at) }}</span>
                            </li>
                        </ul>
                    </div>

                    <div class="mailbox-footer">
                        <button @click="ToggleMailbox">关闭</button>
                    </div>
                </div>
            </div>
        </transition>

    </body>
</template>

<style scoped>
@import '@assets/todolist.css';
</style>
