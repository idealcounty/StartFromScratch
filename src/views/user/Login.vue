<script setup lang="ts">
import { ref,computed } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { userLogin } from '../../api/user.ts'  // 引入登录 API

const router = useRouter()

// 表单数据
const name = ref('')
const password = ref('')

// 表单验证逻辑
const isTelEmpty = computed(() => name.value.trim() === '')
const isPasswordEmpty = computed(() => password.value.trim() === '')
const loginDisabled = computed(() => isTelEmpty.value || isPasswordEmpty.value)

// 记住密码和忘记密码状态
const rememberMe = ref(false)

// 登录逻辑
async function handleLogin() {
  if (loginDisabled.value) {
    ElMessage({
      message: '请输入用户名和密码',
      type: 'error',
      center: true
    })
    return
  }

  try {
    const res = await userLogin(name.value, password.value)
    console.log(res)
    if (res.data.code === '000') {
      ElMessage({
        message: '登录成功！',
        type: 'success',
        center: true
      })
      console.log(1)
      router.push({ path: '/' })  // 跳转到主页或其他页面
    } else {
      ElMessage({
        message: res.data.msg || '登录失败，请检查用户名和密码',
        type: 'error',
        center: true
      })
    }
  } catch (error) {
    ElMessage({
      message: '登录失败，请检查网络或稍后再试',
      type: 'error',
      center: true
    })
    console.error('登录错误:', error)
  }
}

// 跳转到注册页面
function JumpToRegister() {
  router.push({ path: '/register' })
}
</script>

<template>
  <div class="login-container">
    <div class="background-gradient"></div>

    <div class="left-decoration">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
    </div>

    <div class="right-decoration">
      <div class="triangle triangle-1"></div>
      <div class="triangle triangle-2"></div>
    </div>

    <div class="login-form">
      <div class="login-header">
        <h1>Welcome</h1>
        <p>请登录您的账户</p>
      </div>

      <div class="login-body">
        <div class="input-group">
          <label for="tel">电话号码</label>
          <input type="tel" id="tel" placeholder="请输入电话号码" v-model.trim="name">
        </div>

        <div class="input-group">
          <label for="password">密码</label>
          <input type="password" id="password" placeholder="请输入密码" v-model.trim="password">
        </div>

        <div class="remember-forgot">
          <div class="remember">
            <input type="checkbox" id="remember-me" v-model="rememberMe">
            <label for="remember-me">记住我</label>
          </div>
          <a href="#" class="forgot-password">忘记密码？</a>
        </div>

        <button
            class="login-button"
            :disabled="loginDisabled"
            @click="handleLogin"
        >
          登录
        </button>
      </div>

      <div class="login-footer">
        <p>没有账户? <a @click="JumpToRegister">立即注册</a></p>
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
.login-container {
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
.login-container::before {
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

/* 登录表单样式 */
.login-form {
  width: 420px;
  background-color: rgba(255, 255, 255, 0.15);
  border-radius: 15px;
  padding: 30px;
  box-shadow: 0 15px 30px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  transition: all 0.3s ease;
}

.login-form:hover {
  transform: translateY(-5px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.2);
}

/* 登录表头 */
.login-header {
  text-align: center;
  margin-bottom: 30px;
  color: white;
}

.login-header h1 {
  font-size: 2.2rem;
  font-weight: 600;
  margin-bottom: 10px;
  background: linear-gradient(to right, #ffffff, #e0e0e0);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  text-shadow: 0 0 10px rgba(255, 255, 255, 0.3);
}

.login-header p {
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

/* 记住我和忘记密码 */
.remember-forgot {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
}

.remember {
  display: flex;
  align-items: center;
}

.remember input {
  margin-right: 8px;
  width: 16px;
  height: 16px;
  accent-color: #a777e3;
}

.remember label {
  color: #e0e0e0;
  font-size: 0.9rem;
}

.forgot-password {
  color: #e0e0e0;
  text-decoration: none;
  font-size: 0.9rem;
  transition: color 0.3s ease;
}

.forgot-password:hover {
  color: #ffffff;
  text-decoration: underline;
}

/* 登录按钮 */
.login-button {
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

.login-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(102, 142, 251, 0.4);
}

.login-button:active {
  transform: translateY(0);
}

.login-button:disabled {
  background: linear-gradient(90deg, #b0b0b0, #a0a0a0);
  cursor: not-allowed;
  box-shadow: none;
}

/* 登录页脚 */
.login-footer {
  text-align: center;
  margin-top: 25px;
  color: #e0e0e0;
  font-size: 0.9rem;
}

.login-footer a {
  color: #e0e0e0;
  text-decoration: none;
  padding-left: 5px;
  transition: color 0.3s ease;
}

.login-footer a:hover {
  color: #ffffff;
  text-decoration: underline;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .login-form {
    width: 90%;
    padding: 20px;
  }

  .login-header h1 {
    font-size: 1.8rem;
  }

  .left-decoration, .right-decoration {
    display: none;
  }
}
</style>