<script setup lang="ts">
import axios from 'axios';
import { ref, onMounted, } from 'vue';
import { ShowCustomModal } from '../ts/model';
import { useRouter } from 'vue-router';

// A 获取后端用户的数据
// B 挂载的时候触发
// C 对用户信息进行操作
// D 弹窗信息


interface User {
    id: number;
    user_name: string;
    email: string;
}
const Users = ref<User[]>([])
const router = useRouter()


// A 获取后端用户的数据
const FetchData = async () => {
    try {
        const response = await axios.get('/api/loaduser', {
            headers: {
                'Content-Type': 'application/json'
            }
        });

        // 后端服务器错误 
        if (response.status == 500) {
            ShowCustomModal(response.data.error + ":服务器错误500");
        }

        Users.value = response.data;
    }
    catch (error) {
        console.error('获取用户信息失败', error);
    }

    // debug 
    // Users.value = [
    //     { id: 1, user_name: '刘备', email: 'liubei@shuhan.com' },
    //     { id: 2, user_name: '关羽', email: 'guanyu@shuhan.com' },
    //     { id: 3, user_name: '张飞', email: 'zhangfei@shuhan.com' },
    //     { id: 4, user_name: '曹操', email: 'caocao@caowei.com' },
    //     { id: 5, user_name: '孙权', email: 'sunquan@sunwu.com' },
    // ];
};


// B 挂载的时候触发
onMounted(() => {
    FetchData();
});


// C 对用户信息进行操作
const DeleteUser = async (userid: number) => {
    const response = await axios.post('/api/deleteuser', {
        "userid": userid
    },
        {
            headers: {
                'Content-Type': 'application/json'
            }
        }
    );
    // TODO: √
    if (response.status == 200) {
        Users.value = Users.value.filter(t => t.id !== userid)
        ShowCustomModal(response.data.message)
    }
    else {
        // TODO: √
        if (response.data.error) {
            ShowCustomModal(response.data.error)
        }
    }


}
const is_message_editorvisible = ref(false);
const message_content = ref('');
const target_user_for_message = ref<User | null>(null);

const SendMessage = async () => {
    if (!target_user_for_message.value || !message_content.value.trim()) {
        ShowCustomModal("请选择用户并输入消息内容。");
        return;
    }

    // 发送消息
    // TODO:s
    try {
        const response = await axios.post('/api/sendmessage', {
            "message": message_content.value,
            "id": target_user_for_message.value.id
        },
            {
                headers: {
                    'Content-Type': 'application/json'
                }
            })
        if (response.status == 201) {
            if (response.data.message) {
            ShowCustomModal(`${response.data.message}`)
        }
        }
        if (response.data.error) {
            ShowCustomModal(`${response.data.message}`)
        }
    }
    catch (error: any) {
        console.error("发送消息失败:", error);
    }
}

const ShowMassage = (userId: number) => {
    const user = Users.value.find(u => u.id === userId);
    if (user) {
        target_user_for_message.value = user;
        message_content.value = "本网站仅供个人学习,不可用于商业目的!";
        is_message_editorvisible.value = true;
    }
    else {
        ShowCustomModal(`错误:找不到ID为 ${userId} 的用户。`);
    }
};
const closeMessageEditor = () => {
    is_message_editorvisible.value = false;
    message_content.value = '';
    target_user_for_message.value = null;
};

// E. 按钮设置
const Return = () => {
    router.push({ "name": "index" })
}

</script>
<template>
    <body>
        <div class="container">
            <button class="return" id="return" @click="Return">返回</button>
            <!-- A标题部分 -->
            <header>
                <h1>用户管理</h1>
            </header>

            <!-- B 用户部分 -->
            <main>
                <div class="user-table-container">
                    <table>
                        <thead>
                            <tr>
                                <th>用户ID</th>
                                <th>用户名</th>
                                <th>邮箱</th>
                                <th>操作</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr v-if="Users.length === 0">
                                <td colspan="4" style="text-align: center;">暂无用户...</td>
                            </tr>
                            <tr v-for="user in Users" :key="user.id">
                                <td>{{ user.id }}</td>
                                <td>{{ user.user_name }}</td>
                                <td>{{ user.email }}</td>
                                <td>
                                <td>
                                    <div class="actions">
                                        <button class="action-button message" @click="ShowMassage(user.id)">提醒</button>
                                        <button class=" action-button delete" @click="DeleteUser(user.id)">删除</button>
                                    </div>
                                </td>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </main>

            <!-- D 消息发送部分 -->
            <div v-if="is_message_editorvisible" class="modal-overlay message-editor-overlay"
                @click.self="closeMessageEditor">
                <div class="modal-content message-editor-content">
                    <h3 v-if="target_user_for_message">发送提醒给：{{ target_user_for_message.user_name }}</h3>
                    <textarea v-model="message_content" placeholder="在此输入提醒内容..." rows="6"
                        class="message-textarea"></textarea>
                    <div class="message-editor-actions">
                        <button @click="SendMessage" class="button-send">发送</button>
                        <button @click="closeMessageEditor" class="button-cancel">取消</button>
                    </div>
                </div>
            </div>

            <!-- C 版权部分 -->
            <footer>
                <p>&copy; MindAdmin</p>
            </footer>
        </div>

    </body>
</template>

<style scoped>
@import '@assets/adminindex.css';
</style>