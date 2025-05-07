<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { Chatsend } from '../../api/chat';
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
getpoints();

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

  // 添加维度标签
  ctx.fillStyle = '#9c27b0'
  for (let i = 0; i < 5; i++) {
    const angle = (Math.PI * 2 / 5) * i - Math.PI / 2
    const labelX = centerX + Math.cos(angle) * (radius + 15)
    const labelY = centerY + Math.sin(angle) * (radius + 15)
    ctx.font = 'bold 12px Arial'
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
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
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
  background: linear-gradient(135deg, #6e8efb, #a777e3);
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
  background-color: #6e8efb;
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
  background: linear-gradient(to right, #6e8efb, #a777e3);
  border: none;
  border-radius: 8px;
  color: white;
  font-size: 14px;
  cursor: pointer;
  transition: background 0.3s;
}

.chat-input button:hover {
  background: linear-gradient(to right, #5d7df9, #9464d0);
}

.stats-container {
  background: rgba(255, 255, 255, 0.2);
  border-radius: 15px;
  padding: 20px;
  backdrop-filter: blur(10px);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  color: purple;
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

.health .stat-icon { color: #ff6b6b; }
.social .stat-icon { color: #4ecdc4; }
.game .stat-icon { color: #ff9f43; }
.science .stat-icon { color: #5f27cd; }
.money .stat-icon { color: #2ecc71; }

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
}
</style>
