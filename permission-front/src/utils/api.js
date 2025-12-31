/**
 * API工具类 - 封装HTTP请求
 */

// 使用相对路径，通过Vite代理访问后端
const API_BASE_URL = '/one-permission'

/**
 * 通用请求方法
 */
async function request(url, options = {}) {
  const token = localStorage.getItem('token')
  
  const defaultOptions = {
    headers: {
      'Content-Type': 'application/json',
      ...(token && { 'satoken': token })
    }
  }
  
  const config = {
    ...defaultOptions,
    ...options,
    headers: {
      ...defaultOptions.headers,
      ...(options.headers || {})
    }
  }
  
  try {
    const response = await fetch(`${API_BASE_URL}${url}`, config)
    const data = await response.json()
    
    // 记录请求和响应详情（仅在开发环境）
    if (process.env.NODE_ENV === 'development') {
      // 开发环境下打印请求信息
      if (import.meta.env.DEV) {
      }
    }
    
    // 检查token是否过期（401未授权）或token无效
    const isTokenInvalid = response.status === 401 || 
      (data.code && data.code !== 200 && (
        (data.message && (data.message.includes('未登录') || data.message.includes('token') || data.message.includes('Token'))) ||
        (data.message && data.message.includes('无效'))
      ))
    
    if (isTokenInvalid) {
      // token过期或无效，清除本地存储
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      
      // 显示提示信息
      if (typeof window !== 'undefined' && window.message) {
        window.message.error('登录已过期，请重新登录')
      } else {
        // 如果 message 组件还未加载，使用 alert
        setTimeout(() => {
          alert('登录已过期，请重新登录')
        }, 100)
      }
      
      // 延迟跳转，确保提示信息显示
      setTimeout(() => {
        // 跳转到登录页（刷新页面）
        window.location.reload()
      }, 500)
      
      throw new Error('登录已过期，请重新登录')
    }
    
    if (data.code === 200) {
      return data
    } else {
      const errorMsg = data.message || '请求失败'
      
      // 检查是否是token相关的错误
      const isTokenError = errorMsg.includes('token') || errorMsg.includes('Token') || errorMsg.includes('无效') || errorMsg.includes('未登录')
      
      if (isTokenError) {
        // token相关错误，清除本地存储并跳转
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
        
        // 显示提示信息
        if (typeof window !== 'undefined' && window.message) {
          window.message.error('登录已过期，请重新登录')
        } else {
          setTimeout(() => {
            alert('登录已过期，请重新登录')
          }, 100)
        }
        
        // 延迟跳转
        setTimeout(() => {
          window.location.reload()
        }, 500)
        
        throw new Error('登录已过期，请重新登录')
      }
      
      console.error('API请求失败:', url, '错误信息:', errorMsg, '完整响应:', data)
      throw new Error(errorMsg)
    }
  } catch (error) {
    // 如果是token过期错误，不打印日志（因为已经处理了）
    if (error.message && error.message.includes('登录已过期')) {
      throw error
    }
    
    // 检查错误消息中是否包含token相关信息
    if (error.message && (error.message.includes('token') || error.message.includes('Token') || error.message.includes('无效'))) {
      // token相关错误，清除本地存储并跳转
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      
      // 显示提示信息
      if (typeof window !== 'undefined' && window.message) {
        window.message.error('登录已过期，请重新登录')
      } else {
        setTimeout(() => {
          alert('登录已过期，请重新登录')
        }, 100)
      }
      
      // 延迟跳转
      setTimeout(() => {
        window.location.reload()
      }, 500)
      
      throw new Error('登录已过期，请重新登录')
    }
    
    // 其他错误正常打印
    console.error('API请求错误:', url, error)
    throw error
  }
}

/**
 * GET请求
 */
export function get(url, params = {}) {
  // 过滤掉undefined和null，但保留空字符串和0值（因为page和pageSize可能是0）
  const filteredParams = {}
  Object.keys(params).forEach(key => {
    const value = params[key]
    // 保留数字0和空字符串，只过滤undefined和null
    if (value !== undefined && value !== null) {
      // 对于字符串，如果为空字符串且不是分页参数，则过滤
      if (typeof value === 'string' && value === '' && key !== 'page' && key !== 'pageSize') {
        // 过滤空字符串（除了page和pageSize）
        return
      }
      filteredParams[key] = value
    }
  })
  
  const queryString = new URLSearchParams(filteredParams).toString()
  const fullUrl = queryString ? `${url}?${queryString}` : url
  // 开发环境下打印请求信息
  if (import.meta.env.DEV) {
  }
  return request(fullUrl, { method: 'GET' })
}

/**
 * POST请求
 */
export function post(url, data = {}) {
  return request(url, {
    method: 'POST',
    body: JSON.stringify(data)
  })
}

/**
 * PUT请求
 */
export function put(url, data = {}) {
  return request(url, {
    method: 'PUT',
    body: JSON.stringify(data)
  })
}

