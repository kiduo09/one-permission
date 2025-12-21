<template>
  <div>
    <!-- 筛选栏 -->
    <div class="filter-bar">
      <div class="filters-row">
        <a-input
          v-model:value="filters.consumerName"
          placeholder="请输入消费者名称"
          style="width: 180px;"
          allow-clear
        >
          <template #prefix>
            <span class="filter-label">消费者名称：</span>
          </template>
        </a-input>
        <a-input
          v-model:value="filters.clientId"
          placeholder="请输入clientId"
          style="width: 180px;"
          allow-clear
        >
          <template #prefix>
            <span class="filter-label">clientId：</span>
          </template>
        </a-input>
        <a-select
          v-model:value="filters.status"
          placeholder="全部状态"
          style="width: 180px;"
          allow-clear
        >
          <a-select-option value="">全部状态</a-select-option>
          <a-select-option :value="1">启用</a-select-option>
          <a-select-option :value="0">禁用</a-select-option>
        </a-select>
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
      :data-source="consumerList"
      :loading="loading"
      :pagination="false"
      :row-key="record => record.id"
      :scroll="{ x: 'max-content' }"
      bordered
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'status'">
          <a-tag :color="record.status === 1 ? 'success' : 'error'">
            {{ record.statusText }}
          </a-tag>
        </template>
        <template v-if="column.key === 'clientId'">
          <div class="copyable-field">
            <span>{{ record.clientId }}</span>
            <a-button 
              type="text" 
              size="small" 
              class="copy-btn"
              @click="copyToClipboard(record.clientId, 'clientId')"
              title="复制clientId"
            >
              <template #icon>
                <CopyOutlined />
              </template>
            </a-button>
          </div>
        </template>
        <template v-if="column.key === 'clientSecret'">
          <div class="copyable-field">
            <span>{{ maskClientSecret(record.clientSecret) }}</span>
            <a-button 
              type="text" 
              size="small" 
              class="copy-btn"
              @click="copyToClipboard(record.clientSecret, 'clientSecret')"
              title="复制clientSecret"
            >
              <template #icon>
                <CopyOutlined />
              </template>
            </a-button>
          </div>
        </template>
        <template v-if="column.key === 'createTime'">
          <span style="white-space: nowrap;">{{ formatDate(record.createTime) }}</span>
        </template>
        <template v-if="column.key === 'updateTime'">
          <span style="white-space: nowrap;">{{ formatDate(record.updateTime) }}</span>
        </template>
        <template v-if="column.key === 'action'">
          <div class="action-buttons">
            <a-button type="link" size="small" @click="openEdit(record)">编辑</a-button>
            <a-divider type="vertical" />
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

    <!-- 新增/编辑抽屉 -->
    <a-drawer
      v-model:open="showForm"
      :title="formMode === 'create' ? '新增消费者' : '编辑消费者'"
      :width="600"
      placement="right"
      @close="closeForm"
    >
      <a-form ref="formRef" :model="formData" layout="horizontal" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
        <a-form-item label="消费者名称" :rules="[{ required: true, message: '请输入消费者名称' }]">
          <a-input
            v-model:value="formData.consumerName"
            placeholder="请输入消费者名称"
            :maxlength="50"
          />
        </a-form-item>
        <a-form-item 
          v-if="formMode === 'create'" 
          label="clientId"
        >
          <a-input
            v-model:value="formData.clientId"
            disabled
            placeholder="系统自动生成"
            style="background-color: #f5f5f5;"
          />
        </a-form-item>
        <a-form-item 
          v-if="formMode === 'edit'" 
          label="clientId"
        >
          <a-input
            v-model:value="formData.clientId"
            disabled
            style="background-color: #f5f5f5;"
          />
        </a-form-item>
        <a-form-item 
          v-if="formMode === 'create'" 
          label="clientSecret"
        >
          <a-input-password
            v-model:value="formData.clientSecret"
            disabled
            placeholder="系统自动生成"
            style="background-color: #f5f5f5;"
          />
        </a-form-item>
        <a-form-item 
          v-if="formMode === 'edit'" 
          label="clientSecret"
        >
          <a-input-password
            v-model:value="formData.clientSecret"
            disabled
            placeholder="clientSecret不可修改"
            style="background-color: #f5f5f5;"
          />
        </a-form-item>
        <a-form-item label="状态">
          <a-radio-group v-model:value="formData.status">
            <a-radio :value="1">启用</a-radio>
            <a-radio :value="0">禁用</a-radio>
          </a-radio-group>
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
      title="删除消费者"
      @ok="confirmDelete"
      @cancel="closeDelete"
    >
      <p>确认删除消费者「{{ currentItem?.consumerName }}」吗？该操作不可恢复。</p>
    </a-modal>
  </div>
