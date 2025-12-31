<template>
  <div>
    <!-- 筛选栏 -->
    <div class="filter-bar">
      <div class="filters-row">
        <div style="display: flex; align-items: center; gap: 8px;">
          <span class="filter-label-inline">应用：</span>
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
          v-model:value="keyword"
          placeholder="请输入菜单名称"
          style="width: 180px;"
          :disabled="!selectedAppId"
          allow-clear
        >
          <template #prefix>
            <span class="filter-label">菜单名称：</span>
          </template>
        </a-input>
        <a-input
          v-model:value="menuKeyFilter"
          placeholder="请输入菜单Key"
          style="width: 180px;"
          :disabled="!selectedAppId"
          allow-clear
        >
          <template #prefix>
            <span class="filter-label">菜单Key：</span>
          </template>
        </a-input>
        <a-select
          v-model:value="statusFilter"
          placeholder="全部状态"
          style="width: 180px;"
          :disabled="!selectedAppId"
          allow-clear
        >
          <a-select-option value="">全部状态</a-select-option>
          <a-select-option value="正常">正常</a-select-option>
          <a-select-option value="停用">停用</a-select-option>
        </a-select>
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
    <div v-if="!selectedApp" style="background: #ffffff; border-radius: 10px; padding: 60px 20px;">
      <a-empty description="请先选择应用，然后查看该应用的菜单列表" />
    </div>

    <!-- 表格菜单列表 -->
    <a-table
      v-else
      :columns="columns"
      :data-source="treeData"
      :loading="menuLoading"
      :pagination="false"
      :row-key="record => record.id"
      :scroll="{ x: 'max-content' }"
      :default-expand-all-rows="false"
      :expanded-row-keys="expandedIds"
      :indent-size="20"
      @expand="handleExpand"
      bordered
      size="middle"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'menuType'">
          <a-tag 
            :color="record.menuType === '目录' ? 'blue' : record.menuType === '菜单' ? 'green' : 'orange'"
            size="small"
          >
            {{ record.menuType }}
          </a-tag>
        </template>
        <template v-if="column.key === 'permission'">
          <span>{{ record.permission || '-' }}</span>
        </template>
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
            <a-button type="link" size="small" @click="openEdit(record)">修改</a-button>
            <a-divider type="vertical" />
            <a-button type="link" size="small" @click="openCreateChild(record)">新增</a-button>
            <a-divider type="vertical" />
            <a-button type="link" size="small" @click="openDetail(record)">查看</a-button>
            <a-divider type="vertical" />
            <a-button type="link" danger size="small" @click="openDelete(record)">删除</a-button>
          </div>
        </template>
      </template>
      <template #expandIcon="{ expanded, record }">
        <span 
          v-if="record.children && record.children.length > 0" 
          class="expand-icon" 
          @click.stop="toggleExpand(record)"
        >
          <svg 
            viewBox="0 0 1024 1024" 
            width="12" 
            height="12" 
            style="color: #000; transition: transform 0.2s;"
            :style="{ transform: expanded ? 'rotate(0deg)' : 'rotate(-90deg)' }"
          >
            <path d="M840.4 300H183.6c-19.7 0-30.7 20.8-18.5 35l328.4 380.8c9.4 10.9 27.5 10.9 37 0L858.9 335c12.2-14.2 1.2-35-18.5-35z" fill="currentColor"></path>
          </svg>
        </span>
        <span v-else class="expand-icon-placeholder"></span>
      </template>
    </a-table>

    <!-- 新增/编辑抽屉 -->
    <a-drawer
      v-model:open="showForm"
      :title="formMode === 'create' ? '新增菜单' : '修改菜单'"
      :width="700"
      placement="right"
      @close="closeForm"
    >
      <a-form ref="formRef" :model="formData" layout="horizontal" :label-col="{ span: 4 }" :wrapper-col="{ span: 20 }">
        <!-- 上级菜单：单独一行 -->
        <a-form-item label="上级菜单">
          <a-tree-select
            v-model:value="formData.parentId"
            :tree-data="treeSelectData"
            :field-names="{ children: 'children', label: 'name', value: 'id' }"
            placeholder="请选择上级菜单（不选则为顶级菜单）"
            allow-clear
            :show-search="true"
            :filter-tree-node="filterTreeNode"
            style="width: 100%"
            :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
            :tree-line="{ showLeafIcon: false }"
            :virtual="true"
            :list-height="300"
          />
        </a-form-item>

        <!-- 菜单类型：单独一行 -->
        <a-form-item label="菜单类型">
          <a-radio-group v-model:value="formData.menuType">
            <a-radio value="目录">目录</a-radio>
            <a-radio value="菜单">菜单</a-radio>
            <a-radio value="按钮">按钮</a-radio>
          </a-radio-group>
        </a-form-item>

        <!-- 其他字段：两列布局 -->
        <div class="form-two-columns">
          <!-- 按钮类型时显示：菜单名称、显示顺序、权限标识 -->
          <template v-if="formData.menuType === '按钮'">

            <a-form-item 
              label="菜单名称" 
              name="name"
              :rules="[{ required: true, message: '请输入菜单名称' }]"
            >
              <a-input v-model:value="formData.name" placeholder="请输入菜单名称" />
            </a-form-item>

            <a-form-item 
              label="显示排序" 
              name="sort"
              :rules="[{ required: true, message: '请输入排序号' }]"
            >
              <a-input-number
                v-model:value="formData.sort"
                :min="0"
                placeholder="请输入排序号"
                style="width: 100%;"
              />
            </a-form-item>

            <a-form-item label="权限标识">
              <a-input v-model:value="formData.permission" placeholder="请输入权限标识" />
            </a-form-item>
          </template>

          <!-- 目录和菜单类型时显示完整表单 -->
          <template v-else>
            <a-form-item label="菜单图标">
              <a-input v-model:value="formData.icon" placeholder="请输入图标名称" />
            </a-form-item>

            <a-form-item 
              label="菜单名称" 
              name="name"
              :rules="[{ required: true, message: '请输入菜单名称' }]"
            >
              <a-input v-model:value="formData.name" placeholder="请输入菜单名称" />
            </a-form-item>

            <a-form-item 
              label="显示排序" 
              name="sort"
              :rules="[{ required: true, message: '请输入排序号' }]"
            >
              <a-input-number
                v-model:value="formData.sort"
                :min="0"
                placeholder="请输入排序号"
                style="width: 100%;"
              />
            </a-form-item>

            <a-form-item label="url">
              <a-input v-model:value="formData.embeddedUrl" placeholder="请输入url" />
            </a-form-item>

            <a-form-item label="是否外链">
              <a-radio-group v-model:value="formData.isExternal">
                <a-radio :value="true">是</a-radio>
                <a-radio :value="false">否</a-radio>
              </a-radio-group>
            </a-form-item>

            <a-form-item label="路由地址">
              <a-input v-model:value="formData.route" placeholder="请输入路由地址" />
            </a-form-item>

            <a-form-item v-if="formData.menuType === '菜单'" label="组件路径">
              <a-input v-model:value="formData.component" placeholder="请输入组件路径" />
            </a-form-item>

            <a-form-item v-if="formData.menuType === '菜单'" label="权限标识">
              <a-input v-model:value="formData.permission" placeholder="请输入权限标识" />
            </a-form-item>

            <a-form-item label="显示状态">
              <a-radio-group v-model:value="formData.displayStatus">
                <a-radio value="显示">显示</a-radio>
                <a-radio value="隐藏">隐藏</a-radio>
              </a-radio-group>
            </a-form-item>

            <a-form-item label="菜单状态">
              <a-radio-group v-model:value="formData.status">
                <a-radio value="正常">正常</a-radio>
                <a-radio value="停用">停用</a-radio>
              </a-radio-group>
            </a-form-item>
          </template>
        </div>

        <!-- 备注：单独一行 -->
        <a-form-item label="备注">
          <a-textarea
            v-model:value="formData.remark"
            placeholder="请输入备注信息"
            :rows="4"
            :maxlength="255"
            show-count
          />
        </a-form-item>

        <!-- 预留字段：两列布局 -->
        <div class="form-two-columns">
          <a-form-item label="预留字段1">
            <a-input v-model:value="formData.extField1" placeholder="预留字段1（用户特殊需求）" :maxlength="100" show-count />
          </a-form-item>

          <a-form-item label="预留字段2">
            <a-input v-model:value="formData.extField2" placeholder="预留字段2（用户特殊需求）" :maxlength="100" show-count />
          </a-form-item>

          <a-form-item label="预留字段3">
            <a-input v-model:value="formData.extField3" placeholder="预留字段3（用户特殊需求）" :maxlength="100" show-count />
          </a-form-item>

          <a-form-item label="预留字段4">
            <a-input v-model:value="formData.extField4" placeholder="预留字段4（用户特殊需求）" :maxlength="100" show-count />
          </a-form-item>

          <a-form-item label="预留字段5">
            <a-input v-model:value="formData.extField5" placeholder="预留字段5（用户特殊需求）" :maxlength="100" show-count />
          </a-form-item>

          <a-form-item label="预留字段6">
            <a-input v-model:value="formData.extField6" placeholder="预留字段6（用户特殊需求）" :maxlength="100" show-count />
          </a-form-item>
        </div>
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

    <!-- 详情抽屉 -->
    <a-drawer
      v-model:open="showDetail"
      title="菜单详情"
      :width="600"
      placement="right"
      @close="closeDetail"
    >
      <a-descriptions :column="1" bordered>
        <a-descriptions-item label="菜单名称">
          {{ currentItem?.name }}
        </a-descriptions-item>
        <a-descriptions-item label="菜单类型">
          <a-tag 
            :color="currentItem?.menuType === '目录' ? 'blue' : currentItem?.menuType === '菜单' ? 'green' : 'orange'"
            size="small"
          >
            {{ currentItem?.menuType || '目录' }}
          </a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="菜单Key">
          {{ currentItem?.menuKey }}
        </a-descriptions-item>
        <a-descriptions-item label="父菜单">
          {{ currentItem?.parentName || '无' }}
        </a-descriptions-item>
        <a-descriptions-item label="菜单图标">
          {{ currentItem?.icon || '-' }}
        </a-descriptions-item>
        <a-descriptions-item label="显示排序">
          {{ currentItem?.sort }}
        </a-descriptions-item>
        <a-descriptions-item v-if="currentItem?.menuType !== '按钮'" label="是否外链">
          {{ currentItem?.isExternal ? '是' : '否' }}
        </a-descriptions-item>
        <a-descriptions-item v-if="currentItem?.menuType !== '按钮'" label="路由地址">
          {{ currentItem?.route || '-' }}
        </a-descriptions-item>
        <a-descriptions-item v-if="currentItem?.menuType === '菜单'" label="组件路径">
          {{ currentItem?.component || '-' }}
        </a-descriptions-item>
        <a-descriptions-item v-if="currentItem?.menuType === '按钮' || currentItem?.menuType === '菜单'" label="权限标识">
          {{ currentItem?.permission || '-' }}
        </a-descriptions-item>
        <a-descriptions-item label="显示状态">
          {{ currentItem?.displayStatus || '显示' }}
        </a-descriptions-item>
        <a-descriptions-item label="菜单状态">
          <a-tag :color="currentItem?.status === '正常' ? 'success' : 'error'">
            {{ currentItem?.status }}
          </a-tag>
        </a-descriptions-item>
        <a-descriptions-item v-if="currentItem?.menuType !== '按钮'" label="url">
          {{ currentItem?.embeddedUrl || '-' }}
        </a-descriptions-item>
        <a-descriptions-item label="备注">
          {{ currentItem?.remark || '-' }}
        </a-descriptions-item>
        <a-descriptions-item label="创建时间">
          {{ currentItem?.createdAt }}
        </a-descriptions-item>
      </a-descriptions>
      <template #footer>
        <div style="text-align: right;">
          <a-button @click="closeDetail">我知道了</a-button>
        </div>
      </template>
    </a-drawer>

    <!-- 删除确认 -->
    <a-modal
      v-model:open="showDelete"
      title="删除菜单"
      @ok="confirmDelete"
      @cancel="closeDelete"
    >
      <p>
        确认删除菜单「{{ currentItem?.name }}」吗？
        <span v-if="currentItem?.children?.length > 0" style="color: #ff4d4f;">
          该菜单下有子菜单，删除后子菜单也会被删除。
        </span>
        该操作不可恢复。
      </p>
    </a-modal>
  </div>
