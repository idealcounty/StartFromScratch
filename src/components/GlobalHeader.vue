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
        <a href="#init" class="nav-item">重新开始</a>
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
$text-color: #333;             /* 文本颜色 */
$font-family: 'PingFang SC', 'Noto Sans CJK SC', sans-serif;

.global-header {
  position: relative;
  width: 100%;
  min-height: 120px;
  padding: 20px 40px;
  box-sizing: border-box;
  background: linear-gradient(to bottom, #708090 25%, #DFF1F4 100%);
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
  margin-left: -50px;  /* 添加左边距，使标题左移 */
  padding-left: 10px;  /* 添加内边距 */
}

.main-title {
  margin: 0;
  font-size: 2.4rem;  /* 增大字体 */
  font-weight: 600;  /* 加粗字体 */
  color: white;
  letter-spacing: 1.5px;  /* 增加字间距 */
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);  /* 增强文字阴影 */
  font-family: 'SimHei', 'Microsoft YaHei', $font-family;  /* 修改字体 */
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
  font-size: 1.2rem;  /* 略微增大字体 */
  color: rgba(255, 255, 255, 0.95);  /* 增加不透明度 */
  font-weight: 400;  /* 增加字重 */
  font-family: 'KaiTi', 'STKaiti', $font-family;  /* 修改为楷体风格 */
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
  font-size: 24px;
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
    color: #94A9C1;
  }

  &:hover:before {
    width: 100%;
  }
}
</style>