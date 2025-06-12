<template>
    <body>
        <router-view />
    </body>
    <div v-if="isModalVisible" class="modal-overlay" @click.self="CloseCustomModal">
      <div class="modal-content">
        <p class="modal-message" v-html="modalMessage.replace(/\n/g, '<br>')"></p>
        <button class="modal-close-button" @click="CloseCustomModal">关闭</button>
      </div>
    </div>

</template>

<script setup lang="ts">
import { isModalVisible, modalMessage, CloseCustomModal } from './ts/model.ts'; 
</script>

<style>
@import './assets/css/base.css';
@import '@assets/model.css'; 

</style>