</template>

<script setup>
import { computed, ref, watch, onMounted } from 'vue'
import { applicationApi, appMenuApi } from '../../utils/api'
import { message } from 'ant-design-vue'

const formRef = ref(null)

// 表格列定义
const columns = [
  {
    title: '菜单名称',
    dataIndex: 'name',
    key: 'name',
    width: 200,
    ellipsis: true
  },
  {
    title: '菜单Key',
    dataIndex: 'menuKey',
    key: 'menuKey',
    width: 120,
    ellipsis: true
  },
  {
    title: '菜单类型',
    dataIndex: 'menuType',
    key: 'menuType',
    width: 100
  },
  {
    title: '排序',
    dataIndex: 'sort',
    key: 'sort',
    width: 80,
    align: 'center'
  },
  {
    title: '路由',
    dataIndex: 'route',
    key: 'route',
    width: 150,
    ellipsis: true
  },
  {
    title: '组件',
    dataIndex: 'component',
    key: 'component',
    width: 150,
    ellipsis: true
  },
  {
    title: '权限标识',
    dataIndex: 'permission',
    key: 'permission',
    width: 150,
    ellipsis: true
  },
  {
    title: '状态',
    dataIndex: 'status',
    key: 'status',
    width: 100,
    align: 'center'
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    key: 'createTime',
    width: 180
  },
  {
    title: '操作',
    key: 'action',
    width: 200,
    fixed: 'right',
    align: 'center'
  }
]