/**
 * DELETE请求
 */
export function del(url, data = null) {
  const options = { method: 'DELETE' }
  if (data) {
    options.body = JSON.stringify(data)
  }
  return request(url, options)
}

/**
 * 认证相关API
 */
export const authApi = {
  /**
   * 登录
   */
  login(loginAccount, password) {
    return post('/auth/login', { loginAccount, password })
  },
  
  /**
   * 获取当前用户信息
   */
  getCurrentUser() {
    return get('/auth/current')
  },
  
  /**
   * 退出登录
   */
  logout() {
    return post('/auth/logout')
  },
  
  /**
   * 修改密码
   */
  changePassword(oldPassword, newPassword, confirmPassword) {
    return post('/auth/change-password', { oldPassword, newPassword, confirmPassword })
  }
}

/**
 * 登录日志管理API
 */
export const sysLogininforApi = {
  /**
   * 分页查询登录日志列表
   */
  getList(params) {
    return get('/sys-logininfor', params)
  },
  
  /**
   * 删除登录日志
   */
  delete(infoId) {
    return del(`/sys-logininfor/${infoId}`)
  },
  
  /**
   * 清空登录日志
   */
  clean() {
    return del('/sys-logininfor/clean')
  }
}

/**
 * 登录用户管理API
 */
export const loginUserApi = {
  /**
   * 分页查询登录用户列表
   */
  getList(params) {
    return get('/login-users', params)
  },
  
  /**
   * 重置密码
   */
  resetPassword(id) {
    return post(`/login-users/${id}/reset-password`)
  },
  
  /**
   * 根据ID查询登录用户详情
   */
  getById(id) {
    return get(`/login-users/${id}`)
  },
  
  /**
   * 新增登录用户
   */
  create(data) {
    return post('/login-users', data)
  },
  
  /**
   * 更新登录用户
   */
  update(id, data) {
    return put(`/login-users/${id}`, data)
  },
  
  /**
   * 删除登录用户
   */
  delete(id) {
    return del(`/login-users/${id}`)
  },
  
  /**
   * 批量删除登录用户
   */
  batchDelete(ids) {
    return request('/login-users/batch', {
      method: 'DELETE',
      body: JSON.stringify(ids)
    })
  }
}

/**
 * 应用管理API
 */
export const applicationApi = {
  /**
   * 分页查询应用列表
   */
  getList(params) {
    return get('/applications', params)
  },
  
  /**
   * 查询所有应用列表（不分页，用于下拉选择，会根据当前用户权限过滤）
   */
  getAll() {
    return get('/applications/all')
  },
  
  /**
   * 查询所有应用列表（不分页，用于分配应用等场景，不进行权限过滤）
   */
  getAllForAssign() {
    return get('/applications/all-for-assign')
  },
  
  /**
   * 根据ID查询应用详情
   */
  getById(id) {
    return get(`/applications/${id}`)
  },
  
  /**
   * 新增应用
   */
  create(data) {
    return post('/applications', data)
  },
  
  /**
   * 更新应用
   */
  update(id, data) {
    return put(`/applications/${id}`, data)
  },
  
  /**
   * 删除应用
   */
  delete(id) {
    return del(`/applications/${id}`)
  }
}

/**
 * 普通用户管理API（仅查询）
 */
export const normalUserApi = {
  /**
   * 分页查询普通用户列表
   */
  getList(params) {
    return get('/normal-users', params)
  },
  
  /**
   * 根据ID查询普通用户详情
   */
  getById(id) {
    return get(`/normal-users/${id}`)
  },
  
  /**
   * 根据工号查询用户
   */
  getByWorkNo(workNo) {
    return get(`/normal-users/work-no/${workNo}`)
  }
}

/**
 * 应用菜单管理API
 */
export const appMenuApi = {
  /**
   * 查询应用菜单树形列表
   */
  getMenuTree(appId, status) {
    const params = status ? { status } : {}
    return get(`/applications/${appId}/menus/tree`, params)
  },
  
  /**
   * 分页查询应用菜单列表
   */
  getList(appId, params) {
    return get(`/applications/${appId}/menus`, params)
  },
  
  /**
   * 根据ID查询菜单详情
   */
  getById(appId, id) {
    return get(`/applications/${appId}/menus/${id}`)
  },
  
  /**
   * 新增菜单
   */
  create(appId, data) {
    return post(`/applications/${appId}/menus`, data)
  },
  
  /**
   * 更新菜单
   */
  update(appId, id, data) {
    return put(`/applications/${appId}/menus/${id}`, data)
  },
  
  /**
   * 删除菜单
   */
  delete(appId, id) {
    return del(`/applications/${appId}/menus/${id}`)
  }
}

/**
 * 登录用户应用授权API
 */
