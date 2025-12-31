<template>
  <div>
    <!-- 筛选栏 -->
    <div class="filter-bar">
      <div class="filters-row">
        <a-input
          v-model:value="filters.loginAccount"
          placeholder="请输入登录账户"
          style="width: 180px;"
          allow-clear
        >
          <template #prefix>
            <span class="filter-label">登录账户：</span>
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
      :data-source="loginUsers"
      :loading="loading"
      :pagination="false"
      :row-key="record => record.id"
      :scroll="{ x: 'max-content' }"
      bordered
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'adminType'">
          <a-tag :color="record.adminType === 2 ? 'purple' : 'blue'">
            {{ record.adminType === 2 ? '系统管理员' : '普通管理员' }}
          </a-tag>
        </template>
        <template v-if="column.key === 'status'">
          <a-tag :color="record.status === '正常' ? 'success' : 'error'">
            {{ record.status }}
          </a-tag>
        </template>
        <template v-if="column.key === 'action'">
          <div class="action-buttons">
            <a-button type="link" size="small" @click="openEdit(record)">编辑</a-button>
            <a-divider type="vertical" />
            <a-button type="link" danger size="small" @click="openDelete(record)">删除</a-button>
            <a-divider type="vertical" />
            <a-button type="link" size="small" @click="openAssignApps(record)">分配应用</a-button>
            <a-divider type="vertical" />
            <a-button type="link" size="small" @click="handleResetPassword(record)">
              <template #icon>
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect>
                  <path d="M7 11V7a5 5 0 0 1 10 0v4"></path>
                </svg>
              </template>
              重置密码
            </a-button>
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

    <!-- 分配应用抽屉 -->
    <a-drawer
      v-model:open="showAssignApps"
      title="分配应用"
      :width="480"
      placement="right"
      @close="closeAssignApps"
    >
      <div style="margin-bottom: 16px;">
        <a-checkbox
          :checked="selectedAppIds.length === allApplications.length && allApplications.length > 0"
          @change="selectAllApps"
        >
          全选（已选择 {{ selectedAppIds.length }} / {{ allApplications.length }}）
        </a-checkbox>
      </div>
      <div style="max-height: 500px; overflow-y: auto;">
        <div
          v-for="app in allApplications"
          :key="app.id"
          style="padding: 12px; border-bottom: 1px solid #f1f5f9; display: flex; align-items: center; cursor: pointer;"
          @click="toggleSelectApp(app.id)"
        >
          <a-checkbox
            :checked="selectedAppIds.includes(app.id)"
            @click.stop="toggleSelectApp(app.id)"
            style="margin-right: 12px;"
          />
          <div style="flex: 1;">
            <div style="font-weight: 500; color: #1e293b; display: flex; align-items: center; gap: 8px; margin-bottom: 4px;">
              <span>{{ app.name }}</span>
              <a-tag :color="app.status === '正常' ? 'success' : 'error'" size="small" style="margin: 0;">
                {{ app.status }}
              </a-tag>
            </div>
            <div style="font-size: 12px; color: #64748b;">
              {{ app.appKey }}
            </div>
          </div>
          <a-tag v-if="assignedAppIds.includes(app.id)" color="success">
            已授权
          </a-tag>
        </div>
        <a-empty v-if="allApplications.length === 0" description="暂无应用" />
      </div>
      <template #footer>
        <div style="text-align: right;">
          <a-button style="margin-right: 8px;" @click="closeAssignApps">取消</a-button>
          <a-button type="primary" @click="confirmAssignApps">确定</a-button>
        </div>
      </template>
    </a-drawer>

    <!-- 新增/编辑抽屉 -->
    <a-drawer
      v-model:open="showForm"
      :title="formMode === 'create' ? '新增管理员' : '编辑管理员'"
      :width="480"
      placement="right"
      @close="closeForm"
    >
      <a-form ref="formRef" :model="formData" layout="horizontal" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
        <a-form-item 
          label="登录账户" 
          name="loginAccount"
          :rules="[{ required: true, message: '请输入登录账户' }]"
        >
          <a-input
            v-model:value="formData.loginAccount"
            placeholder="请输入登录账户"
            :disabled="formMode === 'edit'"
          />
        </a-form-item>
        <a-form-item 
          label="姓名" 
          name="name"
          :rules="[{ required: true, message: '请输入姓名' }]"
        >
          <a-input v-model:value="formData.name" placeholder="请输入姓名" />
        </a-form-item>
        <a-form-item 
          label="邮箱" 
          name="email"
          :rules="[
            { required: true, message: '请输入邮箱' },
            { type: 'email', message: '请输入有效的邮箱地址' }
          ]"
        >
          <a-input v-model:value="formData.email" type="email" placeholder="请输入邮箱" />
        </a-form-item>
        <a-form-item 
          v-if="formMode === 'create'" 
          label="密码" 
          name="password"
          :rules="[{ required: true, message: '请输入密码' }]"
        >
          <a-input-password
            v-model:value="formData.password"
            placeholder="请输入密码（默认123456）"
          />
        </a-form-item>
        <a-form-item v-if="formMode === 'edit'" label="新密码">
          <a-input-password
            v-model:value="formData.password"
            placeholder="留空则不修改密码"
          />
        </a-form-item>
        <a-form-item label="管理员类型">
          <a-radio-group v-model:value="formData.adminType">
            <a-radio :value="1">普通管理员</a-radio>
            <a-radio :value="2">系统管理员</a-radio>
          </a-radio-group>
        </a-form-item>
        <a-form-item label="状态">
          <a-select v-model:value="formData.status">
            <a-select-option value="正常">正常</a-select-option>
            <a-select-option value="禁用">禁用</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="备注">
          <a-textarea
            v-model:value="formData.remark"
            placeholder="请输入备注信息"
            :rows="3"
          />
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
      title="删除管理员"
      @ok="confirmDelete"
      @cancel="closeDelete"
    >
      <p>确认删除管理员「{{ currentItem?.name }}」吗？该操作不可恢复。</p>
    </a-modal>
  </div>
