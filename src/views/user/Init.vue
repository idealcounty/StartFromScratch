<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
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

const filteredPoints = computed(() => {
  const { userId, ...rest } = points.value;
  return rest;
});

const totalPoints = computed(() =>
    Object.values(filteredPoints.value).reduce((a, b) => a + b, 0)
)

const remainingPoints = computed(() => 350 - totalPoints.value)

function handleInput(name: string, e: Event) {
  const inputValue = parseInt((e.target as HTMLInputElement).value, 10) || 0
  const currentValue = points.value[name as keyof typeof points.value]
  const otherSum = totalPoints.value - currentValue
  const maxAllowed = Math.min(100, 350 - otherSum)
  const adjustedValue = Math.min(inputValue, maxAllowed)
  points.value[name as keyof typeof points.value] = Math.max(0, adjustedValue)
}

function handleConfirm() {
  userInfo().then(res => {
    points.value.userId = res.data.result.userId
    archiveCreate(points.value).then(() => {
      router.push({ path: '/' })
    })
  })
}

const canvasRef = ref<HTMLCanvasElement | null>(null)

function drawRadarChart() {
  const canvas = canvasRef.value
  if (!canvas) return
  const ctx = canvas.getContext('2d')
  if (!ctx) return

  const width = canvas.width
  const height = canvas.height
  ctx.clearRect(0, 0, width, height)

  const centerX = width / 2
  const centerY = height / 2
  const radius = 100
  const labels = Object.keys(filteredPoints.value)
  const values = Object.values(filteredPoints.value)

  ctx.strokeStyle = '#ccc'
  ctx.lineWidth = 1

  for (let i = 1; i <= 5; i++) {
    ctx.beginPath()
    for (let j = 0; j < 5; j++) {
      const angle = (Math.PI * 2 / 5) * j - Math.PI / 2
      const x = centerX + Math.cos(angle) * (radius * i / 5)
      const y = centerY + Math.sin(angle) * (radius * i / 5)
      if (j === 0) ctx.moveTo(x, y)
      else ctx.lineTo(x, y)
    }
    ctx.closePath()
    ctx.stroke()
  }

  ctx.strokeStyle = '#9c27b0'
  ctx.fillStyle = 'rgba(156, 39, 176, 0.4)'
  ctx.beginPath()
  for (let i = 0; i < 5; i++) {
    const angle = (Math.PI * 2 / 5) * i - Math.PI / 2
    const valueRatio = values[i] / 100
    const x = centerX + Math.cos(angle) * radius * valueRatio
    const y = centerY + Math.sin(angle) * radius * valueRatio
    if (i === 0) ctx.moveTo(x, y)
    else ctx.lineTo(x, y)
  }
  ctx.closePath()
  ctx.stroke()
  ctx.fill()
}

onMounted(drawRadarChart)
watch(points, drawRadarChart, { deep: true })
</script>

<template>
  <div class="container">
    <h1>决定你的初始属性 <span class="remaining">剩余点数：{{ remainingPoints }}</span></h1>

    <div class="main-grid">
      <div class="radar-chart-wrapper">
        <canvas ref="canvasRef" width="250" height="250"></canvas>
      </div>

      <div class="category-list">
        <div
            v-for="(value, name) in filteredPoints"
            :key="name"
            class="category-card"
        >
          <div class="category-header">
            {{ name.toUpperCase().replace('ARCHIVE', '') }}
          </div>

          <div class="controls">
            <input
                type="range"
                v-model.number="points[name]"
                :min="0"
                :max="100"
                @input="handleInput(name, $event)"
            class="points-input"
            >
            <div class="value-display">{{ points[name] }}</div>
          </div>
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
  max-width: 1000px;
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

.main-grid {
  display: flex;
  gap: 2rem;
  justify-content: space-between;
  align-items: flex-start;
  flex-wrap: wrap;
}

.radar-chart-wrapper {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  min-width: 280px;
}

.category-list {
  flex: 2;
  display: flex;
  flex-direction: column;
  gap: 1.2rem;
  min-width: 280px;
}

.category-card {
  background: white;
  padding: 1rem 1.5rem;
  border-radius: 15px;
  box-shadow: 0 4px 16px rgba(149, 69, 199, 0.1);
  display: flex;
  flex-direction: column;
}

.category-header {
  color: #9c27b0;
  font-weight: 600;
  font-size: 1.1rem;
  margin-bottom: 0.5rem;
  text-align: center;
}

.controls {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.points-input {
  flex: 1;
}

.value-display {
  width: 30px;
  text-align: right;
  font-weight: bold;
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
