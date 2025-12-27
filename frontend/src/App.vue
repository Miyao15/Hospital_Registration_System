<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script setup>
import { onMounted } from 'vue'

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