</template>

<script setup>
import { computed, ref, watch, onMounted } from 'vue'
import { loginUserApi, applicationApi, loginUserAppApi } from '../../utils/api'
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
    title: '登录账户',
    dataIndex: 'loginAccount',
    key: 'loginAccount',
    width: 150,
    ellipsis: true
  },
  {
    title: '姓名',
    dataIndex: 'name',
    key: 'name',
    width: 120,
    ellipsis: true
  },
  {
    title: '管理员类型',
    dataIndex: 'adminType',
    key: 'adminType',
    width: 120
  },
  {
    title: '状态',
    dataIndex: 'status',
    key: 'status',
    width: 100
  },
  {
    title: '邮箱',
    dataIndex: 'email',
    key: 'email',
    width: 200,
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
    title: '操作',
    key: 'action',
    width: 320,
    fixed: 'right'
  }
]

// 数据
const loginUsers = ref([])
const totalRecords = ref(0)

const filters = ref({
  loginAccount: '',
  name: '',
  status: ''
})
const showForm = ref(false)
const showDelete = ref(false)
const showAssignApps = ref(false)
const formMode = ref('create')
const currentItem = ref(null)
const currentPage = ref(1)
const pageSize = ref(10)
const loading = ref(false)

// 分配应用相关
const allApplications = ref([])
const assignedAppIds = ref([])
const selectedAppIds = ref([])

