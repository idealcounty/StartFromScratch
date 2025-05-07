<script setup lang="ts">
import { ref,onMounted } from "vue";
import { useRouter } from 'vue-router';
import { getArchive } from "../../api/archive.ts";

const router = useRouter()
const userId = ref(0)
const archiveVO = ref()
const hasArchive = ref(false)

async function getUserInfo() {
  const { userInfo } = await import('../../api/user.ts')
  const res = await userInfo()
  if (res.data.code === '000') {
    const result = res.data.result
    userId.value = result.userId
  } else if (res.data.code === '400') {
    console.log('未登录')
  }
}

onMounted(async () => {
  await getUserInfo();
  const res = await getArchive(userId.value);
  console.log(res)
  archiveVO.value = res.data.result;
  console.log(archiveVO.value);

  if (archiveVO.value) {
    hasArchive.value = true;
  }
  console.log(hasArchive.value);
});


function handlerestart(){
  router.push({path:'/init'})
}

function handleContinue(){
  router.push({path:'/'})
}

</script>

<template>
  <div class="container">
    <button class="menu-btn restart-btn">
      <span class="btn-text" @click="handlerestart">🔄 重新开始</span>
      <div class="btn-bg"></div>
    </button>

    <button class="menu-btn continue-btn" v-if="hasArchive">
      <span class="btn-text" @click="handleContinue">🎮 继续你的故事</span>
      <div class="btn-bg"></div>
    </button>
  </div>
</template>

<style scoped>
.container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #7e57c2 0%, #ec407a 100%);
  gap: 2rem;
  position: relative;
  overflow: hidden;
}

/* 装饰元素 */
.container::before,
.container::after {
  content: '';
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.4);
  z-index: -1;
}

.container::before {
  width: 300px;
  height: 300px;
  left: 10%;
  top: 20%;
  animation: float-left 8s ease-in-out infinite;
}

.container::after {
  width: 400px;
  height: 400px;
  right: 10%;
  bottom: 10%;
  animation: float-right 10s ease-in-out infinite;
}

.menu-btn {
  position: relative;
  padding: 1.5rem 3rem;
  border: none;
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(8px);
  cursor: pointer;
  transition: transform 0.3s ease;
  border-radius: 15px;
  overflow: hidden;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.1);
}

.btn-bg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(45deg,
  transparent 20%,
  rgba(255,255,255,0.1) 40%,
  transparent 60%
  );
  transition: all 0.5s ease;
  z-index: 1;
  border-radius: 15px;
}

.btn-text {
  position: relative;
  z-index: 2;
  font-family: 'Arial Black', sans-serif;
  font-size: 1.8rem;
  letter-spacing: 2px;
  transition: all 0.3s ease;
}

/* 重新开始按钮样式 */
.restart-btn .btn-text {
  color: #ff6b6b;
  text-shadow: 0 0 10px rgba(255,107,107,0.5);
}

.restart-btn {
  box-shadow: 0 0 20px rgba(255,107,107,0.3);
}

/* 继续游戏按钮样式 */
.continue-btn .btn-text {
  color: #4ecdc4;
  text-shadow: 0 0 10px rgba(78,205,196,0.5);
}

.continue-btn {
  box-shadow: 0 0 20px rgba(78,205,196,0.3);
}

/* 悬停效果 */
.menu-btn:hover {
  transform: translateY(-5px);
}

.menu-btn:hover .btn-bg {
  transform: translateX(100%);
}

.menu-btn:hover .btn-text {
  text-shadow: 0 0 15px currentColor;
}

/* 点击动画 */
.menu-btn:active {
  transform: translateY(0) scale(0.95);
}

/* 流光动画 */
@keyframes flowLight {
  from { transform: translateX(-100%); }
  to { transform: translateX(100%); }
}

.menu-btn:hover .btn-bg {
  animation: flowLight 1.5s infinite;
}

/* 装饰元素动画 */
@keyframes float-left {
  0%, 100% { transform: translate(0, 0); }
  50% { transform: translate(-30px, 30px); }
}

@keyframes float-right {
  0%, 100% { transform: translate(0, 0); }
  50% { transform: translate(30px, -30px); }
}

/* 响应式设计 */
@media (max-width: 480px) {
  .menu-btn {
    padding: 1.2rem 2.5rem;
    font-size: 1.5rem;
  }
}
</style>