</template>

<script setup>
import { computed, ref, watch, onMounted } from 'vue'
import { consumerInfoApi } from '../../utils/api'
import { message } from 'ant-design-vue'
import { CopyOutlined } from '@ant-design/icons-vue'

const formRef = ref(null)

// 表格列定义
const columns = [
  {
    title: 'ID',
    dataIndex: 'id',
    key: 'id',
    width: 80,
    fixed: 'left'
  },
  {
    title: '消费者名称',
    dataIndex: 'consumerName',
    key: 'consumerName',
    width: 150,
    ellipsis: true
  },
  {
    title: 'clientId',
    dataIndex: 'clientId',
    key: 'clientId',
    width: 180,
    ellipsis: true
  },
  {
    title: 'clientSecret',
    dataIndex: 'clientSecret',
    key: 'clientSecret',
    width: 200,
    ellipsis: true
  },
  {
    title: '状态',
    dataIndex: 'status',
    key: 'status',
    width: 100
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    key: 'createTime',
    width: 180
  },
  {
    title: '更新时间',
    dataIndex: 'updateTime',
    key: 'updateTime',
    width: 180
  },
  {
    title: '操作',
    key: 'action',
    width: 150,
    fixed: 'right'
  }
]

// 数据
const consumerList = ref([])
const totalRecords = ref(0)

const filters = ref({
  consumerName: '',
  clientId: '',
  status: ''
})
const showForm = ref(false)
const showDelete = ref(false)
const formMode = ref('create')
const currentItem = ref(null)
const currentPage = ref(1)
const pageSize = ref(10)
const loading = ref(false)

const formData = ref({
  id: null,
  consumerName: '',
  clientId: '',
  clientSecret: '',
  status: 1
})

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    // 构建查询参数，过滤掉空值和undefined
    const params = {
      current: currentPage.value,
      size: pageSize.value
    }
    
    // 只添加有值的筛选条件
    if (filters.value.consumerName && filters.value.consumerName.trim()) {
      params.consumerName = filters.value.consumerName.trim()
    }
    if (filters.value.clientId && filters.value.clientId.trim()) {
      params.clientId = filters.value.clientId.trim()
    }
    if (filters.value.status !== '' && filters.value.status !== null && filters.value.status !== undefined) {
      params.status = filters.value.status
    }
    
    const response = await consumerInfoApi.pageQuery(params)
    
    if (response && response.code === 200) {
      if (response.data) {
        consumerList.value = Array.isArray(response.data.list) ? response.data.list : []
        // 确保total是数字类型
        const total = response.data.total
        totalRecords.value = total !== null && total !== undefined ? Number(total) : 0
      } else {
        consumerList.value = []
        totalRecords.value = 0
      }
    } else {
      consumerList.value = []
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
  if (totalRecords.value === 0 || consumerList.value.length === 0) return 0
  return (currentPage.value - 1) * pageSize.value + 1
})

const endIndex = computed(() => {
  if (totalRecords.value === 0 || consumerList.value.length === 0) return 0
  const end = currentPage.value * pageSize.value
  return end > totalRecords.value ? totalRecords.value : end
})

