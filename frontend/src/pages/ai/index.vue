<template>
  <div class="page-container pb-10">
    <!-- Background Elements -->
    <image class="ink-bg-anim" src="/static/backgorund.png" mode="aspectFill" />
    <div class="bg-overlay"></div>

    <!-- 顶部 -->
    <div class="header-bar pt-12 px-6 flex items-center justify-between z-20 relative">
      <div class="flex items-center text-gray-800">
        <div class="icon-box w-8 h-8 rounded bg-teal-600 text-white flex items-center justify-center mr-3">
          AI
        </div>
        <div>
          <h2 class="font-bold text-lg leading-none">DeepSeek</h2>
          <span class="text-xs text-gray-500">命理模型推理中...</span>
        </div>
      </div>
      <div @click="goBack" class="close-btn w-8 h-8 rounded-full bg-white-40 flex items-center justify-center text-gray-600">
        X
      </div>
    </div>

    <!-- 内容滚动区 -->
    <div class="px-6 mt-6 pb-10" v-if="result && result.analysis">
      <div class="glass-card min-h-70vh p-6 relative">
        <div class="prose text-gray-700 leading-relaxed font-sans">
            <h3 class="font-bold text-gray-900 text-base mb-2">【性格侧写】</h3>
            <p class="mb-4">{{ result.analysis.character }}</p>

            <h3 class="font-bold text-gray-900 text-base mb-2">【五行补救】</h3>
            <p class="mb-4">{{ result.analysis.suggestion }}</p>
        </div>
        
        <!-- 底部引用 -->
        <div class="mt-8 pt-4 border-t border-gray-200 text-center">
            <p class="font-serif text-lg text-teal-800 italic">{{ result.analysis.poetry }}</p>
        </div>
      </div>
    </div>
    <div v-else class="text-center mt-20 text-gray-500">
        数据加载中...
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
</script>

<style scoped>
.page-container {
    min-height: 100vh;
    position: relative;
}
.pb-10 { padding-bottom: 2.5rem; }
.pt-12 { padding-top: 3rem; }
.px-6 { padding-left: 1.5rem; padding-right: 1.5rem; }
.mt-6 { margin-top: 1.5rem; }
.mt-8 { margin-top: 2rem; }
.mt-20 { margin-top: 5rem; }
.mb-2 { margin-bottom: 0.5rem; }
.mb-4 { margin-bottom: 1rem; }
.mr-3 { margin-right: 0.75rem; }
.p-6 { padding: 1.5rem; }
.pt-4 { padding-top: 1rem; }

/* Header */
.header-bar {
    display: flex;
    align-items: center;
    justify-content: space-between;
}
.icon-box {
    width: 2rem;
    height: 2rem;
    border-radius: 0.25rem;
    background-color: #0D9488;
    color: white;
    display: flex;
    align-items: center;
    justify-content: center;
}
.close-btn {
    width: 2rem;
    height: 2rem;
    border-radius: 9999px;
    background-color: rgba(255, 255, 255, 0.4);
    display: flex;
    align-items: center;
    justify-content: center;
    color: #4B5563;
}

/* Text */
.text-gray-500 { color: #6B7280; }
.text-gray-600 { color: #4B5563; }
.text-gray-700 { color: #374151; }
.text-gray-800 { color: #1F2937; }
.text-gray-900 { color: #111827; }
.text-teal-800 { color: #115E59; }
.text-xs { font-size: 0.75rem; }
.text-base { font-size: 1rem; }
.text-lg { font-size: 1.125rem; }
.font-bold { font-weight: 700; }
.leading-none { line-height: 1; }
.leading-relaxed { line-height: 1.625; }
.font-serif { font-family: "Songti SC", "SimSun", "Times New Roman", serif; }
.font-sans { font-family: sans-serif; }

/* Content */
.min-h-70vh { min-height: 70vh; }
.border-t { border-top-width: 1px; }
.border-gray-200 { border-color: #E5E7EB; }
</style>
