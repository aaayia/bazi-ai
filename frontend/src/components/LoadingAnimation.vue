<template>
  <view class="loading-overlay" v-if="show">
    <view class="loading-container">
      <!-- Bagua Symbol -->
      <view class="bagua-wrapper">
        <image class="bagua-image" src="/static/xiantianbagua.svg" mode="aspectFit" />
      </view>

      <!-- Loading Text -->
      <view class="loading-text-container">
        <text class="loading-text-primary">{{ primaryText }}</text>
        <text class="loading-text-secondary" v-if="secondaryText">{{ secondaryText }}</text>
        <text class="loading-text-tertiary" v-if="tertiaryText">{{ tertiaryText }}</text>
      </view>
    </view>
  </view>
</template>

<script setup>
const props = defineProps({
  show: {
    type: Boolean,
    default: false
  },
  primaryText: {
    type: String,
    default: '乾坤排盘中...'
  },
  secondaryText: {
    type: String,
    default: ''
  },
  tertiaryText: {
    type: String,
    default: ''
  },
  mode: {
    type: String,
    default: 'calculation'
  }
})
</script>

<style scoped>
.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.98);
  z-index: 9999;
  display: flex;
  align-items: center;
  justify-content: center;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.bagua-wrapper {
  width: 160rpx;
  height: 160rpx;
  margin-bottom: 40rpx;
  animation: rotate 4s linear infinite;
}

.bagua-image {
  width: 100%;
  height: 100%;
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.loading-text-container {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.loading-text-container text {
  display: block;
}

.loading-text-primary {
  font-size: 32rpx;
  color: #9CA3AF;
  letter-spacing: 0.1em;
}

.loading-text-secondary {
  font-size: 28rpx;
  color: #6B7280;
  margin-top: 20rpx;
}

.loading-text-tertiary {
  font-size: 24rpx;
  color: #9CA3AF;
  font-style: italic;
  margin-top: 8rpx;
}
</style>
