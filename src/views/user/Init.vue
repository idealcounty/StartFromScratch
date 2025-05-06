<script setup lang="ts">
import { ref, computed } from 'vue'
import {archiveCreate,archiveInfo} from "../../api/archive.ts";
import { useRouter } from 'vue-router'

const router = useRouter()
const points = ref<archiveInfo>({
  archiveHealth: 0,
  archiveSocial: 0,
  archiveGame: 0,
  archiveScience: 0,
  archiveMoney: 0
})

const totalPoints = computed(() =>
    Object.values(points.value).reduce((a, b) => a + b, 0)
)

const remainingPoints = computed(() => 20 - totalPoints.value)

function increment(category: keyof typeof points.value) {
  if (points.value[category] < 10 && remainingPoints.value > 0) {
    points.value[category]++
  }
}

function decrement(category: keyof typeof points.value) {
  if (points.value[category] > 0) {
    points.value[category]--
  }
}

function handleConfirm() {
  archiveCreate(points.value).then(res=>{
    console.log(res)
    router.push({path:'/'})
  })
}
</script>

<template>
  <div class="container">
    <h1>决定你的初始属性 <span class="remaining">剩余点数：{{ remainingPoints }}</span></h1>

    <div class="category-grid">
      <div
          v-for="(value, name) in points"
          :key="name"
          class="category-card"
      >
        <div class="category-header">
          {{ name.toUpperCase() }}
          <span class="current-value">{{ value }}</span>
        </div>

        <div class="controls">
          <button
              @click="decrement(name as keyof typeof points)"
              :disabled="value === 0"
              class="control-btn"
          >-</button>

          <div class="progress-bar">
            <div
                class="progress-fill"
                :style="{ width: `${(value / 10) * 100}%` }"
            ></div>
          </div>

          <button
              @click="increment(name as keyof typeof points)"
              :disabled="value === 10 || remainingPoints === 0"
              class="control-btn"
          >+</button>
        </div>
      </div>
    </div>

    <div class="confirm-section">
      <button
          @click="handleConfirm"
          :disabled="remainingPoints !== 0"
          class="confirm-btn"
      >
        确认分配
        <span class="button-glow"></span>
      </button>
    </div>
  </div>
</template>

<style scoped>
.container {
  max-width: 800px;
  margin: 2rem auto;
  padding: 2rem;
  background: linear-gradient(145deg, #f0e6ff, #ffe6f7);
  border-radius: 20px;
  box-shadow: 0 8px 32px rgba(149, 69, 199, 0.1);
}

h1 {
  color: #6a1b9a;
  text-align: center;
  margin-bottom: 2rem;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 1rem;
}

.remaining {
  font-size: 1.2rem;
  color: #e91e63;
  background: rgba(233, 30, 99, 0.1);
  padding: 0.5rem 1rem;
  border-radius: 10px;
}

.category-grid {
  display: grid;
  gap: 1.5rem;
}

.category-card {
  background: white;
  padding: 1.5rem;
  border-radius: 15px;
  box-shadow: 0 4px 16px rgba(149, 69, 199, 0.1);
  transition: transform 0.2s;
}

.category-card:hover {
  transform: translateY(-3px);
}

.category-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
  color: #9c27b0;
  font-weight: 600;
  font-size: 1.1rem;
}

.current-value {
  background: #f3e5f5;
  padding: 0.3rem 0.8rem;
  border-radius: 8px;
}

.controls {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.control-btn {
  width: 40px;
  height: 40px;
  border: none;
  border-radius: 50%;
  background: linear-gradient(145deg, #e91e63, #9c27b0);
  color: white;
  font-size: 1.2rem;
  cursor: pointer;
  transition: all 0.2s;
}

.control-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.control-btn:not(:disabled):hover {
  transform: scale(1.1);
  box-shadow: 0 4px 12px rgba(233, 30, 99, 0.3);
}

.progress-bar {
  flex: 1;
  height: 12px;
  background: #f3e5f5;
  border-radius: 6px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #e91e63, #9c27b0);
  transition: width 0.3s ease;
}



@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

.confirm-section {
  margin-top: 2rem;
  text-align: center;
}

.confirm-btn {
  position: relative;
  padding: 1rem 2.5rem;
  font-size: 1.1rem;
  border: none;
  border-radius: 30px;
  background: linear-gradient(135deg, #e91e63, #9c27b0);
  color: white;
  cursor: pointer;
  transition: all 0.3s ease;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(233, 30, 99, 0.2);
}

.confirm-btn:disabled {
  opacity: 0.7;
  background: linear-gradient(135deg, #cccccc, #999999);
  cursor: not-allowed;
  box-shadow: none;
}

.confirm-btn:not(:disabled):hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 25px rgba(233, 30, 99, 0.3);
}

.confirm-btn:not(:disabled):active {
  transform: translateY(1px);
}

.button-glow {
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(
      90deg,
      rgba(255, 255, 255, 0) 0%,
      rgba(255, 255, 255, 0.3) 50%,
      rgba(255, 255, 255, 0) 100%
  );
  animation: glowSlide 2s infinite;
}

@keyframes glowSlide {
  0% { left: -100%; }
  100% { left: 100%; }
}
</style>