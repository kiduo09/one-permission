<template>
  <div>
    <!-- 筛选栏 -->
    <div class="filter-bar">
      <div class="filters-row">
        <a-input
          v-model:value="filters.name"
          placeholder="请输入应用名称"
          style="width: 180px;"
          allow-clear
        >
          <template #prefix>
            <span class="filter-label">应用名称：</span>
          </template>
        </a-input>
        <a-input
          v-model:value="filters.appKey"
          placeholder="请输入应用Key"
          style="width: 180px;"
          allow-clear
        >
          <template #prefix>
            <span class="filter-label">应用Key：</span>
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
      <div class="action-row">
        <a-button type="primary" @click="openCreate">＋ 新增</a-button>
      </div>
    </div>

    <!-- 表格 -->
    <a-table
      :columns="columns"
      :data-source="applications"
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
        <template v-if="column.key === 'createTime'">
          <span style="white-space: nowrap;">{{ formatDate(record.createTime) }}</span>
        </template>
        <template v-if="column.key === 'action'">
          <div class="action-buttons">
            <a-button type="link" size="small" @click="openEdit(record)">编辑</a-button>
            <a-divider type="vertical" />
            <a-button type="link" size="small" @click="openAssignUsers(record)">授权用户</a-button>
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
      :title="formMode === 'create' ? '新增应用' : '编辑应用'"
      :width="480"
      placement="right"
      @close="closeForm"
    >
      <a-form ref="formRef" :model="formData" layout="horizontal" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
        <a-form-item 
          label="应用名称" 
          name="name"
          :rules="[{ required: true, message: '请输入应用名称' }]"
        >
          <a-input v-model:value="formData.name" placeholder="请输入应用名称" />
        </a-form-item>
        <a-form-item 
          label="应用Key" 
          name="appKey"
          :rules="[{ required: true, message: '请输入应用Key' }]"
        >
          <a-input v-model:value="formData.appKey" placeholder="请输入应用Key（如：oa_system）" />
        </a-form-item>
        <a-form-item label="状态">
          <a-select v-model:value="formData.status">
            <a-select-option value="正常">正常</a-select-option>
            <a-select-option value="禁用">禁用</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="备注">
          <a-textarea v-model:value="formData.remark" placeholder="请输入备注信息" :rows="3" />
        </a-form-item>
      </a-form>
      <template #footer>
        <div style="text-align: right;">
          <a-button style="margin-right: 8px;" @click="closeForm">取消</a-button>
          <a-button type="primary" @click="submitForm">{{ formMode === 'create' ? '保存' : '更新' }}</a-button>
        </div>
      </template>
    </a-drawer>

    <!-- 授权用户抽屉 -->
    <a-drawer
      v-model:open="showAssignUsers"
      :title="`授权用户 - ${currentItem?.name}`"
      :width="480"
      placement="right"
      @close="closeAssignUsers"
    >
      <div style="margin-bottom: 16px;">
        <a-checkbox
          :checked="selectedUserIds.length === allLoginUsers.length && allLoginUsers.length > 0"
          @change="selectAllUsers"
        >
          全选（已选择 {{ selectedUserIds.length }} / {{ allLoginUsers.length }}）
        </a-checkbox>
      </div>
      <div style="max-height: 500px; overflow-y: auto;">
        <div
          v-for="user in allLoginUsers"
          :key="user.id"
          style="padding: 12px; border-bottom: 1px solid #f1f5f9; display: flex; align-items: center; cursor: pointer;"
          @click="toggleSelectUser(user.id)"
        >
          <a-checkbox
            :checked="selectedUserIds.includes(user.id)"
            @click.stop="toggleSelectUser(user.id)"
            style="margin-right: 12px;"
          />
          <div style="flex: 1;">
            <div style="font-weight: 500; color: #1e293b;">{{ user.name }}</div>
            <div style="font-size: 12px; color: #64748b; margin-top: 4px;">
              {{ user.loginAccount }} | {{ user.email }}
            </div>
          </div>
          <a-tag v-if="assignedUserIds.includes(user.id)" color="success">
            已授权
          </a-tag>
        </div>
        <a-empty v-if="allLoginUsers.length === 0" description="暂无登录用户" />
      </div>
      <template #footer>
        <div style="text-align: right;">
          <a-button style="margin-right: 8px;" @click="closeAssignUsers">取消</a-button>
          <a-button type="primary" @click="confirmAssignUsers">确定</a-button>
        </div>
      </template>
    </a-drawer>

    <!-- 删除确认 -->
    <a-modal
      v-model:open="showDelete"
      title="删除应用"
      @ok="confirmDelete"
      @cancel="closeDelete"
    >
      <p>确认删除应用「{{ currentItem?.name }}」吗？该操作不可恢复。</p>
    </a-modal>
  </div>
</template>

<script setup>
import { computed, ref, watch, onMounted } from 'vue'
import { applicationApi, loginUserApi, loginUserAppApi } from '../../utils/api'
import { message } from 'ant-design-vue'

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
    title: '应用名称',
    dataIndex: 'name',
    key: 'name',
    width: 200,
    ellipsis: true
  },
  {
    title: '应用Key',
    dataIndex: 'appKey',
    key: 'appKey',
    width: 150,
    ellipsis: true
  },
  {
    title: '备注',
    dataIndex: 'remark',
    key: 'remark',
    ellipsis: true,
    minWidth: 150
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    key: 'createTime',
    width: 180,
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
    width: 200,
    fixed: 'right'
  }
]

