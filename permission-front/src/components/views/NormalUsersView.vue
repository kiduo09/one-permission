<template>
  <div>
    <!-- 筛选栏 -->
    <div class="filter-bar">
      <div class="filters-row">
        <a-input
          v-model:value="filters.workNo"
          placeholder="请输入工号"
          style="width: 180px;"
          allow-clear
        >
          <template #prefix>
            <span class="filter-label">工号：</span>
          </template>
        </a-input>
        <a-input
          v-model:value="filters.name"
          placeholder="请输入姓名"
          style="width: 180px;"
          allow-clear
        >
          <template #prefix>
            <span class="filter-label">姓名：</span>
          </template>
        </a-input>
        <a-input
          v-model:value="filters.departmentName"
          placeholder="请输入部门名称"
          style="width: 180px;"
          allow-clear
        >
          <template #prefix>
            <span class="filter-label">部门：</span>
          </template>
        </a-input>
        <a-select
          v-model:value="filters.status"
          placeholder="全部状态"
          style="width: 180px;"
          allow-clear
        >
          <a-select-option value="">全部状态</a-select-option>
          <a-select-option value="正常">正常</a-select-option>
          <a-select-option value="禁用">禁用</a-select-option>
        </a-select>
        <div class="filter-actions">
          <a-button type="primary" @click="handleSearch">搜索</a-button>
          <a-button @click="resetFilter">重置</a-button>
        </div>
      </div>
    </div>

    <!-- 表格 -->
    <a-table
      :columns="columns"
      :data-source="normalUsers"
      :loading="loading"
      :pagination="false"
      :row-key="record => record.id"
      :scroll="{ x: 'max-content' }"
      bordered
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'status'">
          <a-tag :color="record.status === '正常' ? 'success' : 'error'">
            {{ record.status }}
          </a-tag>
        </template>
        <template v-if="column.key === 'action'">
          <div class="action-buttons">
            <a-button type="link" size="small" @click="openDetail(record)">查看</a-button>
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

    <!-- 详情抽屉 -->
    <a-drawer
      v-model:open="showDetail"
      title="普通用户详情"
      :width="600"
      placement="right"
      @close="closeDetail"
    >
      <a-descriptions :column="1" bordered>
        <a-descriptions-item label="工号">
          {{ currentItem?.workNo || '-' }}
        </a-descriptions-item>
        <a-descriptions-item label="姓名">
          {{ currentItem?.name || '-' }}
        </a-descriptions-item>
        <a-descriptions-item label="部门">
          {{ currentItem?.departmentName || '-' }}
        </a-descriptions-item>
        <a-descriptions-item label="AD域账号">
          {{ currentItem?.adAccount || '-' }}
        </a-descriptions-item>
        <a-descriptions-item label="手机号码">
          {{ currentItem?.mobile || '-' }}
        </a-descriptions-item>
        <a-descriptions-item label="邮箱">
          {{ currentItem?.email || '-' }}
        </a-descriptions-item>
        <a-descriptions-item label="用户昵称">
          {{ currentItem?.nickname || '-' }}
        </a-descriptions-item>
        <a-descriptions-item label="职级">
          {{ currentItem?.level || '-' }}
        </a-descriptions-item>
        <a-descriptions-item label="状态">
          <a-tag :color="currentItem?.status === '正常' ? 'success' : 'error'">
            {{ currentItem?.status || '-' }}
          </a-tag>
        </a-descriptions-item>
      </a-descriptions>
      <template #footer>
        <div style="text-align: right;">
          <a-button @click="closeDetail">关闭</a-button>
        </div>
      </template>
    </a-drawer>
  </div>
</template>

<script setup>
import { computed, ref, watch, onMounted } from 'vue'
import { normalUserApi } from '../../utils/api'
import { message } from 'ant-design-vue'

// 表格列定义
const columns = [
  {
    title: '工号',
    dataIndex: 'workNo',
    key: 'workNo',
    width: 120
  },
  {
    title: '姓名',
    dataIndex: 'name',
    key: 'name',
    width: 120
  },
  {
    title: '部门',
    dataIndex: 'departmentName',
    key: 'departmentName',
    width: 150,
    ellipsis: true
  },
  {
    title: '状态',
    dataIndex: 'status',
    key: 'status',
    width: 100
  },
  {
    title: '操作',
    key: 'action',
    width: 100,
    fixed: 'right'
  }
]

// 数据
const normalUsers = ref([])
const totalRecords = ref(0)

