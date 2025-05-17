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






// E. 按钮设置
const Return = ()=>
{
    router.push({"name":"adminindex"})
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
                                        <button class="action-button message">提醒</button>
                                        <button class=" action-button delete" @click="DeleteUser(user.id)">删除</button>
                                    </div>
                                </td>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </main>




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