// 数据
const applications = ref([])
const totalRecords = ref(0)

const filters = ref({
  name: '',
  appKey: '',
  status: ''
})
const showForm = ref(false)
const showDelete = ref(false)
const showAssignUsers = ref(false)
const formMode = ref('create')
const currentItem = ref(null)
const currentPage = ref(1)
const pageSize = ref(10)
const loading = ref(false)

// 授权用户相关
const allLoginUsers = ref([])
const assignedUserIds = ref([])
const selectedUserIds = ref([])

const formData = ref({
  id: null,
  name: '',
  appKey: '',
  status: '正常',
  remark: ''
})

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
    if (filters.value.name && filters.value.name.trim()) {
      params.name = filters.value.name.trim()
    }
    if (filters.value.appKey && filters.value.appKey.trim()) {
      params.appKey = filters.value.appKey.trim()
    }
    if (filters.value.status && filters.value.status.trim()) {
      params.status = filters.value.status.trim()
    }
    
    const response = await applicationApi.getList(params)
    if (response.code === 200 && response.data) {
      applications.value = response.data.list || []
      // 确保total是数字类型
      totalRecords.value = Number(response.data.total) || 0
    } else {
      applications.value = []
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
  if (totalRecords.value === 0 || applications.value.length === 0) return 0
  return (currentPage.value - 1) * pageSize.value + 1
})

const endIndex = computed(() => {
  if (totalRecords.value === 0 || applications.value.length === 0) return 0
  const end = currentPage.value * pageSize.value
  return end > totalRecords.value ? totalRecords.value : end
})

watch([() => filters.value.name, () => filters.value.appKey, () => filters.value.status], () => {
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
  filters.value.name = ''
  filters.value.appKey = ''
  filters.value.status = ''
  currentPage.value = 1
  loadData()
}

const openCreate = () => {
  formMode.value = 'create'
  formData.value = {
    id: null,
    name: '',
    appKey: '',
    status: '正常',
    remark: ''
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

  try {
    if (formMode.value === 'create') {
      // 新增
      const createData = {
        name: formData.value.name,
        appKey: formData.value.appKey,
        status: formData.value.status,
        remark: formData.value.remark
      }
      await applicationApi.create(createData)
      message.success('创建成功')
    } else {
      // 更新
      const updateData = {
        name: formData.value.name,
        appKey: formData.value.appKey,
        status: formData.value.status,
        remark: formData.value.remark
      }
      await applicationApi.update(formData.value.id, updateData)
      message.success('更新成功')
    }
    showForm.value = false
    loadData() // 重新加载数据
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
  currentItem.value = null
  showDelete.value = false
}

const confirmDelete = async () => {
  if (currentItem.value) {
    try {
      await applicationApi.delete(currentItem.value.id)
      message.success('删除成功')
      closeDelete()
      loadData() // 重新加载数据
    } catch (err) {
      if (import.meta.env.DEV) {
        console.error('删除失败:', err)
      }
      message.error(err.message || '删除失败')
    }
  }
}

// 授权用户相关
const openAssignUsers = async (item) => {
  currentItem.value = item
  showAssignUsers.value = true
  selectedUserIds.value = []
  
  // 加载所有登录用户
  try {
    const response = await loginUserApi.getList({ page: 1, pageSize: 1000 })
    if (response.code === 200 && response.data) {
      allLoginUsers.value = response.data.list || []
    }
    
    // 加载已授权的用户
    const authResponse = await loginUserAppApi.getAuthorizedUsers(item.id)
    if (authResponse.code === 200 && authResponse.data) {
      assignedUserIds.value = authResponse.data || []
      selectedUserIds.value = [...assignedUserIds.value]
    }
  } catch (err) {
    if (import.meta.env.DEV) {
      console.error('加载数据失败:', err)
    }
    message.error(err.message || '加载数据失败')
  }
}

const closeAssignUsers = () => {
  showAssignUsers.value = false
  currentItem.value = null
  selectedUserIds.value = []
  assignedUserIds.value = []
}

const toggleSelectUser = (userId) => {
  const index = selectedUserIds.value.indexOf(userId)
  if (index > -1) {
    selectedUserIds.value.splice(index, 1)
  } else {
    selectedUserIds.value.push(userId)
  }
}

const selectAllUsers = () => {
  if (selectedUserIds.value.length === allLoginUsers.value.length) {
    selectedUserIds.value = []
  } else {
    selectedUserIds.value = allLoginUsers.value.map(u => u.id)
  }
}

const confirmAssignUsers = async () => {
  if (!currentItem.value) return
  
  try {
    await loginUserAppApi.assignUsers(currentItem.value.id, selectedUserIds.value)
      message.success('授权成功')
    closeAssignUsers()
  } catch (err) {
    if (import.meta.env.DEV) {
      console.error('授权失败:', err)
    }
    message.error(err.message || '授权失败')
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

.action-row {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  width: 100%;
}

.action-buttons {
  display: flex;
  align-items: center;
  gap: 0;
}

/* 响应式布局 */
@media (max-width: 1200px) {
  .filters-row {
    flex-wrap: wrap;
  }
  
  .filter-actions {
    width: 100%;
    margin-top: 8px;
  }
}

@media (max-width: 768px) {
  .filters-row {
    flex-direction: column;
    align-items: stretch;
  }
  
  .filters-row > * {
    width: 100% !important;
  }
  
  .filter-actions {
    width: 100%;
    justify-content: flex-start;
  }
}
</style>
