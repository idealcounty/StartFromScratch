<script setup>
// 如果你使用的是 App.vue 或主 layout
import GlobalHeader from './components/GlobalHeader.vue'
</script>

<template>
  <div class="app-wrapper">
    <GlobalHeader />
    <router-view />
  </div>
</template>

<style>
/* 全局页面背景设置 */
.app-wrapper {
  min-height: 100vh;
  background-color: #0f0f1a;
  background-image: url('assets/bg.png');
  background-repeat: no-repeat;
  background-position: center center;
  background-size: cover;
  background-attachment: fixed;
  position: relative;
  overflow-x: hidden;
}

/* 添加媒体查询以确保在不同设备上的适配 */
@media (max-width: 768px) {
  .app-wrapper {
    background-size: cover;
    background-position: center center;
  }
}
</style>