const filters = ref({
  workNo: '',
  name: '',
  departmentName: '',
  status: ''
})
const showDetail = ref(false)
const currentItem = ref(null)
const currentPage = ref(1)
const pageSize = ref(10)
const loading = ref(false)

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    // 构建查询参数，过滤掉空值和undefined
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value
    }
    
    // 只添加有值的筛选条件
    if (filters.value.workNo && filters.value.workNo.trim()) {
      params.workNo = filters.value.workNo.trim()
    }
    if (filters.value.name && filters.value.name.trim()) {
      params.name = filters.value.name.trim()
    }
    // 部门名称暂时不支持后端查询，前端可以过滤
    // 如果需要按部门ID查询，需要先查询部门列表获取ID
    if (filters.value.status && filters.value.status.trim()) {
      params.status = filters.value.status.trim()
    }
    
    const response = await normalUserApi.getList(params)
    if (response.code === 200 && response.data) {
      let list = response.data.list || []
      
      // 前端过滤部门名称（如果后端不支持）
      if (filters.value.departmentName && filters.value.departmentName.trim()) {
        const deptName = filters.value.departmentName.trim()
        list = list.filter(user => 
          user.departmentName && user.departmentName.includes(deptName)
        )
      }
      
      normalUsers.value = list
      // 确保total是数字类型
      const total = response.data.total
      totalRecords.value = total !== null && total !== undefined ? Number(total) : 0
    } else {
      normalUsers.value = []
      totalRecords.value = 0
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
  if (totalRecords.value === 0 || normalUsers.value.length === 0) return 0
  return (currentPage.value - 1) * pageSize.value + 1
})

const endIndex = computed(() => {
  if (totalRecords.value === 0 || normalUsers.value.length === 0) return 0
  const end = currentPage.value * pageSize.value
  return end > totalRecords.value ? totalRecords.value : end
})

watch([() => filters.value.workNo, () => filters.value.name, () => filters.value.departmentName, () => filters.value.status], () => {
  currentPage.value = 1
})

watch([currentPage, pageSize], () => {
  loadData()
})

const handleSearch = () => {
  currentPage.value = 1
  loadData()
}

const resetFilter = () => {
  filters.value.workNo = ''
  filters.value.name = ''
  filters.value.departmentName = ''
  filters.value.status = ''
  currentPage.value = 1
  loadData()
}

const openDetail = async (item) => {
  try {
    const response = await normalUserApi.getById(item.id)
    if (response.code === 200 && response.data) {
      currentItem.value = response.data
      showDetail.value = true
    }
  } catch (err) {
    if (import.meta.env.DEV) {
      console.error('查询详情失败:', err)
    }
    message.error(err.message || '查询详情失败')
  }
}

const closeDetail = () => {
  currentItem.value = null
  showDetail.value = false
}
</script>

<style scoped>
.filter-bar {
  display: flex;
  flex-direction: column;
  gap: 12px;
  background: #ffffff;
  padding: 12px 16px;
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  margin-bottom: 16px;
  margin-top: 0;
}

.filters-row {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
  width: 100%;
}

.filter-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.action-buttons {
  display: flex;
  align-items: center;
  gap: 0;
}

.action-buttons :deep(.ant-btn-link) {
  padding: 0 4px;
  height: auto;
}

.action-buttons :deep(.ant-divider-vertical) {
  margin: 0 4px;
  height: 14px;
}

/* 响应式布局 */
@media (max-width: 1200px) {
  .filters-row {
    flex-wrap: wrap;
  }
  
  .filters-row :deep(.ant-input),
  .filters-row :deep(.ant-select) {
    width: 180px !important;
  }
  
  .filter-bar {
    padding: 12px;
  }
  
  .filters-row {
    flex-direction: column;
    align-items: stretch;
    gap: 10px;
  }
  
  .filters-row :deep(.ant-input),
  .filters-row :deep(.ant-select) {
    width: 100% !important;
  }
  
  .filter-actions {
    width: 100%;
    justify-content: flex-end;
  }
  
  /* 表格在小屏幕下横向滚动 */
  :deep(.ant-table-wrapper) {
    overflow-x: auto;
  }
  
  .action-buttons {
    min-width: 100px;
  }
}

/* 表格自适应 */
:deep(.ant-table) {
  min-width: 800px;
}

:deep(.ant-table-container) {
  overflow-x: auto;
}

/* 分页响应式 */
@media (max-width: 768px) {
  :deep(.ant-pagination) {
    flex-wrap: wrap;
  }
  
  :deep(.ant-pagination-total-text) {
    width: 100%;
    margin-bottom: 8px;
    text-align: center;
  }
}
</style>