// 应用列表（从API动态加载，只加载当前用户授权的应用）
const applications = ref([])
const loading = ref(false)

// 加载应用列表
const loadApplications = async () => {
  loading.value = true
  try {
    const response = await applicationApi.getAll()
    if (response.code === 200 && response.data) {
      applications.value = response.data || []
    }
  } catch (err) {
    if (import.meta.env.DEV) {
      console.error('加载应用列表失败:', err)
    }
    message.error(err.message || '加载应用列表失败')
  } finally {
    loading.value = false
  }
}

// 菜单数据（从API加载）
const allMenus = ref([])
const menuLoading = ref(false)

// 加载菜单数据
const loadMenus = async () => {
  if (!selectedAppId.value) {
    allMenus.value = []
    return
  }
  
  menuLoading.value = true
  try {
    const response = await appMenuApi.getMenuTree(selectedAppId.value)
    if (response.code === 200 && response.data) {
      // 将树形结构扁平化
      const flattenMenus = (menus, parentId = null) => {
        const result = []
        menus.forEach(menu => {
          const flatMenu = { ...menu, appId: selectedAppId.value, parentId: parentId || menu.parentId }
          result.push(flatMenu)
          if (menu.children && menu.children.length > 0) {
            result.push(...flattenMenus(menu.children, menu.id))
          }
        })
        return result
      }
      allMenus.value = flattenMenus(response.data)
      
      // 默认收起所有菜单（不展开）
      expandedIds.value = []
    } else {
      // 如果没有数据，返回空数组
      allMenus.value = []
    }
  } catch (err) {
    if (import.meta.env.DEV) {
        console.error('加载菜单数据失败:', err)
    }
    const errorMsg = err.message || '加载菜单数据失败，请检查：1. 后端服务是否正常运行 2. 数据库是否有菜单数据 3. 网络连接是否正常'
    message.error(errorMsg)
    allMenus.value = []
  } finally {
    menuLoading.value = false
  }
}

