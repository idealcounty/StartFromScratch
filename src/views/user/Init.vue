<script setup lang="ts">
import { ref, computed } from 'vue'
import { archiveCreate, archiveInfo } from "../../api/archive.ts";
import { useRouter } from 'vue-router'
import { userInfo } from "../../api/user.ts";

const router = useRouter()
const points = ref<archiveInfo>({
  archiveHealth: 0,
  archiveSocial: 0,
  archiveGame: 0,
  archiveScience: 0,
  archiveMoney: 0,
  userId: 0,
})

const totalPoints = computed(() =>
    Object.values(points.value).reduce((a, b) => a + b, 0)
)

const remainingPoints = computed(() => 350 - totalPoints.value)

function handleInput(category: keyof typeof points.value, e: Event) {
  const inputValue = parseInt((e.target as HTMLInputElement).value, 10) || 0
  const currentValue = points.value[category]
  const otherSum = totalPoints.value - currentValue
  const maxAllowed = Math.min(100, 350 - otherSum)
  const adjustedValue = Math.min(inputValue, maxAllowed)
  points.value[category] = Math.max(0, adjustedValue)
}

function handleConfirm() {
  userInfo().then(res => {
    points.value.userId = res.data.result.userId
    archiveCreate(points.value).then(() => {
      router.push({ path: '/' })
    })
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
        <div class="category-header" v-if="name !== 'userId'">
          {{ name.toUpperCase().replace('ARCHIVE', '') }}
        </div>

        <div class="controls" v-if="name !== 'userId'">
          <input
              type="number"
              v-model.number="points[name]"
              :min="0"
              :max="100"
              @input="handleInput(name as keyof typeof points, $event)"
              class="points-input"
          >
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
}

.category-header {
  color: #9c27b0;
  font-weight: 600;
  font-size: 1.1rem;
  margin-bottom: 1rem;
  text-align: center;
}

.points-input {
  width: 100%;
  padding: 0.8rem;
  border: 2px solid #e91e63;
  border-radius: 10px;
  font-size: 1.1rem;
  text-align: center;
  transition: border-color 0.3s ease;
}

.points-input:focus {
  outline: none;
  border-color: #9c27b0;
  box-shadow: 0 0 8px rgba(156, 39, 176, 0.3);
}

input[type="number"]::-webkit-inner-spin-button,
input[type="number"]::-webkit-outer-spin-button {
  -webkit-appearance: none;
  margin: 0;
}

input[type="number"] {
  -moz-appearance: textfield;
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