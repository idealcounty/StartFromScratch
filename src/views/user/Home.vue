<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { Chatsend, ChatGuide,ChatEnd } from '../../api/chat';
import { userInfo } from "../../api/user.ts";
import { getArchive } from "../../api/archive.ts";

interface ChatMessage {
  content: string
  isAI: boolean
  timestamp: number
}

const points = ref({
  archiveHealth: 0,
  archiveSocial: 0,
  archiveGame: 0,
  archiveScience: 0,
  archiveMoney: 0,
})

const Message = ref('')
const messages = ref<ChatMessage[]>([])
const guideText = ref('') // 新增引导词响应式变量

// 获取引导词的独立方法
async function fetchGuide() {
  try {
    const res = await ChatGuide()
    guideText.value = res.data.guide
    // 将引导词作为第一条AI消息添加
    messages.value.push({
      content: res.data.guide,
      isAI: true,
      timestamp: Date.now()
    })
  } catch (error) {
    console.error('获取引导词失败:', error)
  }
}

function getpoints() {
  userInfo().then((res) => {
    getArchive(res.data.result.userId).then((res) => {
      console.log(res)
      points.value.archiveGame = res.data.result.archiveGame
      points.value.archiveSocial = res.data.result.archiveSocial
      points.value.archiveScience = res.data.result.archiveScience
      points.value.archiveMoney = res.data.result.archiveMoney
      points.value.archiveHealth = res.data.result.archiveHealth
      console.log(points)
    })
  })
}

// 初始化时获取数据和引导词
onMounted(() => {
  getpoints()
  fetchGuide() // 组件挂载时获取引导词
  drawRadarChart()
})

function handlechat() {
  if (!Message.value.trim()) return

  // 添加用户消息
  messages.value.push({
    content: Message.value,
    isAI: false,
    timestamp: Date.now()
  })

  const userMessage = Message.value
  Message.value = ''

  Chatsend({ message: userMessage }).then(res => {
    // 添加AI回复
    messages.value.push({
      content: res.data.reply,
      isAI: true,
      timestamp: Date.now()
    })
  }).catch(error => {
    // 错误处理
    messages.value.push({
      content: '暂时无法处理您的请求，请稍后再试',
      isAI: true,
      timestamp: Date.now()
    })
  })
}