export const loginUserAppApi = {
  /**
   * 查询登录用户可访问的应用列表
   */
  getApplicationsByUserId(loginUserId) {
    return get(`/login-users/${loginUserId}/applications`)
  },
  
  /**
   * 为登录用户分配应用
   */
  assignApps(loginUserId, appIds) {
    return post(`/login-users/${loginUserId}/applications`, { appIds })
  },
  
  /**
   * 取消登录用户的某个应用权限
   */
  revokeApp(loginUserId, appId) {
    return del(`/login-users/${loginUserId}/applications/${appId}`)
  },
  
  /**
   * 查询应用被授权给了哪些登录用户
   */
  getAuthorizedUsers(appId) {
    return get(`/applications/${appId}/users`)
  },
  
  /**
   * 为应用分配登录用户
   */
  assignUsers(appId, userIds) {
    return post(`/applications/${appId}/users`, userIds)
  },
  
  /**
   * 取消应用的某个登录用户授权
   */
  revokeUser(appId, userId) {
    return del(`/applications/${appId}/users/${userId}`)
  }
}

/**
 * 应用角色管理API
 */
export const appRoleApi = {
  /**
   * 分页查询应用角色列表
   */
  getList(appId, params) {
    return get(`/applications/${appId}/roles`, params)
  },
  
  /**
   * 根据ID查询角色详情
   */
  getById(appId, id) {
    return get(`/applications/${appId}/roles/${id}`)
  },
  
  /**
   * 新增角色
   */
  create(appId, data) {
    return post(`/applications/${appId}/roles`, data)
  },
  
  /**
   * 更新角色
   */
  update(appId, id, data) {
    return put(`/applications/${appId}/roles/${id}`, data)
  },
  
  /**
   * 删除角色
   */
  delete(appId, id) {
    return del(`/applications/${appId}/roles/${id}`)
  },
  
  /**
   * 查询角色的菜单权限
   */
  getRoleMenus(appId, roleId) {
    return get(`/applications/${appId}/roles/${roleId}/menus`)
  }
}

/**
 * 应用角色用户管理API
 */
export const appRoleUserApi = {
  /**
   * 分页查询角色的用户分配列表
   */
  getUsers(appId, roleId, params) {
    return get(`/applications/${appId}/roles/${roleId}/users`, params)
  },
  
  /**
   * 分配用户给角色（个人分配）
   */
  assignUsers(appId, roleId, workNos) {
    return post(`/applications/${appId}/roles/${roleId}/users`, { workNos })
  },
  
  /**
   * 批量取消用户授权
   */
  batchRevokeUsers(appId, roleId, userIds) {
    return del(`/applications/${appId}/roles/${roleId}/users/batch`, userIds)
  },
  
  /**
   * 取消单个用户授权
   */
  revokeUser(appId, roleId, userId) {
    return del(`/applications/${appId}/roles/${roleId}/users/${userId}`)
  },
  
  /**
   * 查询可选用户列表（用于分配用户弹窗）
   */
  getAvailableUsers(appId, roleId, params) {
    return get(`/applications/${appId}/roles/${roleId}/users/available`, params)
  }
}

/**
 * 应用角色部门管理API
 */
export const appRoleDepartmentApi = {
  /**
   * 分页查询角色的部门分配列表
   */
  getDepartments(appId, roleId, params) {
    return get(`/applications/${appId}/roles/${roleId}/departments`, params)
  },
  
  /**
   * 分配部门给角色
   */
  assignDepartments(appId, roleId, departmentIds) {
    return post(`/applications/${appId}/roles/${roleId}/departments`, { departmentIds })
  },
  
  /**
   * 取消部门授权
   */
  revokeDepartment(appId, roleId, departmentId) {
    return del(`/applications/${appId}/roles/${roleId}/departments/${departmentId}`)
  },
  
  /**
   * 批量取消部门授权
   */
  batchRevokeDepartments(appId, roleId, departmentIds) {
    return del(`/applications/${appId}/roles/${roleId}/departments/batch`, departmentIds)
  }
}

/**
 * 部门管理API
 */
export const departmentApi = {
  /**
   * 获取部门树形结构
   */
  getDepartmentTree() {
    return get('/departments/tree')
  }
}

/**
 * 统计信息API
 */
export const statisticsApi = {
  /**
   * 获取Dashboard统计数据
   */
  getDashboardStatistics() {
    return get('/statistics/dashboard')
  }
}

/**
 * 消费者信息API
 */
export const consumerInfoApi = {
  /**
   * 分页查询消费者信息列表
   */
  pageQuery(params) {
    return get('/consumer-info/page', params)
  },
  
  /**
   * 根据ID查询消费者信息详情
   */
  getById(id) {
    return get(`/consumer-info/${id}`)
  },
  
  /**
   * 新增消费者信息
   */
  create(data) {
    return post('/consumer-info', data)
  },
  
  /**
   * 更新消费者信息
   */
  update(id, data) {
    return put(`/consumer-info/${id}`, data)
  },
  
  /**
   * 删除消费者信息
   */
  delete(id) {
    return del(`/consumer-info/${id}`)
  }
}

