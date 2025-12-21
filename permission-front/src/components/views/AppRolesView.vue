<template>
  <div>
    <!-- 筛选栏 -->
    <div class="filter-bar">
      <div class="filters-row">
        <div class="filter-item">
          <span class="filter-label">应用：</span>
          <a-select
            v-model:value="selectedAppId"
            placeholder="请选择应用"
            style="width: 180px;"
            allow-clear
          >
            <a-select-option v-for="app in applications" :key="app.id" :value="app.id">
              {{ app.name }}
            </a-select-option>
          </a-select>
        </div>
        <a-input
          v-model:value="systemIdFilter"
          placeholder="请输入系统ID"
          style="width: 180px;"
          :disabled="!selectedAppId"
          allow-clear
        >
          <template #prefix>
            <span class="filter-label">系统ID：</span>
          </template>
        </a-input>
        <a-input
          v-model:value="roleNameFilter"
          placeholder="请输入角色名称"
          style="width: 180px;"
          :disabled="!selectedAppId"
          allow-clear
        >
          <template #prefix>
            <span class="filter-label">角色名称：</span>
          </template>
        </a-input>
        <div class="filter-item">
          <span class="filter-label">状态：</span>
          <a-select
            v-model:value="statusFilter"
            placeholder="全部状态"
            style="width: 180px;"
            :disabled="!selectedAppId"
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
        <a-button type="primary" @click="openCreate" :disabled="!selectedAppId">＋ 新增</a-button>
      </div>
    </div>

    <!-- 空状态提示 -->
    <div v-if="!selectedAppId" style="background: #ffffff; border-radius: 10px; padding: 60px 20px;">
      <a-empty description="请先选择应用，然后查看该应用的角色列表" />
    </div>

    <!-- 表格 -->
    <a-table
      v-else
      :columns="columns"
      :data-source="roles"
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
            <a-button type="link" size="small" @click="openAssignUsers(record)">分配用户</a-button>
            <a-divider type="vertical" />
            <a-button type="link" danger size="small" @click="openDelete(record)">删除</a-button>
          </div>
        </template>
      </template>
    </a-table>

    <!-- 分页 -->
    <div v-if="selectedAppId" style="margin-top: 16px; display: flex; justify-content: space-between; align-items: center;">
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
        @change="loadRoles"
        @show-size-change="loadRoles"
      />
    </div>

    <!-- 新增/编辑抽屉 -->
    <a-drawer
      v-model:open="showForm"
      :title="formMode === 'create' ? '新增角色' : '修改角色'"
      :width="480"
      placement="right"
      @close="closeForm"
    >
      <a-form ref="formRef" :model="formData" layout="horizontal" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
        <a-form-item label="系统ID" :required="true">
          <a-input
            :value="formMode === 'create' ? selectedAppId : formData.systemId"
            disabled
            class="disabled-input"
          />
        </a-form-item>
        <a-form-item label="所属应用" :required="true">
          <a-input
            :value="selectedApp?.name || ''"
            disabled
            class="disabled-input"
          />
        </a-form-item>
        <a-form-item 
          label="角色名称" 
          name="name"
          :rules="[{ required: true, message: '请输入角色名称' }]"
        >
          <a-input v-model:value="formData.name" placeholder="请输入角色名称" />
        </a-form-item>
        <a-form-item label="显示顺序">
          <a-input-number
            v-model:value="formData.sort"
            :min="0"
            placeholder="请输入显示顺序"
            style="width: 100%;"
          />
        </a-form-item>
        <a-form-item label="状态">
          <a-select v-model:value="formData.status">
            <a-select-option value="正常">正常</a-select-option>
            <a-select-option value="禁用">禁用</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="绑定菜单">
          <div v-if="menuTreeData.length === 0" style="padding: 20px; text-align: center; color: #999;">
            <a-empty description="暂无菜单数据，请先在该应用下创建菜单" :image="false" />
          </div>
          <a-tree
            v-else
            v-model:checkedKeys="formData.menuIds"
            checkable
            :tree-data="menuTreeData"
            :field-names="{ children: 'children', title: 'name', key: 'id' }"
            :check-strictly="false"
            style="max-height: 400px; overflow-y: auto; border: 1px solid #d9d9d9; border-radius: 4px; padding: 8px;"
            @check="handleMenuCheck"
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
      title="删除角色"
      @ok="confirmDelete"
      @cancel="closeDelete"
    >
      <p>确认删除角色「{{ currentItem?.name }}」吗？该操作不可恢复。</p>
    </a-modal>
  </div>
