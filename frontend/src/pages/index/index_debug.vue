<template>
  <view class="page-container">
    <!-- Background Elements -->
    <image class="ink-bg-anim" src="/static/backgorund.png" mode="aspectFill" />
    <view class="bg-overlay"></view>

    <!-- Loading Animation - Always visible for testing -->
    <LoadingAnimation 
      :show="true" 
      primaryText="测试Loading显示..."
    />

    <!-- 顶部留白 -->
    <view class="header-spacer flex items-center justify-center relative">
      <view class="text-center mt-10 glass-card px-6 py-2 inline-block title-card">
        <text class="title-text">知 命</text>
        <text class="subtitle-text">DeepDestiny AI</text>
      </view>
    </view>

    <!-- 输入卡片 -->
    <view class="content-wrapper px-6">
      <view class="glass-card p-8">
        <view class="text-center mb-8">
          <text class="quote-text">"万物负阴而抱阳，冲气以为和"</text>
        </view>

        <view class="form-group space-y-8">
          <!-- 日期 -->
          <view class="input-group">
            <label class="input-label">出生日期 (阳历)</label>
            <view class="flex items-center py-3">
              <picker mode="date" :value="birthDate" @change="onDateChange" class="w-full">
                <view class="picker-text">
                  {{ birthDate || '请选择日期' }}
                </view>
              </picker>
            </view>
          </view>

          <!-- 时辰 -->
          <view class="input-group">
            <label class="input-label">出生时辰</label>
            <view class="flex items-center py-3">
              <picker mode="selector" :range="timeOptions" @change="onTimeChange" class="w-full">
                <view class="picker-text">
                  {{ birthTime || '请选择时辰' }}
                </view>
              </picker>
            </view>
          </view>
        </view>

        <view class="mt-10">
          <button @click="toggleLoading" class="btn-primary w-full py-4 rounded-xl font-bold text-lg tracking-widest flex items-center justify-center gap-2">
            <text>切换Loading ({{ isLoading ? '显示中' : '已隐藏' }})</text>
          </button>
          <button @click="startCalculation" class="btn-primary w-full py-4 rounded-xl font-bold text-lg tracking-widest flex items-center justify-center gap-2" style="margin-top: 10px;">
            <text>开始排盘</text>
          </button>
        </view>
      </view>
      
      <view class="footer-text mt-4">
        点击即代表同意《服务协议》 | 结果仅供娱乐
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { calculateBazi } from '@/api/bazi'
import { useBaziStore } from '@/store/bazi'
import LoadingAnimation from '@/components/LoadingAnimation.vue'

const birthDate = ref('')
const birthTime = ref('')
const isLoading = ref(false)
const timeOptions = ['时辰不详', '子时 (23:00-01:00)', '丑时 (01:00-03:00)', '寅时 (03:00-05:00)', '卯时 (05:00-07:00)', '辰时 (07:00-09:00)', '巳时 (09:00-11:00)', '午时 (11:00-13:00)', '未时 (13:00-15:00)', '申时 (15:00-17:00)', '酉时 (17:00-19:00)', '戌时 (19:00-21:00)', '亥时 (21:00-23:00)']

const baziStore = useBaziStore()

const onDateChange = (e) => {
  birthDate.value = e.detail.value
}

const onTimeChange = (e) => {
  birthTime.value = timeOptions[e.detail.value]
}

const toggleLoading = () => {
  isLoading.value = !isLoading.value
  console.log('Loading state:', isLoading.value)
}

const startCalculation = async () => {
  if (!birthDate.value || !birthTime.value) {
    uni.showToast({ title: '请完整填写信息', icon: 'none' })
    return
  }

  isLoading.value = true
  console.log('Starting calculation, isLoading:', isLoading.value)

  try {
    const res = await calculateBazi({
      birthDate: birthDate.value,
      birthTime: birthTime.value
    })
    
    if (res.data.code === 200) {
      baziStore.setResult(res.data.data)
      uni.navigateTo({ url: '/pages/result/index' })
    } else {
      uni.showToast({ title: res.data.message || '请求失败', icon: 'none' })
    }
  } catch (e) {
    uni.showToast({ title: '网络异常', icon: 'none' })
    console.error(e)
  } finally {
    isLoading.value = false
    console.log('Calculation done, isLoading:', isLoading.value)
  }
}
</script>

<style scoped>
/* Same styles as before */
.page-container {
    min-height: 100vh;
    position: relative;
    overflow: hidden;
}
.header-spacer {
    height: 35vh;
    width: 100%;
}
.content-wrapper {
    padding: 0 24px;
}
.mt-10 { margin-top: 2.5rem; }
.mt-4 { margin-top: 1rem; }
.mb-8 { margin-bottom: 2rem; }
.px-6 { padding-left: 1.5rem; padding-right: 1.5rem; }
.py-2 { padding-top: 0.5rem; padding-bottom: 0.5rem; }
.py-3 { padding-top: 0.75rem; padding-bottom: 0.75rem; }
.py-4 { padding-top: 1rem; padding-bottom: 1rem; }
.p-8 { padding: 2rem; }
.space-y-8 > .input-group + .input-group { margin-top: 2rem; }
.inline-block { display: inline-block; }

/* Typography */
.title-text {
    font-size: 1.875rem;
    font-weight: 700;
    letter-spacing: 0.1em;
    color: #1F2937;
}
.subtitle-text {
    font-size: 0.75rem;
    color: #6B7280;
    margin-top: 0.25rem;
    text-transform: uppercase;
    letter-spacing: 0.3em;
}
.quote-text {
    color: #4B5563;
    font-size: 0.875rem;
    font-style: italic;
}
.footer-text {
    text-align: center;
    font-size: 10px;
    color: #9CA3AF;
}

/* Components */
.input-group {
    position: relative;
    border-bottom: 1px solid #9CA3AF;
    transition: border-color 0.3s;
}
.input-label {
    font-size: 0.75rem;
    color: #0F766E; /* teal-700 */
    font-weight: 700;
    position: absolute;
    top: -0.75rem;
    left: 0;
}
.picker-text {
    width: 100%;
    font-size: 1rem;
    color: #1F2937;
}
.btn-primary {
    border-radius: 0.75rem;
    font-size: 1.125rem;
    letter-spacing: 0.1em;
}
</style>
