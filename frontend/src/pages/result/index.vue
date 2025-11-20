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
        <div class="flex justify-between items-center mb-6 border-b border-gray-300-30 pb-3">
          <div class="text-left">
            <div class="text-xs text-gray-500">阳历</div>
            <div class="font-sans font-bold text-gray-800">{{ result.solarDate }}</div>
          </div>
          <div class="text-right">
            <div class="text-xs text-gray-500">农历</div>
            <div class="font-bold text-teal-800">{{ result.lunarDate }}</div>
          </div>
        </div>

        <!-- 四柱排盘 -->
        <div class="grid grid-cols-4 gap-2 mb-6">
          <div class="bazi-grid-item">
            <span class="text-xs text-gray-400 mb-1">年柱</span>
            <span class="text-xl font-bold text-gray-800 block">{{ result.bazi.year.charAt(0) }}</span>
            <span class="text-xl font-bold text-gray-800 block">{{ result.bazi.year.charAt(1) }}</span>
          </div>
          <div class="bazi-grid-item">
            <span class="text-xs text-gray-400 mb-1">月柱</span>
            <span class="text-xl font-bold text-gray-800 block">{{ result.bazi.month.charAt(0) }}</span>
            <span class="text-xl font-bold text-gray-800 block">{{ result.bazi.month.charAt(1) }}</span>
          </div>
          <div class="bazi-grid-item border border-teal-400 bg-teal-50-50">
            <span class="text-xs text-teal-600 mb-1">日主</span>
            <span class="text-xl font-bold text-teal-900 block">{{ result.bazi.day.charAt(0) }}</span>
            <span class="text-xl font-bold text-teal-900 block">{{ result.bazi.day.charAt(1) }}</span>
          </div>
          <div class="bazi-grid-item">
            <span class="text-xs text-gray-400 mb-1">时柱</span>
            <span class="text-xl font-bold text-gray-800 block">{{ result.bazi.time.charAt(0) }}</span>
            <span class="text-xl font-bold text-gray-800 block">{{ result.bazi.time.charAt(1) }}</span>
          </div>
        </div>

        <!-- 五行量化 -->
        <div>
          <div class="flex justify-between text-xs text-gray-500 mb-2">
            <span>五行分布</span>
            <span>生肖：<span class="text-gray-800 font-bold">{{ result.zodiac }}</span></span>
          </div>
          <!-- 简易五行条 -->
           <div class="flex h-3 rounded-full overflow-hidden shadow-inner w-full">
              <div class="bg-green-600 h-full" :style="{width: (result.wuxing.wood * 10) + '%'}" title="木"></div>
              <div class="bg-red-500 h-full" :style="{width: (result.wuxing.fire * 10) + '%'}" title="火"></div>
              <div class="bg-yellow-500 h-full" :style="{width: (result.wuxing.earth * 10) + '%'}" title="土"></div>
              <div class="bg-gray-300 h-full" :style="{width: (result.wuxing.gold * 10) + '%'}" title="金"></div>
              <div class="bg-blue-500 h-full" :style="{width: (result.wuxing.water * 10) + '%'}" title="水"></div>
          </div>
          <div class="flex justify-between text-xs text-gray-400 mt-1 font-sans">
            <span>木:{{result.wuxing.wood}}</span>
            <span>火:{{result.wuxing.fire}}</span>
            <span>土:{{result.wuxing.earth}}</span>
            <span>金:{{result.wuxing.gold}}</span>
            <span>水:{{result.wuxing.water}}</span>
          </div>
        </div>
      </div>

      <!-- 底部悬浮按钮 -->
      <div class="fixed bottom-0 left-0 w-full p-4 pb-8 bg-gradient-mask z-30">
        <button @click="goToAI" class="btn-primary w-full py-3 rounded-full text-lg font-bold flex items-center justify-center shadow-xl animate-pulse text-white">
          AI 深度推理解读
        </button>
      </div>
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
.mb-4 { margin-bottom: 1rem; }
.mb-6 { margin-bottom: 1.5rem; }
.mt-1 { margin-top: 0.25rem; }
.mt-20 { margin-top: 5rem; }
.p-4 { padding: 1rem; }
.p-5 { padding: 1.25rem; }
.pb-3 { padding-bottom: 0.75rem; }
.pb-8 { padding-bottom: 2rem; }

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

/* Text Colors */
.text-gray-400 { color: #9CA3AF; }
.text-gray-500 { color: #6B7280; }
.text-gray-600 { color: #4B5563; }
.text-gray-800 { color: #1F2937; }
.text-teal-800 { color: #115E59; }
.text-teal-600 { color: #0D9488; }
.text-teal-900 { color: #134E4A; }
.text-xs { font-size: 0.75rem; }
.text-xl { font-size: 1.25rem; }
.font-bold { font-weight: 700; }
.font-sans { font-family: sans-serif; }

/* Grid */
.grid { display: grid; }
.grid-cols-4 { grid-template-columns: repeat(4, minmax(0, 1fr)); }
.gap-2 { gap: 0.5rem; }

/* Bazi Items */
.bazi-grid-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 10px;
  background: rgba(255,255,255,0.5);
  border-radius: 8px;
}
.bg-teal-50-50 { background-color: rgba(240, 253, 250, 0.5); }
.border-teal-400 { border-color: #2DD4BF; }
.border-gray-300-30 { border-color: rgba(209, 213, 219, 0.3); }

/* Wuxing Bar */
.h-3 { height: 0.75rem; }
.rounded-full { border-radius: 9999px; }
.shadow-inner { box-shadow: inset 0 2px 4px 0 rgba(0, 0, 0, 0.06); }
.bg-green-600 { background-color: #16A34A; }
.bg-red-500 { background-color: #EF4444; }
.bg-yellow-500 { background-color: #EAB308; }
.bg-gray-300 { background-color: #D1D5DB; }
.bg-blue-500 { background-color: #3B82F6; }

/* Footer */
.fixed { position: fixed; }
.bottom-0 { bottom: 0; }
.left-0 { left: 0; }
.z-30 { z-index: 30; }
.bg-gradient-to-t { background: linear-gradient(to top, #ffffff, rgba(255,255,255,0.9), transparent); }
.shadow-xl { box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04); }
.animate-pulse { animation: pulse 2s cubic-bezier(0.4, 0, 0.6, 1) infinite; }

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: .5; }
}
</style>
