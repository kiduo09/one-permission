<template>
  <div>
    <!-- 筛选栏 -->
    <div class="filter-bar">
      <div class="filters-row">
        <a-input
          v-model:value="filters.userName"
          placeholder="请输入用户账号"
          style="width: 180px;"
          allow-clear
        >
          <template #prefix>
            <span class="filter-label">用户账号：</span>
          </template>
        </a-input>
        <a-input
          v-model:value="filters.ipaddr"
          placeholder="请输入IP地址"
          style="width: 180px;"
          allow-clear
        >
          <template #prefix>
            <span class="filter-label">IP地址：</span>
          </template>
        </a-input>
        <a-select
          v-model:value="filters.status"
          placeholder="全部状态"
          style="width: 180px;"
          allow-clear
        >
          <a-select-option value="">全部状态</a-select-option>
          <a-select-option value="0">成功</a-select-option>
          <a-select-option value="1">失败</a-select-option>
        </a-select>
        <a-range-picker
          v-model:value="dateRange"
          format="YYYY-MM-DD HH:mm:ss"
          show-time
          style="width: 400px;"
          @change="handleDateRangeChange"
        />
        <div class="filter-actions">
          <a-button type="primary" @click="handleSearch">搜索</a-button>
          <a-button @click="resetFilter">重置</a-button>
        </div>
      </div>
      <div class="action-row">
        <a-button danger @click="openClean">清空日志</a-button>
      </div>
    </div>

    <!-- 表格 -->
    <a-table
      :columns="columns"
      :data-source="logininforList"
      :loading="loading"
      :pagination="false"
      :row-key="record => record.infoId"
      :scroll="{ x: 'max-content' }"
      bordered
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'status'">
          <a-tag :color="record.status === '0' ? 'success' : 'error'">
            {{ record.status === '0' ? '成功' : '失败' }}
          </a-tag>
        </template>
        <template v-if="column.key === 'action'">
          <div class="action-buttons">
            <a-button type="link" danger size="small" @click="openDelete(record)">删除</a-button>
          </div>
        </template>
      </template>
    </a-table>

    <!-- 分页 -->
    <div style="margin-top: 16px; display: flex; justify-content: space-between; align-items: center;">
      <span class="filter-label-inline">
        <template v-if="totalRecords > 0">
          显示 {{ startIndex }}-{{ endIndex }}，共 {{ totalRecords }} 条记录
        </template>
        <template v-else>
          暂无数据
        </template>
      </span>
      <a-pagination
        v-model:current="currentPage"
        v-model:page-size="pageSize"
        :total="totalRecords"
        :show-size-changer="true"
        :show-total="(total) => `共 ${total} 条`"
        @change="loadData"
        @show-size-change="loadData"
      />
    </div>

    <!-- 删除确认对话框 -->
    <a-modal
      v-model:visible="showDelete"
      title="确认删除"
      @ok="confirmDelete"
      @cancel="closeDelete"
    >
      <p>确定要删除这条登录日志吗？</p>
    </a-modal>

    <!-- 清空确认对话框 -->
    <a-modal
      v-model:visible="showClean"
      title="确认清空"
      @ok="confirmClean"
      @cancel="closeClean"
    >
      <p>确定要清空所有登录日志吗？此操作不可恢复！</p>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { sysLogininforApi } from '@/utils/api'
import dayjs from 'dayjs'

// 筛选条件
const filters = ref({
  userName: '',
  ipaddr: '',
  status: '',
  startTime: null,
  endTime: null
})

const dateRange = ref(null)

// 分页
const currentPage = ref(1)
const pageSize = ref(10)
const totalRecords = ref(0)
const loading = ref(false)

// 数据列表
const logininforList = ref([])

// 当前操作项
const currentItem = ref(null)
const showDelete = ref(false)
const showClean = ref(false)

