<script setup lang="ts">
import axios from 'axios';
import { ref, onMounted, } from 'vue';
import { ShowCustomModal } from '../ts/model';
import { useRouter } from 'vue-router';

interface User {
    id: number;
    user_name: string;
    email: string;
}

// 用户数组
const Users = ref<User[]>([])

const router = useRouter()

// 要删除的用户
const user_to_delete = ref<User | null>(null);

// 如果点击了删除
const is_confirm_visible = ref(false)








// 关闭确定按钮
const CloseConfirmModal = () => {
    is_confirm_visible.value = false;
    user_to_delete.value = null;
}

const PrepareDeleteUser = (user: User) => {
    user_to_delete.value = user;
    is_confirm_visible.value = true;
};






const FetchData = async () => {

    /**
     * 获取数据
     */

    try {
        const response = await axios.get('/api/loaduser', {
            headers: {
                'Content-Type': 'application/json'
            }
        });

        // 后端服务器错误 
        if (response.status == 500) {
            ShowCustomModal(response.data.error + "服务器错误500");
            return
        }

        Users.value = response.data;
    }
    catch (error) {
        console.error('获取用户信息失败', error);
    }
};




// B 挂载的时候触发
onMounted(() => {
    FetchData();
});



// C 对用户信息进行操作
const DeleteUser = async () => {

    const response = await axios.post('/api/deleteuser', {
        "userid": user_to_delete.value?.id
    },
        {
            headers: {
                'Content-Type': 'application/json'
            }
        }
    );
    // TODO: √
    if (response.status == 200) {
        Users.value = Users.value.filter(t => t.id !== user_to_delete.value?.id)
        ShowCustomModal(response.data.message)
    }
    else {
        // TODO: √
        if (response.data.error) {
            ShowCustomModal(response.data.error)
        }
    }

    // 关闭
    CloseConfirmModal()
}


const is_message_editorvisible = ref(false);

const message_content = ref('');

const target_user_for_message = ref<User | null>(null);



const SendMessage = async () => {

    if (!target_user_for_message.value || !message_content.value.trim()) {
        ShowCustomModal("请选择用户并输入消息内容。");
        return;
    }

    try {
        const response = await axios.post('/api/sendmessage', {
            "message": message_content.value.trim(),
            "id": target_user_for_message.value.id
        },
            {
                headers: {
                    'Content-Type': 'application/json'
                }
            });

        if (response.status === 201 && response.data && response.data.message) {
            ShowCustomModal(response.data.message);
            closeMessageEditor();
        } else if (response.data && response.data.error) {
            ShowCustomModal(response.data.error);
        } else if (response.status !== 201) {
            ShowCustomModal(`发送失败，状态码: ${response.status}`);
        }

    } catch (error: any) {
        console.error("发送消息失败:", error);
        if (error.response && error.response.data && error.response.data.error) {
            ShowCustomModal(`发送消息失败: ${error.response.data.error}`);
        } else {
            ShowCustomModal("发送消息失败，请查看控制台了解详情");
        }
    }
};

const ShowMessage = (userId: number) => {

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
                                        <button class="action-button message" @click="ShowMessage(user.id)">提醒</button>
                                        <button class=" action-button delete"
                                            @click="PrepareDeleteUser(user)">删除</button>
                                    </div>
                                </td>

                                </td>
                            </tr>
                        </tbody>

                    </table>


                </div>
            </main>


            <div v-if="is_confirm_visible" class="modal-overlay" @click.self="CloseConfirmModal">
                <div class="modal-content confirm-dialog">
                    <h3><i class="fas fa-exclamation-triangle"></i> 确认删除用户</h3>
                    <p>您确定要删除用户 <strong>{{ user_to_delete ? user_to_delete.user_name : '' }}</strong> (ID: {{
                        user_to_delete ? user_to_delete.id : '' }}) 吗？<br>
                        此操作将永久删除该用户的所有数据，且无法恢复。</p>
                    <div class="confirm-actions">
                        <button @click="CloseConfirmModal" class="confirm-cancel">
                            <i class="fas fa-times"></i> 取消
                        </button>
                        <button @click="DeleteUser()" class="confirm-delete">
                            <i class="fas fa-trash-alt"></i> 确认删除
                        </button>
                    </div>
                </div>
            </div>


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