</template>

<script setup>
import { computed, ref, watch, onMounted, nextTick } from 'vue'
import { applicationApi, appRoleApi, appMenuApi } from '@/utils/api'
import { message } from 'ant-design-vue'

const formRef = ref(null)

const emit = defineEmits(['open-role-users'])

// 表格列定义
const columns = [
  {
    title: '系统ID',
    dataIndex: 'systemId',
    key: 'systemId',
    width: 100,
    ellipsis: true
  },
  {
    title: '所属应用',
    dataIndex: 'appName',
    key: 'appName',
    width: 150,
    ellipsis: true
  },
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
    width: 250,
    fixed: 'right'
  }
]

// 计算分页显示范围
const startIndex = computed(() => {
  if (totalRecords.value === 0) return 0
  return (currentPage.value - 1) * pageSize.value + 1
})

const endIndex = computed(() => {
  const end = currentPage.value * pageSize.value
  return end > totalRecords.value ? totalRecords.value : end
})

// 应用列表
const applications = ref([])
const selectedAppId = ref(null)
const selectedApp = computed(() => {
  return applications.value.find(app => app.id === selectedAppId.value)
})

// 角色列表
const roles = ref([])
const loading = ref(false)

const systemIdFilter = ref('')
const roleNameFilter = ref('')
const statusFilter = ref('')
const showForm = ref(false)
const showDelete = ref(false)
const formMode = ref('create')
const currentItem = ref(null)
const currentPage = ref(1)
const pageSize = ref(10)

const formData = ref({
  id: null,
  systemId: '',
  appName: '',
  name: '',
  sort: 0,
  status: '正常',
  menuIds: []
})

// 菜单树数据
const menuTreeData = ref([])

const filteredData = computed(() => {
  if (!selectedAppId.value) {
    return []
  }
  
  return roles.value.filter((item) => {
    const matchSystemId = systemIdFilter.value ? item.systemId?.includes(systemIdFilter.value) : true
    const matchRoleName = roleNameFilter.value ? item.name?.includes(roleNameFilter.value) : true
    const matchStatus = statusFilter.value ? item.status === statusFilter.value : true
    return matchSystemId && matchRoleName && matchStatus
  })
})

const totalRecords = ref(0)

watch([systemIdFilter, roleNameFilter, statusFilter], () => {
  currentPage.value = 1
})

watch(selectedAppId, (newVal) => {
  if (newVal) {
    loadRoles()
  } else {
    roles.value = []
  }
  currentPage.value = 1
})

// 加载应用列表
const loadApplications = async () => {
  try {
    const response = await applicationApi.getAll()
    if (response.code === 200 && response.data) {
      applications.value = response.data
    }
  } catch (err) {
    if (import.meta.env.DEV) {
      console.error('加载应用列表失败:', err)
    }
    message.error('加载应用列表失败')
  }
}

// 加载角色列表
const loadRoles = async () => {
  if (!selectedAppId.value) {
    roles.value = []
    return
  }
  
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value,
      ...Object.fromEntries(
        Object.entries({
          systemId: systemIdFilter.value,
          name: roleNameFilter.value,
          status: statusFilter.value
        }).filter(([, value]) => value !== undefined && value !== null && value !== '')
      )
    }
    
    const response = await appRoleApi.getList(selectedAppId.value, params)
    if (response.code === 200 && response.data) {
      roles.value = response.data.list || []
      totalRecords.value = response.data.total || 0
    } else {
      message.error(response.message || '加载角色列表失败')
      roles.value = []
    }
  } catch (err) {
    // 如果是登录过期错误，不显示错误提示（api.js 已经处理了）
    if (err.message && err.message.includes('登录已过期')) {
      return
    }
    if (import.meta.env.DEV) {
      console.error('加载角色列表失败:', err)
    }
    message.error('加载角色列表失败: ' + (err.message || '未知错误'))
    roles.value = []
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  loadRoles()
}

