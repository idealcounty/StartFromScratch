<script setup lang="ts">
import { ref } from 'vue'
import { Chatsend } from '../../api/chat';

interface ChatMessage {
  content: string
  isAI: boolean
  timestamp: number
}

const Message = ref('')
const messages = ref<ChatMessage[]>([])

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
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 80px);
  padding: 0 20px;
  background: linear-gradient(135deg, #a18cd1, #fbc2eb);
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

/* 滚动条样式 */
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
</style>