const keyword = ref('')
const menuKeyFilter = ref('')
const statusFilter = ref('')
const selectedAppId = ref(null)
const selectedApp = computed(() => {
  if (!selectedAppId.value) return null
  return applications.value.find(app => app.id === selectedAppId.value)
})
const showForm = ref(false)
const showDetail = ref(false)
const showDelete = ref(false)
const formMode = ref('create')
const currentItem = ref(null)
const expandedIds = ref([]) // 展开的菜单ID列表
const createParentId = ref(null) // 创建子菜单时的父菜单ID

const formData = ref({
  id: null,
  name: '',
  menuKey: '',
  parentId: null,
  menuType: '目录',
  icon: '',
  sort: 0,
  isExternal: false,
  route: '',
  component: '',
  permission: '',
  displayStatus: '显示',
  status: '正常',
  embeddedUrl: '',
  remark: '',
  extField1: '',
  extField2: '',
  extField3: '',
  extField4: '',
  extField5: '',
  extField6: ''
})

// 根据筛选条件过滤菜单
const filteredMenus = computed(() => {
  if (!selectedAppId.value) {
    return []
  }
  
  return allMenus.value.filter((item) => {
    const matchKeyword = keyword.value ? item.name.includes(keyword.value) : true
    const matchMenuKey = menuKeyFilter.value ? item.menuKey.includes(menuKeyFilter.value) : true
    const matchStatus = statusFilter.value ? item.status === statusFilter.value : true
    return matchKeyword && matchMenuKey && matchStatus
  })
})

// 构建树形结构
const treeData = computed(() => {
  const menus = filteredMenus.value
  const map = new Map()
  const roots = []
  
  // 先创建所有节点的映射
  menus.forEach(menu => {
    map.set(menu.id, { ...menu, children: [] })
  })
  
  // 构建树形结构
  menus.forEach(menu => {
    const node = map.get(menu.id)
    if (menu.parentId) {
      const parent = map.get(menu.parentId)
      if (parent) {
        parent.children.push(node)
      } else {
        // 如果父节点不存在，作为根节点
        roots.push(node)
      }
    } else {
      roots.push(node)
    }
  })
  
  // 按排序号排序
  const sortMenus = (menus) => {
    menus.sort((a, b) => a.sort - b.sort)
    menus.forEach(menu => {
      if (menu.children && menu.children.length > 0) {
        sortMenus(menu.children)
      }
    })
    return menus
  }
  
  return sortMenus(roots)
})

// 获取所有可以作为父菜单的菜单（包括所有菜单，用于创建子菜单时选择）
const parentMenus = computed(() => {
  if (!selectedAppId.value) return []
  // 返回所有菜单，包括父菜单和子菜单，都可以作为父菜单
  return filteredMenus.value
})

// 上级菜单树形数据（排除当前编辑的菜单及其子菜单）
const parentTreeData = computed(() => {
  if (!selectedAppId.value) return []
  
  // 过滤掉当前编辑的菜单及其所有子菜单
  const excludeIds = new Set()
  if (formData.value.id) {
    const collectChildren = (menuId) => {
      excludeIds.add(menuId)
      const children = filteredMenus.value.filter(m => m.parentId === menuId)
      children.forEach(child => collectChildren(child.id))
    }
    collectChildren(formData.value.id)
  }
  
  const filtered = filteredMenus.value.filter(m => !excludeIds.has(m.id))
  
  // 构建树形结构
  const map = new Map()
  const roots = []
  
  filtered.forEach(menu => {
    map.set(menu.id, { ...menu, children: [] })
  })
  
  filtered.forEach(menu => {
    const node = map.get(menu.id)
    if (menu.parentId && map.has(menu.parentId)) {
      const parent = map.get(menu.parentId)
      parent.children.push(node)
    } else {
      roots.push(node)
    }
  })
  
  // 按排序号排序
  const sortMenus = (menus) => {
    menus.sort((a, b) => a.sort - b.sort)
    menus.forEach(menu => {
      if (menu.children && menu.children.length > 0) {
        sortMenus(menu.children)
      }
    })
    return menus
  }
  
  return sortMenus(roots)
})

// 树形选择器过滤函数
const filterTreeNode = (inputValue, node) => {
  const name = node.name || ''
  return name.toLowerCase().includes(inputValue.toLowerCase())
}



