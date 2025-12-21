<template>
  <div>
    <!-- 筛选栏 -->
    <div class="filter-bar">
      <div class="filters-row">
        <a-input
          v-model:value="filters.name"
          placeholder="请输入角色名称"
          style="width: 180px;"
          allow-clear
        >
          <template #prefix>
            <span class="filter-label">角色名称：</span>
          </template>
        </a-input>
        <div class="filter-item">
          <span class="filter-label">状态：</span>
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
        </div>
        <div class="filter-actions">
          <a-button type="primary" @click="handleSearch">搜索</a-button>
          <a-button @click="resetFilter">重置</a-button>
        </div>
      </div>
      <div class="action-row">
        <a-button type="primary" @click="openCreate">＋ 新增</a-button>
      </div>
    </div>

    <!-- 表格 -->
    <a-table
      :columns="columns"
      :data-source="paginatedData"
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
            <a-button type="link" size="small" @click="openEdit(record)">修改</a-button>
            <a-divider type="vertical" />
            <a-button type="link" danger size="small" @click="openDelete(record)">删除</a-button>
          </div>
        </template>
      </template>
      <template #emptyText>
        <a-empty description="暂无数据" />
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
        @change="handlePageChange"
        @show-size-change="handlePageChange"
      />
    </div>

    <!-- 新增/编辑抽屉 -->
    <a-drawer
      v-model:open="showForm"
      :title="formMode === 'create' ? '新增系统角色' : '修改系统角色'"
      :width="480"
      placement="right"
      @close="closeForm"
    >
      <a-form ref="formRef" :model="formData" layout="horizontal" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
        <a-form-item 
          label="角色名称" 
          name="name"
          :rules="[{ required: true, message: '请输入角色名称' }]"
        >
          <a-input v-model:value="formData.name" placeholder="请输入角色名称" />
        </a-form-item>
        <a-form-item label="显示顺序">
          <a-input-number v-model:value="formData.sort" :min="0" placeholder="请输入显示顺序" style="width: 100%;" />
        </a-form-item>
        <a-form-item label="状态">
          <a-select v-model:value="formData.status">
            <a-select-option value="正常">正常</a-select-option>
            <a-select-option value="禁用">禁用</a-select-option>
          </a-select>
        </a-form-item>
      </a-form>
      <template #footer>
        <div style="text-align: right;">
          <a-button style="margin-right: 8px;" @click="closeForm">取消</a-button>
          <a-button type="primary" @click="submitForm">
            {{ formMode === 'create' ? '保存' : '更新' }}
          </a-button>
        </div>
      </template>
    </a-drawer>

    <!-- 删除确认 -->
    <a-modal
      v-model:open="showDelete"
      title="删除系统角色"
      @ok="confirmDelete"
      @cancel="closeDelete"
    >
      <p>确认删除系统角色「{{ currentItem?.name }}」吗？该操作不可恢复。</p>
    </a-modal>
  </div>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
import { message } from 'ant-design-vue'

const formRef = ref(null)

// 表格列定义
const columns = [
  {
    title: '角色名称',
    dataIndex: 'name',
    key: 'name',
    width: 150,
    ellipsis: true
  },
  {
    title: '显示顺序',
    dataIndex: 'sort',
    key: 'sort',
    width: 100
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
    width: 200,
    fixed: 'right'
  }
]

const roles = ref([
  { id: 1, name: '超级管理员', sort: 1, status: '正常' },
  { id: 2, name: '系统管理员', sort: 2, status: '正常' },
  { id: 3, name: '普通管理员', sort: 3, status: '正常' }
])

const loading = ref(false)

const filters = ref({
  name: '',
  status: ''
})
const showForm = ref(false)
const showDelete = ref(false)
const formMode = ref('create')
const currentItem = ref(null)
const currentPage = ref(1)
const pageSize = ref(10)

const formData = ref({
  id: null,
  name: '',
  sort: 0,
  status: '正常'
})

const filteredData = computed(() => {
  return roles.value.filter((item) => {
    const matchName = filters.value.name
      ? item.name.toLowerCase().includes(filters.value.name.toLowerCase())
      : true
    const matchStatus = filters.value.status
      ? item.status === filters.value.status
      : true
    return matchName && matchStatus
  })
})