const formData = ref({
  id: null,
  loginAccount: '',
  name: '',
  email: '',
  password: '',
  adminType: 1,
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
    if (filters.value.loginAccount && filters.value.loginAccount.trim()) {
      params.loginAccount = filters.value.loginAccount.trim()
    }
    if (filters.value.name && filters.value.name.trim()) {
      params.name = filters.value.name.trim()
    }
    if (filters.value.status && filters.value.status.trim()) {
      params.status = filters.value.status.trim()
    }
    
    const response = await loginUserApi.getList(params)
    
    if (response && response.code === 200) {
      if (response.data) {
        loginUsers.value = Array.isArray(response.data.list) ? response.data.list : []
        // 确保total是数字类型
        const total = response.data.total
        totalRecords.value = total !== null && total !== undefined ? Number(total) : 0
      } else {
        loginUsers.value = []
        totalRecords.value = 0
      }
    } else {
      loginUsers.value = []
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
  if (totalRecords.value === 0 || loginUsers.value.length === 0) return 0
  return (currentPage.value - 1) * pageSize.value + 1
})

const endIndex = computed(() => {
  if (totalRecords.value === 0 || loginUsers.value.length === 0) return 0
  const end = currentPage.value * pageSize.value
  return end > totalRecords.value ? totalRecords.value : end
})

watch([() => filters.value.loginAccount, () => filters.value.name, () => filters.value.status], () => {
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
  filters.value.loginAccount = ''
  filters.value.name = ''
  filters.value.status = ''
  currentPage.value = 1
  loadData()
}

const openCreate = () => {
  formMode.value = 'create'
  formData.value = {
    id: null,
    loginAccount: '',
    name: '',
    email: '',
    password: '',
    adminType: 1,
    status: '正常',
    remark: ''
  }
  showForm.value = true
}

const openEdit = (item) => {
  formMode.value = 'edit'
  formData.value = { ...item }
  formData.value.password = '' // 编辑时不显示密码
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
        loginAccount: formData.value.loginAccount,
        name: formData.value.name,
        email: formData.value.email,
        password: formData.value.password || 'onepermission', // 默认密码
        adminType: formData.value.adminType || 1,
        status: formData.value.status,
        remark: formData.value.remark
      }
      await loginUserApi.create(createData)
      message.success('创建成功')
    } else {
      // 更新
      const updateData = {
        loginAccount: formData.value.loginAccount,
        name: formData.value.name,
        email: formData.value.email,
        adminType: formData.value.adminType,
        status: formData.value.status,
        remark: formData.value.remark
      }
      // 如果提供了新密码，则更新密码
      if (formData.value.password) {
        updateData.password = formData.value.password
      }
      await loginUserApi.update(formData.value.id, updateData)
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
      await loginUserApi.delete(currentItem.value.id)
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

// 重置密码
const handleResetPassword = async (item) => {
  try {
    await loginUserApi.resetPassword(item.id)
    message.success(`密码重置成功，新密码为：onepermission`)
    loadData() // 重新加载数据
  } catch (err) {
    if (import.meta.env.DEV) {
      console.error('重置密码失败:', err)
    }
    message.error(err.message || '重置密码失败')
  }
}

// 分配应用相关
const openAssignApps = async (item) => {
  currentItem.value = item
  showAssignApps.value = true
  selectedAppIds.value = []
  
  // 加载所有应用（不进行权限过滤）
  try {
    const response = await applicationApi.getAllForAssign()
    if (response.code === 200 && response.data) {
      allApplications.value = response.data || []
    }
    
    // 加载已授权的应用
    const authResponse = await loginUserAppApi.getApplicationsByUserId(item.id)
    if (authResponse.code === 200 && authResponse.data) {
      assignedAppIds.value = (authResponse.data || []).map(app => app.id)
      selectedAppIds.value = [...assignedAppIds.value]
    }
  } catch (err) {
    if (import.meta.env.DEV) {
      console.error('加载数据失败:', err)
    }
    message.error(err.message || '加载数据失败')
  }
}

const closeAssignApps = () => {
  showAssignApps.value = false
  currentItem.value = null
  selectedAppIds.value = []
  assignedAppIds.value = []
}

const toggleSelectApp = (appId) => {
  const index = selectedAppIds.value.indexOf(appId)
  if (index > -1) {
    selectedAppIds.value.splice(index, 1)
  } else {
    selectedAppIds.value.push(appId)
  }
}

const selectAllApps = () => {
  if (selectedAppIds.value.length === allApplications.value.length) {
    selectedAppIds.value = []
  } else {
    selectedAppIds.value = allApplications.value.map(app => app.id)
  }
}

const confirmAssignApps = async () => {
  if (!currentItem.value) return
  
  try {
    await loginUserAppApi.assignApps(currentItem.value.id, selectedAppIds.value)
      message.success('分配成功')
    closeAssignApps()
  } catch (err) {
    if (import.meta.env.DEV) {
      console.error('分配失败:', err)
    }
    message.error(err.message || '分配失败')
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

/* 确保与tab之间无间距 */
:deep(.page-tabs) {
  margin-bottom: 0 !important;
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
    min-width: 220px;
  }
}

/* 表格自适应 */
:deep(.ant-table) {
  min-width: 1000px;
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