// 树形选择器数据转换：将菜单数据转换为 TreeSelect 需要的格式
const treeSelectData = computed(() => {
  if (!selectedAppId.value) {
    return []
  }
  
  // 过滤掉当前编辑的菜单及其所有子菜单
  const excludeIds = new Set()
  if (formData.value.id) {
    const collectChildren = (menuId) => {
      excludeIds.add(menuId)
      const children = filteredMenus.value.filter(m => m.parentId === menuId)
      children.forEach(child => collectChildren(child.id))
    }
    collectChildren(formData.value.id)
  }
  
  const filtered = filteredMenus.value.filter(m => !excludeIds.has(m.id))
  
  // 构建树形结构
  const map = new Map()
  const roots = []
  
  filtered.forEach(menu => {
    map.set(menu.id, { 
      id: menu.id,
      name: menu.name,
      value: menu.id,
      children: [] 
    })
  })
  
  filtered.forEach(menu => {
    const node = map.get(menu.id)
    if (menu.parentId && map.has(menu.parentId)) {
      const parent = map.get(menu.parentId)
      parent.children.push(node)
    } else {
      roots.push(node)
    }
  })
  
  // 按排序号排序
  const sortMenus = (menus) => {
    menus.sort((a, b) => {
      const menuA = filteredMenus.value.find(m => m.id === a.id)
      const menuB = filteredMenus.value.find(m => m.id === b.id)
      return (menuA?.sort || 0) - (menuB?.sort || 0)
    })
    menus.forEach(menu => {
      if (menu.children && menu.children.length > 0) {
        sortMenus(menu.children)
      }
    })
    return menus
  }
  
  return sortMenus(roots)
})

// 处理表格展开/折叠
const handleExpand = (expanded, record) => {
  if (expanded) {
    if (!expandedIds.value.includes(record.id)) {
      expandedIds.value.push(record.id)
    }
  } else {
    const index = expandedIds.value.indexOf(record.id)
    if (index > -1) {
      expandedIds.value.splice(index, 1)
    }
  }
}

// 切换展开/折叠
const toggleExpand = (record) => {
  const index = expandedIds.value.indexOf(record.id)
  if (index > -1) {
    expandedIds.value.splice(index, 1)
  } else {
    expandedIds.value.push(record.id)
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

// 获取菜单的完整路径（用于显示父菜单选择）
const getMenuPath = (menu) => {
  if (!menu.parentId) {
    return menu.name
  }
  const parent = allMenus.value.find(m => m.id === menu.parentId)
  if (parent) {
    return `${getMenuPath(parent)} / ${menu.name}`
  }
  return menu.name
}

// 监听应用选择变化，重新加载菜单
watch(selectedAppId, (newAppId) => {
  if (newAppId) {
    loadMenus()
  } else {
    allMenus.value = []
    expandedIds.value = []
  }
})

watch([keyword, menuKeyFilter, statusFilter], () => {
  // 筛选条件变化时，保持展开状态
})

onMounted(() => {
  loadApplications()
})

const handleSearch = () => {
  // 搜索逻辑已在computed中实现，这里可以添加其他逻辑
}

const resetFilter = () => {
  keyword.value = ''
  menuKeyFilter.value = ''
  statusFilter.value = ''
  selectedAppId.value = null
  allMenus.value = []
  expandedIds.value = []
}

const openCreate = (parentMenu = null) => {
  if (!selectedAppId.value) {
    error('请先选择应用')
    return
  }
  formMode.value = 'create'
  createParentId.value = parentMenu ? parentMenu.id : null
  formData.value = {
    id: null,
    name: '',
    menuKey: '',
    parentId: parentMenu ? parentMenu.id : null,
    menuType: '目录',
    icon: '',
    sort: 0,
    isExternal: false,
    route: '',
    component: '',
    permission: '',
    displayStatus: '显示',
    status: '正常',
    embeddedUrl: '',
    remark: '',
    extField1: '',
    extField2: '',
    extField3: '',
    extField4: '',
    extField5: '',
    extField6: ''
  }
  showForm.value = true
}

const openCreateChild = (parentMenu) => {
  openCreate(parentMenu)
}

const openEdit = (item) => {
  formMode.value = 'edit'
  formData.value = { 
    id: item.id,
    name: item.name || '',
    menuKey: item.menuKey || '',
    parentId: item.parentId || null,
    menuType: item.menuType || '目录',
    icon: item.icon || '',
    sort: item.sort || 0,
    isExternal: item.isExternal || false,
    route: item.route || '',
    component: item.component || '',
    permission: item.permission || '',
    displayStatus: item.displayStatus || '显示',
    status: item.status || '正常',
    embeddedUrl: item.embeddedUrl || '',
    remark: item.remark || '',
    extField1: item.extField1 || '',
    extField2: item.extField2 || '',
    extField3: item.extField3 || '',
    extField4: item.extField4 || '',
    extField5: item.extField5 || '',
    extField6: item.extField6 || ''
  }
  showForm.value = true
}

const closeForm = () => {
  showForm.value = false
  createParentId.value = null
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
      // 创建时不需要发送 menuKey，后端会自动生成
      const response = await appMenuApi.create(selectedAppId.value, {
        name: formData.value.name,
        parentId: formData.value.parentId,
        menuType: formData.value.menuType,
        icon: formData.value.icon,
        iconActive: formData.value.iconActive,
        sort: formData.value.sort,
        isExternal: formData.value.isExternal ? 1 : 0,
        route: formData.value.route,
        component: formData.value.component,
        permission: formData.value.permission,
        displayStatus: formData.value.displayStatus,
        status: formData.value.status,
        embeddedUrl: formData.value.embeddedUrl,
        remark: formData.value.remark
      })
      
      if (response.code === 200) {
        message.success('创建成功')
        await loadMenus()
        showForm.value = false
        
        // 如果是子菜单，自动展开父菜单
        if (formData.value.parentId) {
          if (!expandedIds.value.includes(formData.value.parentId)) {
            expandedIds.value.push(formData.value.parentId)
          }
        }
      }
    } else {
      const response = await appMenuApi.update(selectedAppId.value, formData.value.id, {
        name: formData.value.name,
        menuKey: formData.value.menuKey,
        parentId: formData.value.parentId,
        menuType: formData.value.menuType,
        icon: formData.value.icon,
        iconActive: formData.value.iconActive,
        sort: formData.value.sort,
        isExternal: formData.value.isExternal ? 1 : 0,
        route: formData.value.route,
        component: formData.value.component,
        permission: formData.value.permission,
        displayStatus: formData.value.displayStatus,
        status: formData.value.status,
        embeddedUrl: formData.value.embeddedUrl,
        remark: formData.value.remark
      })
      
      if (response.code === 200) {
        message.success('更新成功')
        await loadMenus()
        showForm.value = false
      }
    }
  } catch (err) {
    if (import.meta.env.DEV) {
      console.error('保存菜单失败:', err)
    }
    message.error(err.message || '保存菜单失败')
  }
}

