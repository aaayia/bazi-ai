<template>
  <div class="page-container pb-24">
    <!-- Background Elements -->
    <image class="ink-bg-anim" src="/static/backgorund.png" mode="aspectFill" />
    <div class="bg-overlay"></div>

    <!-- 导航栏 -->
    <div class="nav-bar flex items-center justify-between sticky top-0 z-20 glass-card rounded-none border-x-0 border-t-0 border-b border-white-40 mb-4">
      <div @click="goBack" class="nav-btn flex items-center justify-center rounded-full bg-white-50 text-gray-600">
        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <polyline points="15 18 9 12 15 6"></polyline>
        </svg>
      </div>
      <span class="font-bold text-gray-800">命盘详情</span>
      <div class="w-8"></div>
    </div>

    <div class="px-4" v-if="result">
      <!-- 核心八字卡片 -->
      <div class="glass-card p-5 mb-4 relative overflow-hidden">
        <!-- 日期对照 -->
        <div class="flex justify-between items-end mb-8 border-b border-gray-100 pb-4">
          <div class="text-left">
            <div class="text-xs text-gray-400 mb-1">阳历</div>
            <div class="font-sans font-bold text-gray-900 text-3xl tracking-tight">{{ result.solarDate }}</div>
          </div>
          <div class="text-right">
            <div class="text-xs text-gray-400 mb-1">农历</div>
            <div class="font-bold text-teal-700 text-lg">{{ result.lunarDate }}</div>
          </div>
        </div>

        <!-- 四柱排盘 -->
        <div class="grid grid-cols-4 gap-3 mb-8">
          <div class="bazi-grid-item">
            <span class="text-xs text-gray-400 mb-2">年柱</span>
            <span class="text-2xl font-bold text-gray-800 block mb-1">{{ result.bazi.year.charAt(0) }}</span>
            <span class="text-2xl font-bold text-gray-800 block">{{ result.bazi.year.charAt(1) }}</span>
          </div>
          <div class="bazi-grid-item">
            <span class="text-xs text-gray-400 mb-2">月柱</span>
            <span class="text-2xl font-bold text-gray-800 block mb-1">{{ result.bazi.month.charAt(0) }}</span>
            <span class="text-2xl font-bold text-gray-800 block">{{ result.bazi.month.charAt(1) }}</span>
          </div>
          <div class="bazi-grid-item day-pillar">
            <span class="text-xs text-teal-600 mb-2">日主</span>
            <span class="text-3xl font-bold text-teal-900 block mb-1">{{ result.bazi.day.charAt(0) }}</span>
            <span class="text-3xl font-bold text-teal-900 block">{{ result.bazi.day.charAt(1) }}</span>
          </div>
          <div class="bazi-grid-item">
            <span class="text-xs text-gray-400 mb-2">时柱</span>
            <span class="text-2xl font-bold text-gray-800 block mb-1">{{ result.bazi.time.charAt(0) }}</span>
            <span class="text-2xl font-bold text-gray-800 block">{{ result.bazi.time.charAt(1) }}</span>
          </div>
        </div>

        <!-- 五行量化 -->
        <div>
          <div class="flex justify-between text-xs text-gray-500 mb-3">
            <span>五行分布</span>
            <span>生肖：<span class="text-gray-800 font-bold">{{ result.zodiac }}</span></span>
          </div>
          <!-- 简易五行条 -->
           <div class="flex h-3 rounded-full overflow-hidden w-full mb-2">
              <div class="bg-green-500 h-full" :style="{width: (result.wuxing.wood * 10) + '%'}" title="木"></div>
              <div class="bg-red-500 h-full" :style="{width: (result.wuxing.fire * 10) + '%'}" title="火"></div>
              <div class="bg-yellow-500 h-full" :style="{width: (result.wuxing.earth * 10) + '%'}" title="土"></div>
              <div class="bg-gray-400 h-full" :style="{width: (result.wuxing.gold * 10) + '%'}" title="金"></div>
              <div class="bg-blue-500 h-full" :style="{width: (result.wuxing.water * 10) + '%'}" title="水"></div>
          </div>
          <div class="flex justify-between text-xs text-gray-400 font-sans px-1">
            <span>木</span>
            <span>火</span>
            <span>土</span>
            <span>金</span>
            <span>水</span>
          </div>
        </div>
      </div>

      <!-- 五行缺失检测卡片 -->
      <div class="glass-card p-5 mb-6 flex items-start">
        <div class="mr-4 mt-1">
          <div class="w-8 h-8 rounded-full bg-red-100 flex items-center justify-center">
             <text class="text-red-500 text-lg">🔔</text>
          </div>
        </div>
        <div>
          <div class="font-bold text-gray-900 text-base mb-1">五行缺失检测</div>
          <div class="text-sm text-gray-600 mb-1">
            您的命盘中五行缺 <text class="text-red-500 font-bold">{{ missingElementsText }}</text>。
          </div>
          <div class="text-xs text-gray-400">
            五行流转不息，缺失可能影响运势平衡。
          </div>
        </div>
      </div>

      <!-- 广告位 (文字形式) -->
      <view class="ad-container text-center mb-6">
        <text class="text-xs text-gray-300">广告：招财开运金饰推荐</text>
      </view>

      <!-- AI深度解读按钮 -->
      <view class="button-container">
        <button @click="goToAI" class="btn-ai w-full py-4 rounded-full text-lg font-bold flex items-center justify-center shadow-lg text-white">
          AI 深度推理解读
        </button>
      </view>
    </div>
    <div v-else class="text-center mt-20 text-gray-500">
        暂无数据，请重新测算
    </div>
  </div>