const resetFilter = () => {
  systemIdFilter.value = ''
  roleNameFilter.value = ''
  statusFilter.value = ''
  currentPage.value = 1
  loadRoles()
}

const goToPage = (page) => {
  if (page === '...' || page < 1 || page > totalPages.value) return
  currentPage.value = page
  loadRoles()
}

// 加载菜单树数据
const loadMenuTree = async () => {
  if (!selectedAppId.value) {
    menuTreeData.value = []
    return
  }
  
  try {
    if (import.meta.env.DEV) {
      console.log('开始加载菜单树，应用ID:', selectedAppId.value)
    }
    
    const response = await appMenuApi.getMenuTree(selectedAppId.value)
    
    if (import.meta.env.DEV) {
      console.log('菜单树API响应:', response)
      console.log('响应数据:', response.data)
    }
    
    if (response && response.code === 200) {
      if (response.data && Array.isArray(response.data) && response.data.length > 0) {
        // 转换树形数据格式
        const transformTree = (menus, parentId = null) => {
          if (!Array.isArray(menus)) {
            return []
          }
          return menus.map(menu => {
            const node = {
              id: menu.id,
              name: menu.name || menu.title || `菜单${menu.id}`,
              key: menu.id, // a-tree 需要 key 字段
              title: menu.name || menu.title || `菜单${menu.id}`, // 兼容性
              parentId: menu.parentId || parentId // 保留父菜单ID信息
            }
            if (menu.children && Array.isArray(menu.children) && menu.children.length > 0) {
              node.children = transformTree(menu.children, menu.id)
            }
            return node
          })
        }
        menuTreeData.value = transformTree(response.data)
        
        if (import.meta.env.DEV) {
          console.log('转换后的菜单树数据:', menuTreeData.value)
          console.log('菜单树数据数量:', menuTreeData.value.length)
        }
      } else {
        if (import.meta.env.DEV) {
          console.warn('菜单树数据为空，应用ID:', selectedAppId.value)
        }
        menuTreeData.value = []
        message.warning('该应用下暂无菜单数据，请先在应用菜单管理中创建菜单')
      }
    } else {
      if (import.meta.env.DEV) {
        console.warn('菜单树API返回错误:', response)
      }
      menuTreeData.value = []
      const errorMsg = response?.message || '加载菜单树失败'
      message.error(errorMsg)
    }
  } catch (err) {
    if (import.meta.env.DEV) {
      console.error('加载菜单树异常:', err)
      console.error('错误详情:', {
        appId: selectedAppId.value,
        error: err,
        message: err.message,
        stack: err.stack
      })
    }
    menuTreeData.value = []
    message.error('加载菜单树失败: ' + (err.message || '未知错误'))
  }
}

// 处理菜单选择变化
const handleMenuCheck = (checkedKeys, info) => {
  // 当 check-strictly=false 时，checkedKeys 是一个对象，包含 checked 和 halfChecked
  // checked: 所有选中的节点（包括父节点，如果所有子节点都被选中）
  // halfChecked: 半选中的节点（部分子节点被选中）
  if (checkedKeys && typeof checkedKeys === 'object' && !Array.isArray(checkedKeys)) {
    // 级联选择模式，保持格式一致：{ checked: [], halfChecked: [] }
    formData.value.menuIds = {
      checked: checkedKeys.checked || [],
      halfChecked: checkedKeys.halfChecked || []
    }
  } else if (Array.isArray(checkedKeys)) {
    // 兼容非级联模式，转换为标准格式
    formData.value.menuIds = {
      checked: checkedKeys,
      halfChecked: []
    }
  } else {
    formData.value.menuIds = {
      checked: [],
      halfChecked: []
    }
  }
  
  if (import.meta.env.DEV) {
    console.log('菜单选择变化:', {
      checkedKeys,
      checked: checkedKeys?.checked,
      halfChecked: checkedKeys?.halfChecked,
      formDataMenuIds: formData.value.menuIds,
      info
    })
  }
}