const openDetail = (item) => {
  // 查找父菜单名称
  const parentMenu = allMenus.value.find(m => m.id === item.parentId)
  currentItem.value = {
    ...item,
    parentName: parentMenu ? parentMenu.name : null
  }
  showDetail.value = true
}

const closeDetail = () => {
  currentItem.value = null
  showDetail.value = false
}

const openDelete = async (item) => {
  // 查询菜单详情，检查是否有子菜单
  try {
    const response = await appMenuApi.getById(selectedAppId.value, item.id)
    if (response.code === 200 && response.data) {
      // 检查是否有子菜单
      const hasChildren = allMenus.value.some(m => m.parentId === item.id)
      currentItem.value = {
        ...item,
        children: hasChildren ? allMenus.value.filter(m => m.parentId === item.id) : []
      }
      showDelete.value = true
    }
  } catch (err) {
    if (import.meta.env.DEV) {
      console.error('查询菜单详情失败:', err)
    }
    message.error(err.message || '查询菜单详情失败')
  }
}

const closeDelete = () => {
  currentItem.value = null
  showDelete.value = false
}

const confirmDelete = async () => {
  if (!currentItem.value) return
  
  try {
    const response = await appMenuApi.delete(selectedAppId.value, currentItem.value.id)
    if (response.code === 200) {
      message.success('删除成功')
      await loadMenus()
      closeDelete()
    }
  } catch (err) {
    if (import.meta.env.DEV) {
      console.error('删除菜单失败:', err)
    }
    message.error(err.message || '删除菜单失败')
  }
}
</script>

<style scoped>
.menu-tree-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.tree-toggle {
  background: none;
  border: none;
  color: #64748b;
  cursor: pointer;
  padding: 0;
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: color 0.2s;
}

.tree-toggle:hover {
  color: #667eea;
}

.tree-icon {
  width: 16px;
  height: 16px;
}

.tree-spacer {
  width: 20px;
  display: inline-block;
}

.tree-line {
  width: 1px;
  height: 20px;
  background: #e5e7eb;
  margin-right: 8px;
}

.tree-child {
  background: #f8fafc;
}

.tree-child:hover {
  background: #f1f5f9;
}

/* 表单样式 */
.select-with-clear {
  position: relative;
  display: flex;
  align-items: center;
}

.select-with-clear select {
  flex: 1;
  padding-right: 30px;
}

.clear-select {
  position: absolute;
  right: 8px;
  background: none;
  border: none;
  color: #64748b;
  cursor: pointer;
  font-size: 18px;
  padding: 0;
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: color 0.2s;
}

.clear-select:hover {
  color: #667eea;
}

.radio-group {
  display: flex;
  gap: 14px;
  align-items: center;
}

.radio-item {
  display: flex;
  align-items: center;
  gap: 3px;
  cursor: pointer;
  color: #1e293b;
  font-size: 13px;
  padding: 0;
  transition: color 0.2s;
}

.radio-item:hover {
  color: #667eea;
}

