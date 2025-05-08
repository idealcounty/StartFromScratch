<script setup lang="ts">
import { ref,computed } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { userLogin } from '../../api/user.ts'  // 引入登录 API

const router = useRouter()

// 表单数据
const name = ref('')
const password = ref('')
const errMsg = ref('')
const loading = ref(false)
// 表单验证逻辑
const isTelEmpty = computed(() => name.value.trim() === '')
const isPasswordEmpty = computed(() => password.value.trim() === '')
const loginDisabled = computed(() => isTelEmpty.value || isPasswordEmpty.value)

// 记住密码和忘记密码状态
const rememberMe = ref(false)

// 登录逻辑
function handleLogin() {
  userLogin(name.value, password.value).then(res => {
    console.log(res.data.code)
    if (res.data.code === '000') {
      const token = res.data.result
      sessionStorage.setItem('token', token)
      router.push({path: "/choose"})
    } else if (res.data.code === '400') {
      errMsg.value = '登陆失败'
      password.value = ''
    }
  })
      .catch(error => {
        console.error("登录请求失败:", error);
        errMsg.value = '用户名或密码错误';
      })
      .finally(() => {
        loading.value = false; // 取消加载状态
      });
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
          <label for="tel">用户名</label>
          <input type="tel" id="tel" placeholder="请输入用户名" v-model.trim="name">
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
  background: linear-gradient(135deg, #5d7a9c, #3d5a7c, #6b8eb3);  /* 修改为蓝灰色系渐变 */
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
  color: #212121;  /* 改为深黑色 */
}

.login-header h1 {
  font-size: 2.2rem;
  font-weight: 600;
  margin-bottom: 10px;
  color: #000000;  /* 添加实色黑色 */
  /* 移除渐变和透明效果，使用实色 */
  background: none;
  -webkit-text-fill-color: initial;
  -webkit-background-clip: initial;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);  /* 调整阴影使文字更清晰 */
}

.login-header p {
  font-size: 1rem;
  opacity: 1;  /* 提高不透明度 */
  color: #212121;  /* 改为深黑色 */
}

/* 输入组样式 */
.input-group {
  margin-bottom: 25px;
  position: relative;
}

.input-group label {
  display: block;
  margin-bottom: 8px;
  color: #212121;  /* 改为深黑色 */
  font-size: 0.9rem;
  font-weight: 500;  /* 加粗使其更清晰 */
}

.input-group input {
  width: 100%;
  padding: 12px 15px;
  background-color: rgba(255, 255, 255, 0.7);  /* 提高背景不透明度 */
  border: 1px solid rgba(0, 0, 0, 0.2);  /* 更改边框颜色 */
  border-radius: 8px;
  color: #212121;  /* 输入文字改为深黑色 */
  font-size: 1rem;
  transition: all 0.3s ease;
  outline: none;
}

.input-group input:focus {
  background-color: rgba(255, 255, 255, 0.3);
  border-color: #5d7a9c;  /* 修改为主色调 */
  box-shadow: 0 0 0 3px rgba(93, 122, 156, 0.2);  /* 修改为主色调 */
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
  accent-color: #5d7a9c;  /* 修改为主色调 */
}

.remember label {
  color: #212121;  /* 改为深黑色 */
  font-size: 0.9rem;
}

.forgot-password {
  color: #212121;  /* 改为深黑色 */
  text-decoration: none;
  font-size: 0.9rem;
  transition: color 0.3s ease;
}

.forgot-password:hover {
  color: #000000;  /* 悬停时更黑 */
  text-decoration: underline;
}

/* 登录按钮 */
.login-button {
  width: 100%;
  padding: 14px;
  background: linear-gradient(90deg, #5d7a9c, #3d5a7c);  /* 保持#5d7a9c系列颜色 */
  border: none;
  border-radius: 8px;
  color: white;  /* 按钮文字保持白色 */
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 5px 15px rgba(93, 122, 156, 0.3);
}

.login-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(93, 122, 156, 0.4);
  background: linear-gradient(90deg, #6b8eb3, #5d7a9c);  /* 悬停时稍微亮一些 */
}

.login-button:active {
  transform: translateY(0);
  background: #3d5a7c;  /* 点击时使用更深的颜色 */
}

.login-button:disabled {
  background: linear-gradient(90deg, #a0a0a0, #808080);  /* 禁用时使用灰色渐变 */
  cursor: not-allowed;
  box-shadow: none;
}

/* 登录页脚 */
.login-footer {
  text-align: center;
  margin-top: 25px;
  color: #212121;  /* 改为深黑色 */
  font-size: 0.9rem;
}

.login-footer a {
  color: #000000;  /* 链接改为黑色 */
  text-decoration: none;
  padding-left: 5px;
  transition: color 0.3s ease;
  font-weight: 500;  /* 加粗使其更醒目 */
}

.login-footer a:hover {
  color: #000000;  /* 悬停时保持黑色 */
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