const openCreate = async () => {
  if (!selectedAppId.value) {
    message.error('请先选择应用')
    return
  }
  
  // 加载菜单树
  await loadMenuTree()
  
  formMode.value = 'create'
  formData.value = {
    id: null,
    systemId: selectedAppId.value.toString(), // 自动使用所选应用的ID
    appName: selectedApp.value?.name || '',
    name: '',
    sort: 0,
    status: '正常',
    menuIds: {
      checked: [],
      halfChecked: []
    }
  }
  showForm.value = true
}

const openEdit = async (item) => {
  if (!selectedAppId.value) {
    message.error('请先选择应用')
    return
  }
  
  try {
    // 加载菜单树
    await loadMenuTree()
    
    // 加载角色详情
    const response = await appRoleApi.getById(selectedAppId.value, item.id)
    if (response.code === 200 && response.data) {
      // 加载角色的菜单权限
      let menuIds = []
      try {
        const menuResponse = await appRoleApi.getRoleMenus(selectedAppId.value, item.id)
        if (menuResponse.code === 200 && menuResponse.data) {
          menuIds = menuResponse.data || []
        }
      } catch (err) {
        if (import.meta.env.DEV) {
          console.error('加载角色菜单权限失败:', err)
        }
      }
      
      formMode.value = 'edit'
      
      // 编辑时，Ant Design Vue 的树组件会自动处理半选状态
      // 我们只需要提供实际选中的菜单ID数组，树组件会自动计算父菜单的状态
      // 关键：只传递实际选中的叶子节点，让树组件自动处理父菜单的选中和半选状态
      let checkedMenuIds = menuIds || []
      
      if (checkedMenuIds.length > 0 && menuTreeData.value.length > 0) {
        // 构建菜单映射表（包含所有菜单及其父子关系）
        const menuMap = new Map()
        const buildMenuMap = (menus) => {
          menus.forEach(menu => {
            menuMap.set(menu.id, menu)
            if (menu.children && menu.children.length > 0) {
              buildMenuMap(menu.children)
            }
          })
        }
        buildMenuMap(menuTreeData.value)
        
        // 找出所有叶子节点（没有子菜单的菜单）
        const leafMenuIds = new Set()
        menuMap.forEach((menu, menuId) => {
          if (!menu.children || menu.children.length === 0) {
            leafMenuIds.add(menuId)
          }
        })
        
        // 只保留实际选中的叶子节点，过滤掉父菜单
        // 这是关键：只保留实际选中的叶子节点，让树组件自动处理父菜单状态
        const actualSelectedLeafMenus = checkedMenuIds.filter(id => leafMenuIds.has(id))
        
        // 将实际选中的叶子菜单ID数组直接传递给树组件
        // 树组件会自动计算父菜单的选中和半选状态
        checkedMenuIds = actualSelectedLeafMenus
        
        if (import.meta.env.DEV) {
          console.log('编辑时菜单状态（使用Ant Design自带逻辑）:', {
            原始菜单ID: menuIds,
            叶子节点总数: leafMenuIds.size,
            实际选中的叶子节点: actualSelectedLeafMenus,
            说明: '树组件会自动计算父菜单的选中和半选状态'
          })
        }
      }
      
      // 设置树组件的选中状态
      // 直接传递数组，Ant Design Vue 会自动转换为 { checked: [], halfChecked: [] } 格式
      // 并自动计算父菜单的半选状态
      formData.value = {
        id: response.data.id,
        systemId: response.data.systemId || '',
        appName: selectedApp.value?.name || '',
        name: response.data.name || '',
        sort: response.data.sort || 0,
        status: response.data.status || '正常',
        menuIds: checkedMenuIds // 直接传递数组，让树组件自动处理
      }
      
      // 使用 nextTick 确保树组件正确渲染和计算状态
      await nextTick()
      
      if (import.meta.env.DEV) {
        console.log('编辑时菜单状态设置完成:', {
          设置的菜单ID: checkedMenuIds,
          formDataMenuIds: formData.value.menuIds
        })
      }
      
      showForm.value = true
    } else {
      message.error(response.message || '加载角色详情失败')
    }
  } catch (err) {
    if (import.meta.env.DEV) {
      console.error('加载角色详情失败:', err)
    }
    message.error('加载角色详情失败')
  }
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

  if (!selectedAppId.value) {
    message.error('请先选择应用')
    return
  }
  
  // 处理菜单ID（确保级联选择的父菜单也被包含）
  let menuIds = []
  if (formData.value.menuIds) {
    if (Array.isArray(formData.value.menuIds)) {
      menuIds = formData.value.menuIds
    } else if (formData.value.menuIds.checked && Array.isArray(formData.value.menuIds.checked)) {
      // 级联选择模式，checked 数组可能包含父菜单（用于UI显示）
      // 我们需要过滤掉只用于UI显示的父菜单，只保留实际选中的菜单
      menuIds = formData.value.menuIds.checked
    }
  }
  
  // 确保 menuIds 中的值都是数字类型
  menuIds = menuIds.map(id => typeof id === 'string' ? parseInt(id) : id).filter(id => !isNaN(id))
  
  // 构建完整的菜单映射表（包含所有菜单及其父子关系）
  const menuMap = new Map()
  const buildMenuMap = (menus) => {
    menus.forEach(menu => {
      menuMap.set(menu.id, menu)
      if (menu.children && menu.children.length > 0) {
        buildMenuMap(menu.children)
      }
    })
  }
  
  // 如果选择了子菜单，确保所有父菜单也被包含
  if (menuIds.length > 0 && menuTreeData.value.length > 0) {
    // 构建菜单映射表
    buildMenuMap(menuTreeData.value)
    
    // 找出所有叶子节点（没有子菜单的菜单）
    const leafMenuIds = new Set()
    menuMap.forEach((menu, menuId) => {
      if (!menu.children || menu.children.length === 0) {
        leafMenuIds.add(menuId)
      }
    })
    
    // 第一步：只保留实际选中的叶子节点
    const actualSelectedLeafMenus = menuIds.filter(id => leafMenuIds.has(id))
    
    // 第二步：查找所有选中叶子菜单的父菜单ID（递归查找所有层级）
    const allParentMenuIds = new Set()
    
    // 递归查找某个菜单的所有父菜单ID
    const findAllParents = (menuId) => {
      const menu = menuMap.get(menuId)
      if (menu && menu.parentId) {
        // 添加父菜单ID
        allParentMenuIds.add(menu.parentId)
        // 继续查找父菜单的父菜单（递归）
        findAllParents(menu.parentId)
      }
    }
    
    // 对每个实际选中的叶子菜单，查找其所有父菜单
    actualSelectedLeafMenus.forEach(menuId => {
      findAllParents(menuId)
    })
    
    // 第三步：合并实际选中的叶子菜单和所有父菜单
    const finalMenuIds = new Set()
    actualSelectedLeafMenus.forEach(id => finalMenuIds.add(id))
    allParentMenuIds.forEach(id => finalMenuIds.add(id))
    
    menuIds = Array.from(finalMenuIds)
    
    // 确保菜单ID列表按层级排序（父菜单在前）
    const sortMenuIds = (ids) => {
      const sorted = []
      const added = new Set()
      
      // 先添加所有没有父菜单的菜单（根菜单）
      ids.forEach(id => {
        const menu = menuMap.get(id)
        if (!menu || !menu.parentId || !ids.includes(menu.parentId)) {
          if (!added.has(id)) {
            sorted.push(id)
            added.add(id)
          }
        }
      })
      
      // 然后按层级添加其他菜单
      const addMenuWithParents = (menuId) => {
        if (added.has(menuId)) return
        
        const menu = menuMap.get(menuId)
        if (menu && menu.parentId && ids.includes(menu.parentId)) {
          // 先添加父菜单
          addMenuWithParents(menu.parentId)
        }
        
        if (!added.has(menuId)) {
          sorted.push(menuId)
          added.add(menuId)
        }
      }
      
      ids.forEach(id => {
        if (!added.has(id)) {
          addMenuWithParents(id)
        }
      })
      
      return sorted
    }
    
    menuIds = sortMenuIds(menuIds)
  }
  
  if (import.meta.env.DEV) {
    console.log('提交的菜单ID列表（包含所有父菜单）:', menuIds)
    console.log('菜单ID数量:', menuIds.length)
  }

  try {
    const data = {
      systemId: formMode.value === 'create' ? selectedAppId.value.toString() : formData.value.systemId,
      name: formData.value.name,
      sort: formData.value.sort || 0,
      status: formData.value.status || '正常',
      menuIds: menuIds
    }

    if (formMode.value === 'create') {
      const response = await appRoleApi.create(selectedAppId.value, data)
      if (response.code === 200) {
        message.success('创建成功')
        await loadRoles()
        showForm.value = false
      } else {
        message.error(response.message || '创建失败')
      }
    } else {
      const response = await appRoleApi.update(selectedAppId.value, formData.value.id, data)
      if (response.code === 200) {
        message.success('更新成功')
        await loadRoles()
        showForm.value = false
      } else {
        message.error(response.message || '更新失败')
      }
    }
  } catch (err) {
    if (import.meta.env.DEV) {
      console.error('保存失败:', err)
    }
    message.error(err.message || '保存失败')
  }
}

