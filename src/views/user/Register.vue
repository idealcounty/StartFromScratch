<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()

function JumpToLogin() {
  router.push({ path: '/login' })
}
</script>


<template>
  <div class="register-container">
    <!-- 背景渐变和装饰 -->
    <div class="background-gradient"></div>

    <!-- 左侧装饰 -->
    <div class="left-decoration">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
    </div>

    <!-- 右侧装饰 -->
    <div class="right-decoration">
      <div class="triangle triangle-1"></div>
      <div class="triangle triangle-2"></div>
    </div>

    <!-- 注册表单 -->
    <div class="register-form">
      <div class="register-header">
        <h1>创建账户</h1>
        <p>填写以下信息创建您的账户</p>
      </div>

      <div class="register-body">
        <div class="input-group">
          <label for="username">用户名</label>
          <input type="text" id="username" placeholder="请输入用户名">
        </div>

        <div class="input-group">
          <label for="phone">手机号</label>
          <input type="tel" id="phone" placeholder="请输入手机号">
        </div>

        <div class="input-group">
          <label for="password">密码</label>
          <input type="password" id="password" placeholder="请输入密码">
        </div>

        <div class="input-group">
          <label for="confirm-password">确认密码</label>
          <input type="password" id="confirm-password" placeholder="请再次输入密码">
        </div>

        <button class="register-button">注册</button>
      </div>

      <div class="register-footer">
        <p>已有账户? <a href="#" @click="JumpToLogin">立即登录</a></p>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* 基础样式重置 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

/* 容器样式 */
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  position: relative;
  overflow: hidden;
}

/* 背景渐变 */
.background-gradient {
  position: absolute;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #6e8efb, #a777e3, #ff00cc);
  z-index: -2;
}

/* 玻璃质感背景 */
.register-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  z-index: -1;
}

/* 左侧装饰（圆形） */
.left-decoration {
  position: absolute;
  left: 10%;
  top: 20%;
}

.circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  box-shadow: 0 0 20px rgba(255, 255, 255, 0.3);
}

.circle-1 {
  width: 200px;
  height: 200px;
  background: rgba(255, 255, 255, 0.15);
  box-shadow: 0 0 50px rgba(255, 255, 255, 0.2);
}

.circle-2 {
  width: 120px;
  height: 120px;
  background: rgba(255, 255, 255, 0.1);
  box-shadow: 0 0 30px rgba(255, 255, 255, 0.15);
  left: 100px;
  top: 100px;
}

/* 右侧装饰（三角形） */
.right-decoration {
  position: absolute;
  right: 10%;
  bottom: 20%;
}

.triangle {
  position: absolute;
  width: 0;
  height: 0;
  border-left: 80px solid transparent;
  border-right: 80px solid transparent;
  border-bottom: 140px solid rgba(255, 255, 255, 0.15);
}

.triangle-1 {
  transform: rotate(45deg);
  border-bottom-color: rgba(255, 255, 255, 0.2);
}

.triangle-2 {
  border-bottom-color: rgba(255, 255, 255, 0.1);
  right: 50px;
  bottom: 50px;
  transform: rotate(-30deg);
  border-left: 60px solid transparent;
  border-right: 60px solid transparent;
  border-bottom: 100px solid rgba(255, 255, 255, 0.1);
}

/* 注册表单样式 */
.register-form {
  width: 420px;
  background-color: rgba(255, 255, 255, 0.15);
  border-radius: 15px;
  padding: 30px;
  box-shadow: 0 15px 30px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  transition: all 0.3s ease;
}

.register-form:hover {
  transform: translateY(-5px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.2);
}

/* 注册表头 */
.register-header {
  text-align: center;
  margin-bottom: 30px;
  color: white;
}

.register-header h1 {
  font-size: 2.2rem;
  font-weight: 600;
  margin-bottom: 10px;
  background: linear-gradient(to right, #ffffff, #e0e0e0);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  text-shadow: 0 0 10px rgba(255, 255, 255, 0.3);
}

.register-header p {
  font-size: 1rem;
  opacity: 0.8;
}

/* 输入组样式 */
.input-group {
  margin-bottom: 25px;
  position: relative;
}

.input-group label {
  display: block;
  margin-bottom: 8px;
  color: #e0e0e0;
  font-size: 0.9rem;
}

.input-group input {
  width: 100%;
  padding: 12px 15px;
  background-color: rgba(255, 255, 255, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 8px;
  color: white;
  font-size: 1rem;
  transition: all 0.3s ease;
  outline: none;
}

.input-group input:focus {
  background-color: rgba(255, 255, 255, 0.3);
  border-color: #a777e3;
  box-shadow: 0 0 0 3px rgba(167, 119, 227, 0.2);
}

/* 注册按钮 */
.register-button {
  width: 100%;
  padding: 14px;
  background: linear-gradient(90deg, #6e8efb, #a777e3);
  border: none;
  border-radius: 8px;
  color: white;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 5px 15px rgba(102, 142, 251, 0.3);
}

.register-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(102, 142, 251, 0.4);
}

.register-button:active {
  transform: translateY(0);
}

/* 注册页脚 */
.register-footer {
  text-align: center;
  margin-top: 25px;
  color: #e0e0e0;
  font-size: 0.9rem;
}

.register-footer a {
  color: #e0e0e0;
  text-decoration: none;
  padding-left: 5px;
  transition: color 0.3s ease;
}

.register-footer a:hover {
  color: #ffffff;
  text-decoration: underline;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .register-form {
    width: 90%;
    padding: 20px;
  }

  .register-header h1 {
    font-size: 1.8rem;
  }

  .left-decoration, .right-decoration {
    display: none;
  }
}

</style>