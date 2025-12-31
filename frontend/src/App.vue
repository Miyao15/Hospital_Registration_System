<template>
  <div id="app">
    <router-view />
    <AIAssistant />
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import AIAssistant from '@/components/AIAssistant.vue'

onMounted(() => {
  console.log('医院门诊预约挂号系统已启动')
})
</script>

<style>
#app {
  width: 100%;
  min-height: 100vh;
  background: #fffef6; /* light warm background to match yellow/white theme */
}
</style>
