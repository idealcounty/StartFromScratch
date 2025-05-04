<template>
  <header class="global-header">
    <div class="header-container">
      <!-- 标题区域 -->
      <div class="title-wrap">
        <h1 class="main-title" :class="['slide-in', { animated: showTitle }]">
          从零开始的呢喃生活
        </h1>
        <div class="subtitle" :class="['fade-in', { animated: showSubtitle }]">
          你将怎样选择你的生活
        </div>
      </div>

      <!-- 导航栏（可扩展） -->
      <nav class="header-nav" v-if="showNav">
        <a href="#home" class="nav-item">首页</a>
        <a href="#load" class="nav-item">读档</a>
        <a href="#achievement" class="nav-item">成就</a>
        <a href="#restart" class="nav-item">重新开始</a>
      </nav>
    </div>
  </header>
</template>

<script>
export default {
  data() {
    return {
      showTitle: false,
      showSubtitle: false,
      showNav: false
    };
  },
  mounted() {
    // 延迟加载动画，模拟页面加载效果
    setTimeout(() => this.showTitle = true, 100);
    setTimeout(() => this.showSubtitle = true, 200);
    setTimeout(() => this.showNav = true, 300);
  }
};
</script>

<style scoped lang="scss">
/* 全局样式变量 */
$primary-color: #8A6CD6;       /* 主色调 - 柔和紫 */
$secondary-color: #F5E0FF;     /* 辅助色 - 淡粉紫 */
$text-color: #333;             /* 文本颜色 */
$font-family: 'PingFang SC', 'Noto Sans CJK SC', sans-serif;

.global-header {
  position: relative;
  width: 100%;
  min-height: 120px;
  padding: 20px 40px;
  box-sizing: border-box;
  background: linear-gradient(135deg, $primary-color 25%, $secondary-color 100%);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  transition: height 0.3s ease;

  /* 响应式调整 */
  @media (max-width: 768px) {
    padding: 20px 20px;
    min-height: 80px;
  }
}

.header-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
  max-width: 1200px;
  margin: 0 auto;
}

.title-wrap {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 8px;
}

.main-title {
  margin: 0;
  font-size: 2.2rem;
  font-weight: 500;
  color: white;
  letter-spacing: 1px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.15);
  transform: translateY(20px);
  opacity: 0;
  transition: all 0.5s cubic-bezier(0.25, 0.46, 0.45, 0.94);

  &.animated {
    transform: translateY(0);
    opacity: 1;
  }
}

.subtitle {
  margin: 0;
  font-size: 1.1rem;
  color: rgba(255, 255, 255, 0.9);
  font-weight: 300;
  transform: translateY(20px);
  opacity: 0;
  transition: all 0.5s cubic-bezier(0.25, 0.46, 0.45, 0.94) 0.1s;

  &.animated {
    transform: translateY(0);
    opacity: 1;
  }
}

.header-nav {
  display: flex;
  gap: 24px;

  @media (max-width: 768px) {
    display: none; /* 移动端隐藏导航，可替换为汉堡菜单 */
  }
}

.nav-item {
  position: relative;
  color: white;
  font-size: 1.1rem;
  font-weight: 400;
  text-decoration: none;
  transition: color 0.3s ease;

  &:before {
    content: '';
    position: absolute;
    bottom: -4px;
    left: 0;
    width: 0;
    height: 2px;
    background: white;
    transition: width 0.3s ease;
  }

  &:hover {
    color: $secondary-color;
  }

  &:hover:before {
    width: 100%;
  }
}
</style>