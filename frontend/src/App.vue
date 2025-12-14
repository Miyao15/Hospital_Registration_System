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

<style lang="scss">
#app {
  width: 100%;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
</style>