function handleEnd(){
  ChatEnd().then((res)=>{
    console.log(res)
    messages.value.push({
      content: res.data.finalOutcome,
      isAI: true,
      timestamp: Date.now()
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
  const radius = 90
  const labels = ['健康', '社交', '娱乐', '学术', '财务']
  const values = Object.values(points.value)

  ctx.strokeStyle = '#ccc'
  ctx.lineWidth = 1

  // 绘制网格
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

  // 绘制数据
  ctx.strokeStyle = '#5d7a9c'
  ctx.fillStyle = 'rgba(93, 122, 156, 0.5)'
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

  // 添加维度标签
  ctx.fillStyle = '#5d7a9c'
  for (let i = 0; i < 5; i++) {
    const angle = (Math.PI * 2 / 5) * i - Math.PI / 2
    const labelRadius = radius + 20
    const labelX = centerX + Math.cos(angle) * labelRadius
    const labelY = centerY + Math.sin(angle) * labelRadius
    ctx.font = 'bold 17px Arial'
    ctx.textAlign = 'center'
    ctx.textBaseline = 'middle'
    ctx.fillText(labels[i], labelX, labelY)
  }
}

onMounted(drawRadarChart)
watch(points, drawRadarChart, { deep: true })
</script>

<template>
  <div class="container">
    <div class="chat-container">
      <div class="chat-header">
        <h2>Welcome</h2>
        <p>开始你的nju生活吧</p>
      </div>

      <div class="chat-body">
        <div
            v-for="(msg, index) in messages"
            :key="index"
            class="message"
            :class="{ 'user': !msg.isAI, 'ai': msg.isAI }"
        >
          <div class="message-bubble">
            {{ msg.content }}
          </div>
        </div>
      </div>

      <div class="chat-input">
        <input
            id="Message"
            placeholder="输入你的消息..."
            v-model.trim="Message"
            @keyup.enter="handlechat"
        >
        <button @click="handlechat">发送</button>
      </div>
    </div>

    <div class="stats-container">
      <h3>成长档案</h3>

      <div class="radar-chart-wrapper">
        <canvas ref="canvasRef" width="250" height="250"></canvas>
      </div>

      <div class="stats-grid">
        <div class="stat-item health">
          <div class="stat-icon">❤️</div>
          <div class="stat-info">
            <span class="stat-label">健康</span>
            <span class="stat-value">{{ points.archiveHealth }}</span>
          </div>
        </div>
        <div class="stat-item social">
          <div class="stat-icon">👥</div>
          <div class="stat-info">
            <span class="stat-label">社交</span>
            <span class="stat-value">{{ points.archiveSocial }}</span>
          </div>
        </div>
        <div class="stat-item game">
          <div class="stat-icon">🎮</div>
          <div class="stat-info">
            <span class="stat-label">娱乐</span>
            <span class="stat-value">{{ points.archiveGame }}</span>
          </div>
        </div>
        <div class="stat-item science">
          <div class="stat-icon">🔬</div>
          <div class="stat-info">
            <span class="stat-label">学术</span>
            <span class="stat-value">{{ points.archiveScience }}</span>
          </div>
        </div>
        <div class="stat-item money">
          <div class="stat-icon">💵</div>
          <div class="stat-info">
            <span class="stat-label">财务</span>
            <span class="stat-value">{{ points.archiveMoney }}</span>
          </div>
        </div>
      </div>
    </div>
    <div class="end-game-wrapper">
      <button class="end-game-btn" @click="handleEnd">结束游戏</button>
    </div>
  </div>
</template>

<style scoped>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
}

.container {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 20px;
  align-items: start;
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.chat-container {
  width: 100%;
  max-width: 800px;
  background-color: rgba(255, 255, 255, 0.2);
  border-radius: 15px;
  padding: 30px;
  backdrop-filter: blur(10px);
  box-shadow: 0 10px 30px rgba(93, 122, 156, 0.15);
}

.chat-header {
  text-align: center;
  margin-bottom: 30px;
  color: white;
}

.chat-header h2 {
  font-size: 24px;
  margin-bottom: 5px;
}

.chat-header p {
  font-size: 14px;
  opacity: 0.8;
}

.chat-body {
  height: 60vh;
  overflow-y: auto;
  padding: 20px;
  margin-bottom: 20px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 10px;
}

.message {
  margin-bottom: 15px;
  transition: all 0.3s ease;
}

.message-bubble {
  max-width: 75%;
  padding: 12px 18px;
  border-radius: 18px;
  word-break: break-word;
  position: relative;
  animation: fadeIn 0.3s ease;
}

.message.ai .message-bubble {
  background: rgba(255, 255, 255, 0.9);
  color: #333;
  border-radius: 18px 18px 18px 4px;
}

.message.user .message-bubble {
  background: linear-gradient(135deg, #5d7a9c, #3d5a7c);
  color: white;
  border-radius: 18px 18px 4px 18px;
  margin-left: auto;
}

.chat-body::-webkit-scrollbar {
  width: 6px;
}

.chat-body::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.1);
}

.chat-body::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.4);
  border-radius: 4px;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.message {
  display: flex;
  align-items: flex-start;
  gap: 10px;
}

.message.ai {
  justify-content: flex-start;
}

.message.user {
  justify-content: flex-end;
}

.message-bubble {
  max-width: 70%;
  padding: 12px 15px;
  border-radius: 18px;
  font-size: 14px;
  line-height: 1.4;
}

.message.ai .message-bubble {
  background-color: rgba(255, 255, 255, 0.8);
  color: #333;
  border-top-left-radius: 5px;
}

.message.user .message-bubble {
  background-color: #5d7a9c;
  color: white;
  border-top-right-radius: 5px;
}

.chat-input {
  display: flex;
  gap: 10px;
}

.chat-input input {
  flex: 1;
  padding: 12px 15px;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  outline: none;
}

.chat-input button {
  padding: 12px 20px;
  background: linear-gradient(to right, #5d7a9c, #3d5a7c);
  border: none;
  border-radius: 8px;
  color: white;
  font-size: 14px;
  cursor: pointer;
  transition: background 0.3s;
}

.chat-input button:hover {
  background: linear-gradient(to right, #4d6a8c, #2d4a6c);
}

.stats-container {
  background: rgba(255, 255, 255, 0.2);
  border-radius: 15px;
  padding: 20px;
  backdrop-filter: blur(10px);
  box-shadow: 0 10px 30px rgba(93, 122, 156, 0.15);
  color: #5d7a9c;
}

.stats-container h3 {
  text-align: center;
  margin-bottom: 1.5rem;
  font-size: 1.5rem;
  text-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.stats-grid {
  display: grid;
  gap: 15px;
}

.stat-item {
  display: flex;
  align-items: center;
  padding: 12px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  transition: transform 0.2s ease;
}

.stat-item:hover {
  transform: translateY(-2px);
}

.stat-icon {
  font-size: 1.8rem;
  margin-right: 15px;
  width: 40px;
  text-align: center;
}

.stat-info {
  display: flex;
  flex-direction: column;
}

.stat-label {
  font-size: 0.9rem;
  opacity: 0.8;
  margin-bottom: 4px;
}

.stat-value {
  font-size: 1.4rem;
  font-weight: bold;
}

.health .stat-icon { color: #e74c3c; }
.social .stat-icon { color: #3498db; }
.game .stat-icon { color: #f39c12; }
.science .stat-icon { color: #5d7a9c; }
.money .stat-icon { color: #27ae60; }

@media (max-width: 768px) {
  .container {
    grid-template-columns: 1fr;
  }

  .stats-container {
    order: -1;
    margin-bottom: 20px;
  }
}

.radar-chart-wrapper {
  display: flex;
  justify-content: center;
  margin-bottom: 1.5rem;
  padding: 10px;
}

.end-game-wrapper {
  grid-column: 1 / -1;
  margin-top: 30px;
  display: flex;
  justify-content: center;
}

.end-game-btn {
  padding: 12px 40px;
  background: linear-gradient(135deg, #ff6b6b 0%, #ff4757 100%);
  border: none;
  border-radius: 25px;
  color: white;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(255, 107, 107, 0.3);
}

.end-game-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(255, 107, 107, 0.4);
}

.end-game-btn:active {
  transform: translateY(1px);
}

@media (max-width: 768px) {
  .end-game-wrapper {
    margin-top: 20px;
  }

  .end-game-btn {
    width: 100%;
    max-width: 300px;
  }
}
</style>
