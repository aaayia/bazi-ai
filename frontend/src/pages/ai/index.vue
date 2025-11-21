<template>
  <div class="page-container pb-10">
    <!-- Background Elements -->
    <image class="ink-bg-anim" src="/static/backgorund.png" mode="aspectFill" />
    <div class="bg-overlay"></div>

    <!-- Loading Animation -->
    <LoadingAnimation 
      :show="isLoading" 
      primaryText="命理模型推理中..."
      secondaryText="正在解析天干地支..."
      tertiaryText="Connecting to DeepSeek API"
    />

    <!-- 顶部 -->
    <div class="header-bar pt-12 px-6 flex items-center justify-between z-20 relative">
      <div class="flex items-center text-gray-800">
        <div class="icon-box w-8 h-8 rounded bg-teal-600 text-white flex items-center justify-center mr-3">
          AI
        </div>
        <div>
          <h2 class="font-bold text-lg leading-none">DeepSeek</h2>
          <span class="text-xs text-gray-500" v-if="!isLoading && !typingComplete">命理模型推理中...</span>
          <span class="text-xs text-teal-600" v-else-if="typingComplete">分析完成</span>
        </div>
      </div>
      <div @click="goBack" class="close-btn w-8 h-8 rounded-full bg-white-40 flex items-center justify-center text-gray-600">
        X
      </div>
    </div>

    <!-- 内容滚动区 -->
    <div class="px-6 mt-6 pb-10" v-if="!isLoading && analysis">
      <div class="glass-card min-h-70vh p-6 relative">
        <div class="prose text-gray-700 leading-relaxed font-sans">
            <h3 class="font-bold text-gray-900 text-base mb-2">【性格侧写】</h3>
            <p class="mb-4">{{ displayedCharacter }}</p>

            <h3 class="font-bold text-gray-900 text-base mb-2">【五行补救】</h3>
            <p class="mb-4">{{ displayedSuggestion }}</p>
        </div>
        
        <!-- 底部引用 -->
        <div class="mt-8 pt-4 border-t border-gray-200 text-center">
            <p class="font-serif text-lg text-teal-800 italic">{{ displayedPoetry }}</p>
        </div>
      </div>

      <!-- 底部操作按钮 -->
      <view class="bottom-actions" v-if="typingComplete">
        <button class="share-btn" open-type="share">
          <text>分享报告</text>
        </button>
        <button class="unlock-btn" @click="handleRecalculate">
          <text>{{ isFromShare ? '我也试试' : '重新推理' }}</text>
        </button>
      </view>
    </div>
    <div v-else-if="!isLoading" class="text-center mt-20 text-gray-500">
        数据加载中...
    </div>
  </div>
</template>

<script setup>
import { useBaziStore } from '@/store/bazi'
import { computed, ref, onMounted, onUnmounted } from 'vue'
import { getAnalysis } from '@/api/bazi'
import LoadingAnimation from '@/components/LoadingAnimation.vue'
import { onLoad, onShareAppMessage } from '@dcloudio/uni-app'

const store = useBaziStore()
const result = computed(() => store.result)

const isLoading = ref(true)
const typingComplete = ref(false)
const displayedCharacter = ref('')
const displayedSuggestion = ref('')
const displayedPoetry = ref('')
const analysis = ref(null)
const isFromShare = ref(false)
const shareData = ref(null)

let typingInterval = null

const typewriterEffect = (text, targetRef, callback) => {
  let index = 0
  const speed = 50 // 50ms per character
  
  return setInterval(() => {
    if (index < text.length) {
      targetRef.value += text.charAt(index)
      index++
    } else {
      clearInterval(typingInterval)
      if (callback) callback()
    }
  }, speed)
}

const startTyping = () => {
  if (!analysis.value) return
  
  const { character, suggestion, poetry } = analysis.value
  
  // Type character first
  typingInterval = typewriterEffect(character, displayedCharacter, () => {
    // Then type suggestion
    typingInterval = typewriterEffect(suggestion, displayedSuggestion, () => {
      // Finally type poetry
      typingInterval = typewriterEffect(poetry, displayedPoetry, () => {
        typingComplete.value = true
      })
    })
  })
}