</template>

<script setup>
import { useBaziStore } from '@/store/bazi'
import { computed } from 'vue'

const store = useBaziStore()
const result = computed(() => store.result)

const missingElementsText = computed(() => {
  if (!result.value || !result.value.wuxing) return ''
  
  // Use backend provided missing list if available, otherwise calculate
  if (result.value.wuxing.missing && result.value.wuxing.missing.length > 0) {
    return result.value.wuxing.missing.join('、')
  }
  
  // Fallback calculation
  const wuxing = result.value.wuxing
  const map = { wood: '木', fire: '火', earth: '土', gold: '金', water: '水' }
  const missing = []
  for (const key in map) {
    if (wuxing[key] === 0) {
      missing.push(map[key])
    }
  }
  return missing.length > 0 ? missing.join('、') : '无'
})

const goBack = () => {
  uni.navigateBack()
}

const goToAI = () => {
  uni.navigateTo({ url: '/pages/ai/index' })
}
</script>

<style scoped>
.page-container {
    min-height: 100vh;
    position: relative;
}
.pb-24 { padding-bottom: 6rem; }
.px-4 { padding-left: 1rem; padding-right: 1rem; }
.mb-1 { margin-bottom: 0.25rem; }
.mb-2 { margin-bottom: 0.5rem; }
.mb-3 { margin-bottom: 0.75rem; }
.mb-4 { margin-bottom: 1rem; }
.mb-6 { margin-bottom: 1.5rem; }
.mb-8 { margin-bottom: 2rem; }
.mt-1 { margin-top: 0.25rem; }
.mr-4 { margin-right: 1rem; }
.mt-20 { margin-top: 5rem; }
.p-5 { padding: 1.25rem; }
.pb-4 { padding-bottom: 1rem; }
.px-1 { padding-left: 0.25rem; padding-right: 0.25rem; }

/* Nav Bar */
.nav-bar {
    padding-top: 2.5rem;
    padding-bottom: 1rem;
    padding-left: 1rem;
    padding-right: 1rem;
}
.nav-btn {
    width: 2rem;
    height: 2rem;
}
.rounded-none { border-radius: 0; }
.border-x-0 { border-left-width: 0; border-right-width: 0; }
.border-t-0 { border-top-width: 0; }
.border-b { border-bottom-width: 1px; }
.border-white-40 { border-color: rgba(255, 255, 255, 0.4); }
.border-gray-100 { border-color: #F3F4F6; }

/* Text Colors */
.text-gray-300 { color: #D1D5DB; }
.text-gray-400 { color: #9CA3AF; }
.text-gray-500 { color: #6B7280; }
.text-gray-600 { color: #4B5563; }
.text-gray-800 { color: #1F2937; }
.text-gray-900 { color: #111827; }
.text-teal-600 { color: #0D9488; }
.text-teal-700 { color: #0F766E; }
.text-teal-900 { color: #134E4A; }
.text-red-500 { color: #EF4444; }
.text-xs { font-size: 0.75rem; }
.text-sm { font-size: 0.875rem; }
.text-base { font-size: 1rem; }
.text-lg { font-size: 1.125rem; }
.text-xl { font-size: 1.25rem; }
.text-2xl { font-size: 1.5rem; }
.text-3xl { font-size: 1.875rem; }
.font-bold { font-weight: 700; }
.font-sans { font-family: sans-serif; }
.tracking-tight { letter-spacing: -0.025em; }

/* Grid */
.grid { display: grid; }
.grid-cols-4 { grid-template-columns: repeat(4, minmax(0, 1fr)); }
.gap-3 { gap: 0.75rem; }

/* Bazi Items */
.bazi-grid-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 12px 4px;
  border-radius: 8px;
}
.day-pillar {
  background-color: #FFFFFF;
  border: 1px solid #2DD4BF;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
  transform: scale(1.05);
  z-index: 10;
}

/* Wuxing Bar */
.h-3 { height: 0.75rem; }
.rounded-full { border-radius: 9999px; }
.bg-green-500 { background-color: #22C55E; }
.bg-red-500 { background-color: #EF4444; }
.bg-yellow-500 { background-color: #EAB308; }
.bg-gray-400 { background-color: #9CA3AF; }
.bg-blue-500 { background-color: #3B82F6; }
.bg-red-100 { background-color: #FEE2E2; }

/* Footer */
.ad-container {
  padding: 0.5rem 0;
}

.button-container {
  padding: 0.5rem 0 2rem;
}

.btn-ai {
  background: linear-gradient(to right, #2DD4BF, #0D9488);
}

.shadow-lg { 
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05); 
  color: #FFFFFF;
}
</style>
