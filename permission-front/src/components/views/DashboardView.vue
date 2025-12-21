<template>
  <div class="dashboard-container">
    <div class="statistics-grid">
      <!-- 应用数量统计 -->
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
          <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <rect x="3" y="3" width="7" height="7" stroke="currentColor" stroke-width="2"/>
            <rect x="14" y="3" width="7" height="7" stroke="currentColor" stroke-width="2"/>
            <rect x="3" y="14" width="7" height="7" stroke="currentColor" stroke-width="2"/>
            <rect x="14" y="14" width="7" height="7" stroke="currentColor" stroke-width="2"/>
          </svg>
        </div>
        <div class="stat-content">
          <div class="stat-title">应用数量</div>
          <div class="stat-value">{{ statistics.appCount || 0 }}</div>
          <div class="stat-desc">个应用系统</div>
        </div>
      </div>

      <!-- 登录用户账号数统计 -->
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);">
          <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <circle cx="12" cy="7" r="4" stroke="currentColor" stroke-width="2"/>
          </svg>
        </div>
        <div class="stat-content">
          <div class="stat-title">登录用户数</div>
          <div class="stat-value">{{ statistics.loginUserCount || 0 }}</div>
          <div class="stat-desc">个登录账号</div>
        </div>
      </div>

      <!-- 在线用户数统计 -->
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);">
          <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/>
            <circle cx="12" cy="12" r="6" stroke="currentColor" stroke-width="2"/>
            <circle cx="12" cy="12" r="2" fill="currentColor"/>
          </svg>
        </div>
        <div class="stat-content">
          <div class="stat-title">在线用户数</div>
          <div class="stat-value">{{ statistics.onlineUserCount || 0 }}</div>
          <div class="stat-desc">人在线</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { statisticsApi } from '@/utils/api'

const statistics = ref({
  appCount: 0,
  loginUserCount: 0,
  onlineUserCount: 0
})

const loading = ref(false)

// 加载统计数据
const loadStatistics = async () => {
  loading.value = true
  try {
    const response = await statisticsApi.getDashboardStatistics()
    if (response.code === 200 && response.data) {
      statistics.value.appCount = response.data.appCount || 0
      statistics.value.loginUserCount = response.data.loginUserCount || 0
      statistics.value.onlineUserCount = response.data.onlineUserCount || 0
    } else {
      message.error(response.message || '获取统计数据失败')
    }
  } catch (err) {
    if (import.meta.env.DEV) {
      console.error('加载统计数据失败:', err)
    }
    message.error(err.message || '加载统计数据失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadStatistics()
})
</script>

<style scoped>
.dashboard-container {
  padding: 24px;
  min-height: 100%;
}

.statistics-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 24px;
}

.stat-card {
  background: var(--bg-secondary, #ffffff);
  border: 1px solid var(--border-color, #e5e7eb);
  border-radius: 12px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.stat-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
  transform: translateY(-2px);
}

.stat-icon {
  width: 64px;
  height: 64px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-icon svg {
  width: 32px;
  height: 32px;
  color: #ffffff;
}

.stat-content {
  flex: 1;
}

.stat-title {
  font-size: 14px;
  color: var(--text-secondary, #64748b);
  margin-bottom: 8px;
}

.stat-value {
  font-size: 32px;
  font-weight: 600;
  color: var(--text-primary, #1e293b);
  line-height: 1.2;
  margin-bottom: 4px;
}

.stat-desc {
  font-size: 12px;
  color: var(--text-secondary, #94a3b8);
}

@media (max-width: 768px) {
  .statistics-grid {
    grid-template-columns: 1fr;
  }
  
  .stat-card {
    padding: 20px;
  }
  
  .stat-icon {
    width: 56px;
    height: 56px;
  }
  
  .stat-icon svg {
    width: 28px;
    height: 28px;
  }
  
  .stat-value {
    font-size: 28px;
  }
}
</style>

