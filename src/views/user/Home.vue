<script setup lang="ts">
</script>

<template>
  <div class="container">
    <div class="chat-container">
      <div class="chat-header">
        <h2>Welcome</h2>
        <p>开始你的nju生活吧</p>
      </div>

      <div class="chat-body">
        <div class="message ai">
          <div class="message-bubble">
            你好！欢迎来到AI对话世界，请问有什么我可以帮助你的吗？
          </div>
        </div>
        <div class="message user">
          <div class="message-bubble">
            你好，我很好奇AI是如何学习的？
          </div>
        </div>
        <div class="message ai">
          <div class="message-bubble">
            AI通过大量的数据进行训练，使用复杂的算法不断优化自身。就像人类通过学习和经验增长知识一样。
          </div>
        </div>
      </div>

      <div class="chat-input">
        <input type="text" placeholder="输入你的消息...">
        <button>发送</button>
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
  display: flex;
  flex-direction: column;
  gap: 15px;
  margin-bottom: 20px;
  max-height: 400px;
  overflow-y: auto;
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