<template>
  <div class="triage-page">
    <div class="progress-bar"></div>
    <div class="skip-link">
      <router-link :to="{ path: '/search-results', query: route.query }">Skip to search results</router-link>
    </div>
    <div class="triage-container">
      <h1 class="triage-title">What type of care are you looking for?</h1>
      <div class="card-group">
        <div class="care-card" @click="go_to_results">
          <h2 class="card-title">Annual physical</h2>
          <p class="card-subtitle">A comprehensive preventive exam.</p>
        </div>
        <div class="care-card" @click="go_to_results">
          <h2 class="card-title">I need care for an issue</h2>
          <p class="card-subtitle">Address a specific symptom or condition.</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter, useRoute } from 'vue-router';

const router = useRouter();
const route = useRoute();

const go_to_results = () => {
  router.push({ path: '/search-results', query: route.query });
};
</script>

<style scoped lang="scss">
.triage-page {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: flex-start;
  min-height: 100vh;
  background-color: #f0f2f5;
  padding-top: 1rem;
  position: relative;
}

.progress-bar {
  position: absolute;
  top: 0;
  left: 0;
  width: 50%;
  height: 4px;
  background-color: #007bff;
}

.skip-link {
  position: absolute;
  top: 1rem;
  right: 2rem;
  font-size: 0.9rem;
  a {
    color: #555;
    text-decoration: none;
    &:hover {
      text-decoration: underline;
    }
  }
}

.triage-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  width: 100%;
  max-width: 600px;
  flex-grow: 1;
  padding: 2rem;
}

.triage-title {
  font-size: 2.5rem;
  font-weight: bold;
  margin-bottom: 3rem;
  color: #333;
}

.card-group {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
  width: 100%;
}

.care-card {
  background-color: #ffffff;
  border: 1px solid #e0e0e0;
  border-radius: 12px;
  padding: 2rem;
  cursor: pointer;
  transition: all 0.2s ease-in-out;
  text-align: left;

  &:hover {
    border-color: #007bff;
    background-color: #f8f9fa;
    transform: translateY(-5px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  }

  .card-title {
    font-size: 1.5rem;
    font-weight: bold;
    margin: 0 0 0.5rem 0;
    color: #333;
  }

  .card-subtitle {
    font-size: 1rem;
    color: #666;
    margin: 0;
  }
}
</style>