const openAssignUsers = (item) => {
  if (!selectedAppId.value) {
    message.error('请先选择应用')
    return
  }
  emit('open-role-users', {
    appId: selectedAppId.value,
    roleId: item.id,
    roleName: item.name,
    appName: selectedApp.value?.name || ''
  })
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
  if (!currentItem.value || !selectedAppId.value) {
    return
  }

  try {
    const response = await appRoleApi.delete(selectedAppId.value, currentItem.value.id)
    if (response.code === 200) {
      message.success('删除成功')
      await loadRoles()
      closeDelete()
    } else {
      message.error(response.message || '删除失败')
    }
  } catch (err) {
    if (import.meta.env.DEV) {
      console.error('删除失败:', err)
    }
    message.error(err.message || '删除失败')
  }
}

// 初始化
onMounted(() => {
  loadApplications()
})
</script>

<style scoped>
/* 查询条件样式 */
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
    min-width: 250px;
  }
}

/* 表格自适应 */
:deep(.ant-table) {
  min-width: 1000px;
}

:deep(.ant-table-container) {
  overflow-x: auto;
}

/* 抽屉样式 */
.drawer-mask {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.45);
  z-index: 1000;
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.drawer {
  position: fixed;
  top: 0;
  right: 0;
  width: 480px;
  max-width: 90vw;
  height: 100vh;
  background: #ffffff;
  box-shadow: -2px 0 8px rgba(0, 0, 0, 0.15);
  display: flex;
  flex-direction: column;
  animation: slideInRight 0.3s ease;
  z-index: 1001;
}