watch([() => filters.value.consumerName, () => filters.value.clientId, () => filters.value.status], () => {
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
  filters.value.consumerName = ''
  filters.value.clientId = ''
  filters.value.status = ''
  currentPage.value = 1
  loadData()
}

const openCreate = () => {
  formMode.value = 'create'
  formData.value = {
    id: null,
    consumerName: '',
    clientId: '',
    clientSecret: '',
    status: 1
  }
  showForm.value = true
}

const openEdit = (item) => {
  formMode.value = 'edit'
  formData.value = {
    id: item.id,
    consumerName: item.consumerName,
    clientId: item.clientId,
    clientSecret: item.clientSecret || '', // 编辑时显示原密码（用于查看和修改）
    status: item.status
  }
  showForm.value = true
}

const closeForm = () => {
  showForm.value = false
  if (formRef.value) {
    formRef.value.resetFields()
  }
}

const submitForm = async () => {
  try {
    await formRef.value.validate()
  } catch (error) {
    return
  }

  try {
    if (formMode.value === 'create') {
      // 创建时，appKey和appSecret由后端自动生成，前端不需要传递
      const createData = {
        consumerName: formData.value.consumerName,
        status: formData.value.status
      }
      const response = await consumerInfoApi.create(createData)
      // 创建成功后，将返回的数据显示在表单中（用于查看生成的appKey和appSecret）
      if (response.code === 200 && response.data) {
        formData.value.clientId = response.data.clientId
        formData.value.clientSecret = response.data.clientSecret
        message.success('创建成功，请记录clientId和clientSecret')
        // 延迟关闭，让用户有时间查看生成的appKey和appSecret
        setTimeout(() => {
          closeForm()
          loadData()
        }, 2000)
      } else {
        closeForm()
        loadData()
      }
    } else {
      // 编辑时，只传递需要更新的字段
      const updateData = {
        consumerName: formData.value.consumerName,
        clientSecret: formData.value.clientSecret,
        status: formData.value.status
      }
      await consumerInfoApi.update(formData.value.id, updateData)
      message.success('更新成功')
      closeForm()
      loadData()
    }
  } catch (err) {
    if (import.meta.env.DEV) {
      console.error('保存失败:', err)
    }
    message.error(err.message || '保存失败')
  }
}

const openDelete = (item) => {
  currentItem.value = item
  showDelete.value = true
}

const closeDelete = () => {
  showDelete.value = false
  currentItem.value = null
}

const confirmDelete = async () => {
  try {
    await consumerInfoApi.delete(currentItem.value.id)
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

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  const seconds = String(date.getSeconds()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}

// 格式化clientSecret，显示后10位，其余用星号代替
const maskClientSecret = (secret) => {
  if (!secret || typeof secret !== 'string' || secret.length <= 10) {
    return '******'
  }
  const visiblePart = secret.slice(-10)
  const maskedPart = '*'.repeat(Math.max(0, secret.length - 10))
  return maskedPart + visiblePart
}

// 复制到剪贴板
const copyToClipboard = async (text, fieldName) => {
  if (!text || text === 'undefined' || text === 'null') {
    message.error(`${fieldName} 数据不存在，无法复制`)
    return
  }
  try {
    await navigator.clipboard.writeText(text)
    message.success(`${fieldName} 已复制到剪贴板`)
  } catch (err) {
    // 降级方案：使用传统方法
    const textArea = document.createElement('textarea')
    textArea.value = text
    textArea.style.position = 'fixed'
    textArea.style.left = '-999999px'
    document.body.appendChild(textArea)
    textArea.select()
    try {
      document.execCommand('copy')
      message.success(`${fieldName} 已复制到剪贴板`)
    } catch (e) {
      message.error('复制失败，请手动复制')
    }
    document.body.removeChild(textArea)
  }
}
</script>

<style scoped>
.filter-bar {
  background: #ffffff;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.filters-row {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  align-items: center;
}

.filter-actions {
  display: flex;
  gap: 8px;
}

.action-row {
  margin-top: 12px;
}

.action-buttons {
  display: flex;
  align-items: center;
  gap: 0;
}

.copyable-field {
  display: flex;
  align-items: center;
  gap: 8px;
}

.copyable-field span {
  flex: 1;
  word-break: break-all;
}

.copy-btn {
  flex-shrink: 0;
  padding: 0;
  color: var(--ant-color-primary);
  opacity: 0.7;
  transition: opacity 0.2s;
}

.copy-btn:hover {
  opacity: 1;
}

.filter-label {
  font-size: 12px;
  color: #666;
  margin-right: 4px;
}

.filter-label-inline {
  font-size: 14px;
  color: #666;
}
</style>