.radio-item input[type="radio"] {
  width: 14px;
  height: 14px;
  cursor: pointer;
  accent-color: #667eea;
  margin: 0;
  flex-shrink: 0;
  transform: scale(0.75);
  transform-origin: center;
}

.radio-item span {
  user-select: none;
}

.icon-input {
  display: flex;
  align-items: center;
  gap: 8px;
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  padding: 0 12px;
  height: 36px;
  transition: all 0.2s;
}

.icon-input:focus-within {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.icon-preview {
  width: 18px;
  height: 18px;
  color: #64748b;
  flex-shrink: 0;
}

.icon-input input {
  background: transparent;
  border: none;
  outline: none;
  color: #1e293b;
  flex: 1;
  font-size: 13px;
  padding: 0;
}

.icon-input input::placeholder {
  color: #94a3b8;
}

.number-input {
  display: flex;
  align-items: center;
  gap: 6px;
}

.number-input input {
  flex: 1;
}

.number-controls {
  display: flex;
  flex-direction: column;
  gap: 1px;
}

.number-btn {
  background: #f8fafc;
  border: 1px solid #e5e7eb;
  color: #64748b;
  width: 22px;
  height: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  font-size: 9px;
  padding: 0;
  transition: all 0.2s;
  line-height: 1;
  border-radius: 3px;
}

.number-btn:hover {
  background: #f1f5f9;
  border-color: #cbd5e1;
  color: #1e293b;
}

.number-btn:active {
  background: #e5e7eb;
}

.textarea-wrapper {
  position: relative;
}

.textarea-wrapper textarea {
  width: 100%;
  resize: vertical;
  padding-bottom: 24px;
}

.char-count {
  position: absolute;
  bottom: 8px;
  right: 12px;
  font-size: 12px;
  color: #94a3b8;
}

.icon-upload {
  display: flex;
  gap: 12px;
}

.upload-area {
  width: 100px;
  height: 100px;
  border: 2px dashed #cbd5e1;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
  background: #ffffff;
}

.upload-area:hover {
  border-color: #667eea;
  background: rgba(102, 126, 234, 0.05);
}

.upload-icon {
  width: 32px;
  height: 32px;
  color: #94a3b8;
  transition: color 0.2s;
}

.upload-area:hover .upload-icon {
  color: #667eea;
}

.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  margin-bottom: 16px;
}

.form-row.full-width {
  grid-column: 1 / -1;
}

.icon-upload-row {
  display: flex;
  gap: 20px;
}

.icon-upload-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.icon-upload-item label {
  font-size: 13px;
  color: #64748b;
  font-weight: 500;
}

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
  margin-top: 0;
  width: 100%;
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
  gap: 8px;
  min-width: 150px;
}

.filter-item label {
  font-size: 13px;
  color: #64748b;
  font-weight: 500;
  white-space: nowrap;
}

.filter-item input,
.filter-item select {
  background: #ffffff;
  border: 1px solid #e5e7eb;
  color: #1e293b;
  padding: 8px 12px;
  border-radius: 8px;
  outline: none;
  font-size: 13px;
  height: 40px;
  transition: all 0.2s;
  flex: 1;
}

.filter-item input:focus,
.filter-item select:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.filter-item input::placeholder {
  color: #94a3b8;
}