// 表格列定义
const columns = [
  {
    title: '用户账号',
    dataIndex: 'userName',
    key: 'userName',
    width: 150
  },
  {
    title: 'IP地址',
    dataIndex: 'ipaddr',
    key: 'ipaddr',
    width: 150
  },
  {
    title: '登录地点',
    dataIndex: 'loginLocation',
    key: 'loginLocation',
    width: 150
  },
  {
    title: '浏览器',
    dataIndex: 'browser',
    key: 'browser',
    width: 120
  },
  {
    title: '操作系统',
    dataIndex: 'os',
    key: 'os',
    width: 150
  },
  {
    title: '登录状态',
    dataIndex: 'status',
    key: 'status',
    width: 100
  },
  {
    title: '提示消息',
    dataIndex: 'msg',
    key: 'msg',
    width: 200
  },
  {
    title: '登录时间',
    dataIndex: 'loginTime',
    key: 'loginTime',
    width: 180
  },
  {
    title: '操作',
    key: 'action',
    width: 100,
    fixed: 'right'
  }
]

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    // 构建查询参数
    const params = {
      current: currentPage.value,
      size: pageSize.value
    }
    
    // 只添加有值的筛选条件
    if (filters.value.userName && filters.value.userName.trim()) {
      params.userName = filters.value.userName.trim()
    }
    if (filters.value.ipaddr && filters.value.ipaddr.trim()) {
      params.ipaddr = filters.value.ipaddr.trim()
    }
    if (filters.value.status && filters.value.status.trim()) {
      params.status = filters.value.status.trim()
    }
    if (filters.value.startTime) {
      params.startTime = filters.value.startTime
    }
    if (filters.value.endTime) {
      params.endTime = filters.value.endTime
    }
    
    if (import.meta.env.DEV) {
    }
    
    const response = await sysLogininforApi.getList(params)
    
    if (import.meta.env.DEV) {
    }
    
    if (response && response.code === 200) {
      if (response.data) {
        logininforList.value = Array.isArray(response.data.list) ? response.data.list : []
        const total = response.data.total
        totalRecords.value = total !== null && total !== undefined ? Number(total) : 0
        
        if (import.meta.env.DEV) {
        }
      } else {
        logininforList.value = []
        totalRecords.value = 0
      }
    } else {
      logininforList.value = []
      totalRecords.value = 0
      if (response && response.message) {
        message.error(response.message)
      }
    }
  } catch (err) {
    if (import.meta.env.DEV) {
      console.error('加载数据失败:', err)
    }
    message.error(err.message || '加载数据失败')
  } finally {
    loading.value = false
  }
}

// 初始化加载
onMounted(() => {
  loadData()
})

const startIndex = computed(() => {
  if (totalRecords.value === 0 || logininforList.value.length === 0) return 0
  return (currentPage.value - 1) * pageSize.value + 1
})

const endIndex = computed(() => {
  if (totalRecords.value === 0 || logininforList.value.length === 0) return 0
  const end = currentPage.value * pageSize.value
  return end > totalRecords.value ? totalRecords.value : end
})

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  loadData()
}

// 重置筛选
const resetFilter = () => {
  filters.value = {
    userName: '',
    ipaddr: '',
    status: '',
    startTime: null,
    endTime: null
  }
  dateRange.value = null
  currentPage.value = 1
  loadData()
}

// 日期范围变化
const handleDateRangeChange = (dates) => {
  if (dates && dates.length === 2) {
    filters.value.startTime = dayjs(dates[0]).format('YYYY-MM-DD HH:mm:ss')
    filters.value.endTime = dayjs(dates[1]).format('YYYY-MM-DD HH:mm:ss')
  } else {
    filters.value.startTime = null
    filters.value.endTime = null
  }
}

// 删除
const openDelete = (item) => {
  currentItem.value = item
  showDelete.value = true
}

const closeDelete = () => {
  currentItem.value = null
  showDelete.value = false
}

const confirmDelete = async () => {
  if (currentItem.value) {
    try {
      await sysLogininforApi.delete(currentItem.value.infoId)
      message.success('删除成功')
      closeDelete()
      loadData()
    } catch (err) {
      if (import.meta.env.DEV) {
        console.error('删除失败:', err)
      }
      message.error(err.message || '删除失败')
    }
  }
}

// 清空
const openClean = () => {
  showClean.value = true
}

const closeClean = () => {
  showClean.value = false
}

const confirmClean = async () => {
  try {
    await sysLogininforApi.clean()
    message.success('清空成功')
    closeClean()
    loadData()
  } catch (err) {
    if (import.meta.env.DEV) {
      console.error('清空失败:', err)
    }
    message.error(err.message || '清空失败')
  }
}
</script>

<style scoped>
.filter-bar {
  background: var(--bg-primary, #ffffff);
  padding: 16px;
  margin-bottom: 16px;
  border-radius: 4px;
}

.filters-row {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  align-items: center;
  margin-bottom: 12px;
}

.filter-label {
  color: var(--text-secondary, #666);
  font-size: 14px;
  margin-right: 8px;
}

.filter-actions {
  display: flex;
  gap: 8px;
}

.action-row {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

.action-buttons {
  display: flex;
  gap: 8px;
}

.filter-label-inline {
  color: var(--text-secondary, #666);
  font-size: 14px;
}
</style>