@keyframes slideInRight {
  from {
    transform: translateX(100%);
  }
  to {
    transform: translateX(0);
  }
}

.drawer-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  border-bottom: 1px solid #f1f5f9;
}

.drawer-title {
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
}

.drawer-close {
  background: none;
  border: none;
  color: #64748b;
  cursor: pointer;
  padding: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  border-radius: 4px;
  transition: all 0.2s;
}

.drawer-close:hover {
  background: #f1f5f9;
  color: #1e293b;
}

.drawer-close svg {
  width: 18px;
  height: 18px;
}

.drawer-body {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.drawer-body .form-row {
  display: flex !important;
  flex-direction: row !important;
  align-items: center !important;
  gap: 12px;
  flex-wrap: nowrap !important;
}

.drawer-body .form-row label {
  min-width: 90px;
  width: 90px;
  font-size: 13px;
  color: #64748b;
  font-weight: 500;
  white-space: nowrap;
  text-align: right;
  flex-shrink: 0;
  margin: 0;
  padding: 0;
}

.drawer-body .form-row input,
.drawer-body .form-row select {
  flex: 1;
  height: 40px;
  margin: 0;
}

.drawer-body .form-row textarea {
  flex: 1;
  min-height: 80px;
  resize: vertical;
  margin: 0;
}

.drawer-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 16px 20px;
  border-top: 1px solid #f1f5f9;
}

/* 响应式布局 */
@media (max-width: 600px) {
  .drawer {
    width: 100vw;
  }
  
  .drawer-body .form-row {
    flex-direction: column !important;
    align-items: flex-start !important;
  }
  
  .drawer-body .form-row label {
    width: 100%;
    text-align: left;
  }
  
  .drawer-body .form-row input,
  .drawer-body .form-row select,
  .drawer-body .form-row textarea {
    width: 100%;
  }
}
</style>

