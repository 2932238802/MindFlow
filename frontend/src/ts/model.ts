import { ref, readonly } from 'vue'; 

const _isModalVisible = ref(false);
const _modalMessage = ref('');  

export const isModalVisible = readonly(_isModalVisible);
export const modalMessage = readonly(_modalMessage);

// 操作状态的函数
export const ShowCustomModal = (message: string) => {
    _modalMessage.value = message;
    _isModalVisible.value = true;
};

export const CloseCustomModal = () => {
    _isModalVisible.value = false;
    _modalMessage.value = ''; 
};