const fetchAnalysis = async () => {
  if (!result.value) {
    uni.showToast({ title: '请先进行八字排盘', icon: 'none' })
    setTimeout(() => {
      uni.reLaunch({ url: '/pages/index/index' })
    }, 1500)
    return
  }

  try {
    // Send simplified DTO format
    const analysisRequest = {
      solarDate: result.value.solarDate,
      year: result.value.bazi.year,
      month: result.value.bazi.month,
      day: result.value.bazi.day,
      time: result.value.bazi.time
    }
    
    const res = await getAnalysis(analysisRequest)
    if (res.data.code === 200) {
      analysis.value = res.data.data
      isLoading.value = false
      startTyping()
    } else {
      uni.showToast({ title: res.data.message || '获取分析失败', icon: 'none' })
      setTimeout(() => {
        uni.reLaunch({ url: '/pages/index/index' })
      }, 1500)
    }
  } catch (e) {
    console.error('Failed to fetch analysis:', e)
    uni.showToast({ title: '网络异常', icon: 'none' })
    setTimeout(() => {
      uni.reLaunch({ url: '/pages/index/index' })
    }, 1500)
  }
}

onLoad((options) => {
  console.log('AI page onLoad options:', options)
  
  // Check if page is from share link
  if (options.share && options.data) {
    isFromShare.value = true
    try {
      // Decode shared data
      const decodedData = decodeURIComponent(options.data)
      shareData.value = JSON.parse(decodedData)
      
      console.log('Parsed share data:', shareData.value)
      
      // Restore analysis data from share
      analysis.value = shareData.value.analysis
      
      // Also restore result data to store for button functionality
      store.setResult({
        bazi: shareData.value.bazi,
        solarDate: shareData.value.solarDate,
        wuxing: shareData.value.wuxing || {},
        zodiac: shareData.value.zodiac || ''
      })
      
      // Clear loading and start typing
      isLoading.value = false
      startTyping()
    } catch (e) {
      console.error('Failed to parse share data:', e)
      uni.showToast({ title: '分享数据解析失败', icon: 'none' })
      setTimeout(() => {
        uni.reLaunch({ url: '/pages/index/index' })
      }, 1500)
    }
  }
})

onMounted(() => {
  // Only fetch if not from share
  if (!isFromShare.value) {
    fetchAnalysis()
  }
})

onUnmounted(() => {
  if (typingInterval) {
    clearInterval(typingInterval)
  }
})

const goBack = () => {
  // Check if there's navigation history
  const pages = getCurrentPages()
  if (pages.length > 1) {
    uni.navigateBack()
  } else {
    // No history, go to home
    uni.reLaunch({ url: '/pages/index/index' })
  }
}

// WeChat share handler - use lifecycle hook
onShareAppMessage(() => {
  console.log('onShareAppMessage triggered')
  
  if (!analysis.value || !result.value) {
    return {
      title: '八字命理分析',
      path: '/pages/index/index'
    }
  }
  
  const sharePayload = {
    analysis: analysis.value,
    bazi: result.value.bazi,
    solarDate: result.value.solarDate,
    wuxing: result.value.wuxing,
    zodiac: result.value.zodiac
  }
  
  const encodedData = encodeURIComponent(JSON.stringify(sharePayload))
  
  console.log('Share payload prepared, encoded length:', encodedData.length)
  
  return {
    title: `我的八字分析结果 - ${result.value.solarDate}`,
    path: `/pages/ai/index?share=1&data=${encodedData}`,
    imageUrl: '/static/xiantianbagua.svg'
  }
})

const handleRecalculate = () => {
  if (isFromShare.value) {
    // Navigate to home for shared pages
    uni.reLaunch({ url: '/pages/index/index' })
  } else {
    // Check if there's navigation history
    const pages = getCurrentPages()
    if (pages.length > 1) {
      uni.navigateBack()
    } else {
      // No history, go to home
      uni.reLaunch({ url: '/pages/index/index' })
    }
  }
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
.text-teal-600 { color: #0D9488; }
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

/* Bottom Actions */
.bottom-actions {
  display: flex;
  gap: 12rpx;
  margin-top: 32rpx;
  padding: 0 8rpx;
}

.share-btn, .unlock-btn {
  flex: 1;
  padding: 24rpx 32rpx;
  border-radius: 48rpx;
  font-size: 28rpx;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8rpx;
  border: none;
  transition: all 0.3s;
}

.share-btn {
  background: rgba(255, 255, 255, 0.8);
  color: #1F2937;
  border: 2rpx solid rgba(15, 118, 110, 0.2);
}

.share-icon {
  font-size: 32rpx;
}

.unlock-btn {
  background: linear-gradient(135deg, #14B8A6, #0D9488);
  color: white;
  box-shadow: 0 8rpx 24rpx rgba(13, 148, 136, 0.3);
}

.share-btn:active {
  transform: scale(0.98);
  background: rgba(255, 255, 255, 0.6);
}

.unlock-btn:active {
  transform: scale(0.98);
  box-shadow: 0 4rpx 12rpx rgba(13, 148, 136, 0.3);
}
</style>