const totalRecords = computed(() => filteredData.value.length)
const totalPages = computed(() => Math.ceil(totalRecords.value / pageSize.value))

const paginatedData = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredData.value.slice(start, end)
})

const startIndex = computed(() => {
  if (totalRecords.value === 0) return 0
  return (currentPage.value - 1) * pageSize.value + 1
})

const endIndex = computed(() => {
  const end = currentPage.value * pageSize.value
  return end > totalRecords.value ? totalRecords.value : end
})

const visiblePages = computed(() => {
  const pages = []
  const total = totalPages.value
  const current = currentPage.value
  
  if (total <= 7) {
    for (let i = 1; i <= total; i++) {
      pages.push(i)
    }
  } else {
    pages.push(1)
    if (current <= 4) {
      for (let i = 2; i <= 5; i++) {
        pages.push(i)
      }
      pages.push('...')
      pages.push(total)
    } else if (current >= total - 3) {
      pages.push('...')
      for (let i = total - 4; i <= total; i++) {
        pages.push(i)
      }
    } else {
      pages.push('...')
      for (let i = current - 1; i <= current + 1; i++) {
        pages.push(i)
      }
      pages.push('...')
      pages.push(total)
    }
  }
  return pages
})

watch([() => filters.value.name, () => filters.value.status], () => {
  currentPage.value = 1
})

const handleSearch = () => {
  // 搜索逻辑已经在computed中实现，这里只需要触发重新计算
  currentPage.value = 1
}

const resetFilter = () => {
  filters.value.name = ''
  filters.value.status = ''
  currentPage.value = 1
}

const handlePageChange = () => {
  // 分页变化时触发，数据已经在computed中自动更新
}

const openCreate = () => {
  formMode.value = 'create'
  formData.value = {
    id: null,
    name: '',
    sort: 0,
    status: '正常'
  }
  showForm.value = true
}

const openEdit = (item) => {
  formMode.value = 'edit'
  formData.value = { ...item }
  showForm.value = true
}

const closeForm = () => {
  showForm.value = false
  // 重置表单验证状态
  if (formRef.value) {
    formRef.value.resetFields()
  }
}

const submitForm = async () => {
  // 表单验证
  try {
    await formRef.value.validate()
  } catch (error) {
    // 验证失败，表单会自动显示错误提示
    return
  }

  if (formMode.value === 'create') {
    roles.value.push({
      id: Date.now(),
      ...formData.value
    })
    message.success('创建成功')
  } else {
    const index = roles.value.findIndex(r => r.id === formData.value.id)
    if (index !== -1) {
      roles.value[index] = { ...formData.value }
      message.success('更新成功')
    }
  }
  showForm.value = false
}

const openDelete = (item) => {
  currentItem.value = item
  showDelete.value = true
}

const closeDelete = () => {
  currentItem.value = null
  showDelete.value = false
}

const confirmDelete = () => {
  if (currentItem.value) {
    roles.value = roles.value.filter(r => r.id !== currentItem.value.id)
    message.success('删除成功')
    closeDelete()
  }
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

.filter-item {
  display: flex;
  align-items: center;
  gap: 0;
}

.filter-label {
  color: #64748b;
  font-size: 13px;
  margin-right: 8px;
  white-space: nowrap;
}

.filter-actions {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
}

.action-row {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  width: 100%;
}

/* 操作按钮容器 */
.action-buttons {
  display: flex;
  align-items: center;
  gap: 0;
  white-space: nowrap;
  flex-wrap: nowrap;
}

.action-buttons :deep(.ant-btn-link) {
  padding: 0 4px;
  height: auto;
  line-height: 1.5;
}

.action-buttons :deep(.ant-divider-vertical) {
  margin: 0 4px;
  height: 14px;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .filters-row {
    gap: 8px;
  }
  
  .filters-row :deep(.ant-input),
  .filters-row :deep(.ant-select) {
    width: 160px !important;
  }
}

@media (max-width: 768px) {
  .filter-bar {
    padding: 10px 12px;
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
  
  .action-row {
    justify-content: flex-start;
  }
  
  /* 表格在小屏幕下横向滚动 */
  :deep(.ant-table-wrapper) {
    overflow-x: auto;
  }
  
  .action-buttons {
    min-width: 200px;
  }
}

/* 表格自适应 */
:deep(.ant-table) {
  min-width: 600px;
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