.filter-item input:disabled,
.filter-item select:disabled {
  background: #f8fafc;
  color: #94a3b8;
  cursor: not-allowed;
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
  min-width: 1200px;
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

.drawer-large {
  width: 800px;
  max-width: 95vw;
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
  gap: 16px;
}

.drawer-body .form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.drawer-body .form-row {
  display: flex !important;
  flex-direction: row !important;
  align-items: center !important;
  gap: 10px;
  flex-wrap: nowrap !important;
}

.drawer-body .form-row.full-width {
  grid-column: 1 / -1;
}

.drawer-body .form-row label {
  min-width: 80px;
  width: 80px;
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
  height: 36px;
  margin: 0;
  border-radius: 6px;
}

.drawer-body .form-row textarea {
  flex: 1;
  min-height: 70px;
  resize: vertical;
  margin: 0;
  padding-top: 8px;
  padding-bottom: 8px;
  border-radius: 6px;
}

.drawer-body .radio-group {
  flex: 1;
}

.drawer-body .radio-item input[type="radio"] {
  width: 14px;
  height: 14px;
  transform: scale(0.75);
  transform-origin: center;
}

.drawer-body .number-input {
  flex: 1;
}

.drawer-body .select-with-clear {
  flex: 1;
}

.drawer-body .icon-input {
  flex: 1;
}

/* 树形选择器样式 */
.tree-select-wrapper {
  position: relative;
  flex: 1;
  display: flex;
  align-items: center;
  gap: 8px;
}

.tree-select {
  flex: 1;
  height: 36px;
  padding: 0 12px;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  background: #ffffff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  cursor: pointer;
  transition: all 0.2s;
}

.tree-select:hover {
  border-color: #cbd5e1;
}

.tree-select-open {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.tree-select-text {
  flex: 1;
  font-size: 13px;
  color: #1e293b;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.tree-select-arrow {
  width: 16px;
  height: 16px;
  color: #64748b;
  flex-shrink: 0;
  transition: transform 0.2s;
}

.tree-select-arrow-open {
  transform: rotate(180deg);
}

.tree-select-dropdown {
  position: absolute;
  top: calc(100% + 4px);
  left: 0;
  right: 0;
  max-height: 300px;
  overflow-y: auto;
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  z-index: 1000;
  padding: 4px 0;
}

.tree-select-item {
  width: 100%;
}

.tree-select-option {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 12px;
  font-size: 13px;
  color: #1e293b;
  cursor: pointer;
  transition: background 0.2s;
  min-height: 36px;
}

.tree-select-option:hover {
  background: #f8fafc;
}

.tree-select-option-selected {
  background: #f0f4ff;
  color: #667eea;
}

.tree-select-toggle {
  background: none;
  border: none;
  padding: 0;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 16px;
  height: 16px;
  flex-shrink: 0;
  color: #64748b;
  transition: color 0.2s;
}

.tree-select-toggle:hover {
  color: #667eea;
}

.tree-select-icon {
  width: 14px;
  height: 14px;
}

.tree-select-spacer {
  width: 16px;
  height: 16px;
  flex-shrink: 0;
}

.drawer-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 16px 20px;
  border-top: 1px solid #f1f5f9;
}

/* 响应式布局 */
@media (max-width: 900px) {
  .drawer-large {
    width: 100vw;
  }
  
  .drawer-body .form-grid {
    grid-template-columns: 1fr;
  }
}

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

/* 菜单类型标签样式 */
.menu-type {
  display: inline-block;
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
  white-space: nowrap;
}

.menu-type-directory {
  background: rgba(102, 126, 234, 0.1);
  color: #667eea;
  border: 1px solid rgba(102, 126, 234, 0.2);
}

.menu-type-menu {
  background: rgba(34, 197, 94, 0.1);
  color: #22c55e;
  border: 1px solid rgba(34, 197, 94, 0.2);
}

.menu-type-button {
  background: rgba(251, 146, 60, 0.1);
  color: #fb923c;
  border: 1px solid rgba(251, 146, 60, 0.2);
}

/* 自定义树形选择器样式 */
.custom-tree-select {
  padding: 4px 0;
}

.tree-node {
  width: 100%;
}

.tree-node-item {
  display: flex;
  align-items: center;
  padding: 6px 12px;
  cursor: pointer;
  transition: background-color 0.2s;
  user-select: none;
}

.tree-node-item:hover {
  background-color: #f5f5f5;
}

.tree-node-selected {
  background-color: #e6f7ff !important;
  color: #1890ff;
}

.tree-toggle {
  width: 16px;
  height: 16px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  margin-right: 4px;
  font-size: 10px;
  color: #666;
  cursor: pointer;
  flex-shrink: 0;
}

.tree-toggle-placeholder {
  width: 16px;
  height: 16px;
  margin-right: 4px;
  flex-shrink: 0;
}

.tree-children {
  padding-left: 20px;
}

.tree-children-level2 {
  padding-left: 20px;
}

.tree-node-child {
  padding-left: 32px;
}

.tree-node-grandchild {
  padding-left: 52px;
}

/* 表格样式优化 */
:deep(.ant-table) {
  background: #ffffff;
}

:deep(.ant-table-thead > tr > th) {
  background: #fafafa;
  font-weight: 600;
  color: #262626;
  border-bottom: 1px solid #f0f0f0;
}

:deep(.ant-table-tbody > tr > td) {
  border-bottom: 1px solid #f0f0f0;
}

:deep(.ant-table-tbody > tr:hover > td) {
  background: #fafafa;
}

/* 展开/折叠图标样式 */
.expand-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 20px;
  height: 20px;
  cursor: pointer;
  transition: all 0.2s;
}

.expand-icon:hover {
  background: #f0f0f0;
  border-radius: 2px;
}

.expand-icon svg {
  display: block;
}

.expand-icon-placeholder {
  display: inline-block;
  width: 20px;
  height: 20px;
}

/* 操作按钮样式 */
.action-buttons {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0;
  white-space: nowrap;
}

.action-buttons .ant-btn-link {
  padding: 0 4px;
  height: auto;
  line-height: 1.5;
}

/* 菜单表单两列布局 */
.form-two-columns {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0 16px;
  align-items: start;
}

.form-two-columns :deep(.ant-form-item) {
  margin-bottom: 20px;
}

/* 确保两列布局中的label宽度与单行一致 */
/* 由于表单使用 label-col span:4，即约16.67%宽度，两列布局中每个字段占50%，所以label应占每个字段的约33.33%才能对齐 */
.form-two-columns :deep(.ant-form-item-label) {
  flex: 0 0 33.33% !important;
  max-width: 33.33% !important;
}

.form-two-columns :deep(.ant-form-item-control) {
  flex: 0 0 66.67% !important;
  max-width: 66.67% !important;
}

/* 响应式：小屏幕时改为单列 */
@media (max-width: 1200px) {
  .form-two-columns {
    grid-template-columns: 1fr;
  }
}
</style>
