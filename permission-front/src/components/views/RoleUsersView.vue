<template>
  <div class="role-users-container">
    <!-- Tab切换 -->
    <a-tabs v-model:activeKey="activeTab" class="role-users-tabs">
      <a-tab-pane key="users" tab="用户">
        <!-- 用户Tab内容 -->
        <div class="tab-content">
          <div class="filter-bar">
            <div class="filters-row">
              <a-input
                v-model:value="filters.adAccount"
                placeholder="请输入AD域账号"
                style="width: 180px;"
                allow-clear
              >
                <template #prefix>
                  <span class="filter-label">AD域账号：</span>
                </template>
              </a-input>
              <a-input
                v-model:value="filters.workNo"
                placeholder="请输入用户工号"
                style="width: 180px;"
                allow-clear
              >
                <template #prefix>
                  <span class="filter-label">工号：</span>
                </template>
              </a-input>
              <a-input
                v-model:value="filters.mobile"
                placeholder="请输入手机号码"
                style="width: 180px;"
                allow-clear
              >
                <template #prefix>
                  <span class="filter-label">手机号码：</span>
                </template>
              </a-input>
              <div class="filter-actions">
                <a-button type="primary" @click="handleSearch">搜索</a-button>
                <a-button @click="handleReset">重置</a-button>
              </div>
            </div>
            <div class="action-row">
              <a-button type="primary" @click="openAddUser">＋ 添加用户</a-button>
              <a-button type="primary" danger @click="handleBatchRevoke" :disabled="selectedIds.length === 0">批量取消授权</a-button>
              <a-button @click="handleClose">关闭</a-button>
            </div>
          </div>

          <!-- 用户列表表格 -->
          <a-table
            :columns="userColumns"
            :data-source="allUsers"
            :loading="userLoading"
            :pagination="false"
            :row-key="record => record.id"
            :row-selection="{
              selectedRowKeys: selectedIds,
              onChange: onSelectChange,
              onSelectAll: onSelectAllChange
            }"
            :scroll="{ x: 'max-content' }"
            bordered
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'appName'">
                {{ record.appName || props.appName }}
              </template>
              <template v-if="column.key === 'status'">
                <a-tag :color="record.status === '正常' ? 'success' : 'error'">
                  {{ record.status }}
                </a-tag>
              </template>
              <template v-if="column.key === 'action'">
                <a-button type="link" danger size="small" @click="openRevoke(record)">取消授权</a-button>
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
              @change="loadUsers"
              @show-size-change="loadUsers"
            />
          </div>
        </div>
      </a-tab-pane>

      <a-tab-pane key="departments" tab="部门">
        <!-- 部门Tab内容 -->
        <div class="tab-content">
          <div class="filter-bar">
            <div class="filters-row">
              <a-input
                v-model:value="departmentFilters.name"
                placeholder="请输入部门名称"
                style="width: 180px;"
                allow-clear
              >
                <template #prefix>
                  <span class="filter-label">部门名称：</span>
                </template>
              </a-input>
              <div class="filter-actions">
                <a-button type="primary" @click="handleDepartmentSearch">搜索</a-button>
                <a-button @click="handleDepartmentReset">重置</a-button>
              </div>
            </div>
            <div class="action-row">
              <a-button type="primary" @click="openAddDepartment">＋ 按部门分配</a-button>
              <a-button type="primary" danger @click="handleBatchRevokeDepartment" :disabled="selectedDepartmentIds.length === 0">批量取消授权</a-button>
              <a-button @click="handleClose">关闭</a-button>
            </div>
          </div>

          <!-- 部门列表表格 -->
          <a-table
            :columns="departmentColumns"
            :data-source="allDepartments"
            :loading="departmentLoading"
            :pagination="false"
            :row-key="record => record.id"
            :row-selection="{
              selectedRowKeys: selectedDepartmentIds,
              onChange: onDepartmentSelectChange,
              onSelectAll: onDepartmentSelectAllChange
            }"
            :scroll="{ x: 'max-content' }"
            bordered
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'appName'">
                {{ record.appName || props.appName }}
              </template>
              <template v-if="column.key === 'departmentName'">
                {{ record.departmentName || '-' }}
              </template>
              <template v-if="column.key === 'userCount'">
                {{ record.userCount || 0 }}
              </template>
              <template v-if="column.key === 'createTime'">
                {{ formatDate(record.createTime) }}
              </template>
              <template v-if="column.key === 'action'">
                <a-button type="link" danger size="small" @click="openRevokeDepartment(record)">取消授权</a-button>
              </template>
            </template>
            <template #emptyText>
              <a-empty description="暂无数据" />
            </template>
          </a-table>

          <!-- 分页 -->
          <div style="margin-top: 16px; display: flex; justify-content: space-between; align-items: center;">
            <span class="filter-label-inline">
              <template v-if="totalDepartmentRecords > 0">
                显示 {{ departmentStartIndex }}-{{ departmentEndIndex }}，共 {{ totalDepartmentRecords }} 条记录
              </template>
              <template v-else>
                暂无数据
              </template>
            </span>
            <a-pagination
              v-model:current="departmentCurrentPage"
              v-model:page-size="departmentPageSize"
              :total="totalDepartmentRecords"
              :show-size-changer="true"
              :show-total="(total) => `共 ${total} 条`"
              @change="loadDepartments"
              @show-size-change="loadDepartments"
            />
          </div>
        </div>
      </a-tab-pane>
    </a-tabs>

    <!-- 选择用户弹窗 -->
    <a-modal
      v-model:open="showAddUser"
      title="选择用户"
      :width="1200"
      :mask-closable="true"
      @cancel="closeAddUser"
      @ok="confirmAddUsers"
    >
      <div style="display: flex; flex-direction: column; gap: 16px;">
        <!-- 筛选栏 -->
        <div style="display: flex; align-items: center; gap: 12px; flex-wrap: wrap;">
          <a-input
            v-model:value="addUserFilters.adAccount"
            placeholder="请输入AD域账号"
            style="width: 180px;"
            allow-clear
          >
            <template #prefix>
              <span class="filter-label">AD域账号：</span>
            </template>
          </a-input>
          <a-input
            v-model:value="addUserFilters.nickname"
            placeholder="请输入用户昵称"
            style="width: 180px;"
            allow-clear
          >
            <template #prefix>
              <span class="filter-label">用户昵称：</span>
            </template>
          </a-input>
          <a-input
            v-model:value="addUserFilters.workNo"
            placeholder="请输入工号"
            style="width: 180px;"
            allow-clear
          >
            <template #prefix>
              <span class="filter-label">工号：</span>
            </template>
          </a-input>
          <a-button type="primary" @click="searchUsers">搜索</a-button>
          <a-button @click="resetAddUserFilters">重置</a-button>
        </div>

        <!-- 内容区域：左侧部门树 + 右侧用户列表 -->
        <div style="display: flex; gap: 16px; align-items: flex-start; height: 600px;">
          <!-- 左侧部门树 -->
          <div style="width: 250px; border: 1px solid #e5e7eb; border-radius: 8px; padding: 12px; background: #f8fafc; display: flex; flex-direction: column; height: 600px; box-sizing: border-box;">
            <div style="font-weight: 500; margin-bottom: 12px; color: #1e293b; flex-shrink: 0;">部门</div>
            <div style="flex: 1; overflow-y: auto; overflow-x: auto; min-height: 0; padding-right: 4px;" class="dept-tree-scroll">
              <template v-for="dept in departmentTreeForSelect" :key="dept.id">
                <div 
                  class="tree-item" 
                  :class="{ active: selectedDepartmentForSelect === dept.id }"
                  @click="selectDepartmentForUser(dept)"
                  :style="{ 
                    padding: '6px 8px', 
                    cursor: 'pointer', 
                    borderRadius: '4px', 
                    marginBottom: '4px', 
                    transition: 'background 0.2s',
                    background: selectedDepartmentForSelect === dept.id ? '#e6f7ff' : 'transparent',
                    display: 'flex',
                    alignItems: 'center',
                    minWidth: 'max-content'
                  }"
                >
                  <span 
                    v-if="dept.children && dept.children.length > 0" 
                    class="tree-toggle expand-icon" 
                    @click.stop="handleToggleExpand(dept.id, $event)"
                    style="display: inline-flex; align-items: center; justify-content: center; z-index: 10; position: relative;"
                  >
                    <svg 
                      viewBox="0 0 1024 1024" 
                      width="12" 
                      height="12" 
                      style="color: #000; transition: transform 0.2s; pointer-events: none;"
                      :style="{ transform: expandedDeptTreeForSelect.includes(dept.id) ? 'rotate(0deg)' : 'rotate(-90deg)' }"
                    >
                      <path d="M840.4 300H183.6c-19.7 0-30.7 20.8-18.5 35l328.4 380.8c9.4 10.9 27.5 10.9 37 0L858.9 335c12.2-14.2 1.2-35-18.5-35z" fill="currentColor"></path>
                    </svg>
                  </span>
                  <span v-else class="expand-icon-placeholder"></span>
                  <span style="white-space: nowrap; overflow: visible; flex: 1; min-width: 0;">{{ dept.name }}</span>
                </div>
                <!-- 递归渲染子部门 -->
                <template v-if="dept.children && dept.children.length > 0 && expandedDeptTreeForSelect.includes(dept.id)">
                  <template v-for="child in dept.children" :key="child.id">
                    <div 
                      class="tree-item" 
                      :class="{ active: selectedDepartmentForSelect === child.id }"
                      @click.stop="selectDepartmentForUser(child)"
                      :style="{ 
                        padding: '6px 8px 6px 32px', 
                        cursor: 'pointer', 
                        borderRadius: '4px', 
                        marginBottom: '4px', 
                        transition: 'background 0.2s',
                        background: selectedDepartmentForSelect === child.id ? '#e6f7ff' : 'transparent'
                      }"
                    >
                      <span v-if="child.children && child.children.length > 0" class="tree-toggle expand-icon" @click.stop="handleToggleExpand(child.id, $event)">
                        <svg 
                          viewBox="0 0 1024 1024" 
                          width="12" 
                          height="12" 
                          style="color: #000; transition: transform 0.2s;"
                          :style="{ transform: expandedDeptTreeForSelect.includes(child.id) ? 'rotate(0deg)' : 'rotate(-90deg)' }"
                        >
                          <path d="M840.4 300H183.6c-19.7 0-30.7 20.8-18.5 35l328.4 380.8c9.4 10.9 27.5 10.9 37 0L858.9 335c12.2-14.2 1.2-35-18.5-35z" fill="currentColor"></path>
                        </svg>
                      </span>
                      <span v-else class="expand-icon-placeholder"></span>
                      <span style="white-space: nowrap; overflow: visible; flex: 1; min-width: 0;">{{ child.name }}</span>
                    </div>
                    <!-- 三级部门 -->
                    <template v-if="child.children && child.children.length > 0 && expandedDeptTreeForSelect.includes(child.id)">
                      <template v-for="grandchild in child.children" :key="grandchild.id">
                        <div 
                          class="tree-item"
                          :class="{ active: selectedDepartmentForSelect === grandchild.id }"
                          @click.stop="selectDepartmentForUser(grandchild)"
                          :style="{ 
                            padding: '6px 8px 6px 56px', 
                            cursor: 'pointer', 
                            borderRadius: '4px', 
                            marginBottom: '4px', 
                            transition: 'background 0.2s',
                            background: selectedDepartmentForSelect === grandchild.id ? '#e6f7ff' : 'transparent'
                          }"
                        >
                          <span v-if="grandchild.children && grandchild.children.length > 0" class="tree-toggle expand-icon" @click.stop="handleToggleExpand(grandchild.id, $event)">
                            <svg 
                              viewBox="0 0 1024 1024" 
                              width="12" 
                              height="12" 
                              style="color: #000; transition: transform 0.2s;"
                              :style="{ transform: expandedDeptTreeForSelect.includes(grandchild.id) ? 'rotate(0deg)' : 'rotate(-90deg)' }"
                            >
                              <path d="M840.4 300H183.6c-19.7 0-30.7 20.8-18.5 35l328.4 380.8c9.4 10.9 27.5 10.9 37 0L858.9 335c12.2-14.2 1.2-35-18.5-35z" fill="currentColor"></path>
                            </svg>
                          </span>
                          <span v-else class="expand-icon-placeholder"></span>
                          <span style="white-space: nowrap; overflow: visible; flex: 1; min-width: 0;">{{ grandchild.name }}</span>
                        </div>
                        <!-- 四级部门 -->
                        <template v-if="grandchild.children && grandchild.children.length > 0 && expandedDeptTreeForSelect.includes(grandchild.id)">
                          <template v-for="level4 in grandchild.children" :key="level4.id">
                            <div 
                              class="tree-item"
                              :class="{ active: selectedDepartmentForSelect === level4.id }"
                              @click.stop="selectDepartmentForUser(level4)"
                              :style="{ 
                                padding: '6px 8px 6px 80px', 
                                cursor: 'pointer', 
                                borderRadius: '4px', 
                                marginBottom: '4px', 
                                transition: 'background 0.2s',
                                background: selectedDepartmentForSelect === level4.id ? '#e6f7ff' : 'transparent'
                              }"
                            >
                              <span v-if="level4.children && level4.children.length > 0" class="tree-toggle expand-icon" @click.stop="handleToggleExpand(level4.id, $event)">
                                <svg 
                                  viewBox="0 0 1024 1024" 
                                  width="12" 
                                  height="12" 
                                  style="color: #000; transition: transform 0.2s;"
                                  :style="{ transform: expandedDeptTreeForSelect.includes(level4.id) ? 'rotate(0deg)' : 'rotate(-90deg)' }"
                                >
                                  <path d="M840.4 300H183.6c-19.7 0-30.7 20.8-18.5 35l328.4 380.8c9.4 10.9 27.5 10.9 37 0L858.9 335c12.2-14.2 1.2-35-18.5-35z" fill="currentColor"></path>
                                </svg>
                              </span>
                              <span v-else class="expand-icon-placeholder"></span>
                              <span style="white-space: nowrap; overflow: visible; flex: 1; min-width: 0;">{{ level4.name }}</span>
                            </div>
                            <!-- 五级及以下部门（继续递归） -->
                            <template v-if="level4.children && level4.children.length > 0 && expandedDeptTreeForSelect.includes(level4.id)">
                              <div 
                                v-for="level5 in level4.children"
                                :key="level5.id"
                                class="tree-item"
                                :class="{ active: selectedDepartmentForSelect === level5.id }"
                                @click.stop="selectDepartmentForUser(level5)"
                                :style="{ 
                                  padding: '6px 8px 6px 104px', 
                                  cursor: 'pointer', 
                                  borderRadius: '4px', 
                                  marginBottom: '4px', 
                                  transition: 'background 0.2s',
                                  background: selectedDepartmentForSelect === level5.id ? '#e6f7ff' : 'transparent'
                                }"
                              >
                                <span style="margin-right: 18px; display: inline-block; width: 12px;"></span>
                                <span style="white-space: nowrap; overflow: visible; flex: 1; min-width: 0;">{{ level5.name }}</span>
                              </div>
                            </template>
                          </template>
                        </template>
                      </template>
                    </template>
                  </template>
                </template>
              </template>
            </div>
          </div>

          <!-- 右侧用户列表 -->
          <div style="flex: 1; height: 600px; overflow: hidden; display: flex; flex-direction: column;">
            <div style="flex: 1; overflow-y: auto; min-height: 0;">
              <a-table
                :columns="selectUserColumns"
                :data-source="selectUserList"
                :loading="selectUserLoading"
                :pagination="false"
                :row-key="record => record.id"
                :row-selection="{
                  selectedRowKeys: selectedUserIds,
                  onChange: onSelectUserChange,
                  getCheckboxProps: (record) => ({ disabled: isUserAdded(record.workNo) })
                }"
                :scroll="{ x: 'max-content' }"
                bordered
                :row-class-name="(record) => isUserAdded(record.workNo) ? 'user-added-row' : ''"
              >
              <template #bodyCell="{ column, record }">
                <template v-if="column.key === 'status'">
                  <a-tag :color="record.status === '正常' ? 'success' : 'error'">
                    {{ record.status }}
                  </a-tag>
                </template>
                <template v-if="column.key === 'action'">
                  <a-button 
                    type="link" 
                    size="small" 
                    @click="addSingleUser(record)"
                    :disabled="isUserAdded(record.workNo)"
                  >
                    {{ isUserAdded(record.workNo) ? '已添加' : '添加' }}
                  </a-button>
                </template>
              </template>
              <template #emptyText>
                <a-empty description="暂无用户数据" />
              </template>
              </a-table>
            </div>

            <!-- 分页 -->
            <div style="margin-top: 16px; display: flex; justify-content: flex-end; flex-shrink: 0;">
              <a-pagination
                v-model:current="selectUserPage"
                v-model:page-size="selectUserPageSize"
                :total="selectUserTotalRecords"
                :show-size-changer="true"
                :show-total="(total) => `共 ${total} 条`"
                @change="loadSelectUsers"
                @show-size-change="loadSelectUsers"
              />
            </div>
          </div>
        </div>
      </div>
      <template #footer>
        <div style="text-align: right;">
          <a-button style="margin-right: 8px;" @click="closeAddUser">取消</a-button>
          <a-button type="primary" @click="confirmAddUsers">确定</a-button>
        </div>
      </template>
    </a-modal>

    <!-- 按部门分配弹窗 -->
    <div v-if="showAddDepartment" class="modal" @click.self="closeAddDepartment">
      <div class="modal-content transfer-modal" @click.stop>
        <div class="modal-header">
          <div class="modal-title">按部门分配权限</div>
          <button class="close" @click="closeAddDepartment">×</button>
        </div>
        <div class="modal-body transfer-body">
          <div class="transfer-container">
            <!-- 左侧：可选部门 -->
            <div class="transfer-panel">
              <div class="transfer-panel-header">
                <input type="checkbox" :checked="selectAllAvailable" @change="toggleSelectAllAvailable" />
                <span>可选部门（{{ availableDepartments.length }}）</span>
              </div>
              <div class="transfer-panel-search">
                <input v-model="availableSearchKeyword" type="text" placeholder="搜索部门名称" />
              </div>
              <div class="transfer-panel-list">
                <div v-if="filteredAvailableDepartments.length === 0" class="transfer-empty">
                  暂无数据
                </div>
                <template v-for="dept in filteredAvailableDepartments" :key="dept.id">
                  <!-- 父部门 -->
                  <div 
                    class="transfer-item"
                    :class="{ 
                      selected: selectedAvailableIds.includes(dept.id),
                      disabled: isDepartmentAssigned(dept.id)
                    }"
                    @click="handleDeptClick(dept)"
                  >
                    <input 
                      type="checkbox" 
                      :checked="selectedAvailableIds.includes(dept.id)" 
                      :disabled="isDepartmentAssigned(dept.id)"
                      @click.stop="handleDeptCheckboxClick(dept)"
                      @change.stop
                    />
                    <span class="transfer-item-label" :class="{ disabled: isDepartmentAssigned(dept.id) }">
                      <span v-if="dept.children && dept.children.length > 0" class="tree-toggle" @click.stop="toggleDeptExpand(dept.id)">
                        <svg v-if="expandedDeptIds.includes(dept.id)" class="tree-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                          <polyline points="6 9 12 15 18 9" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                        </svg>
                        <svg v-else class="tree-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                          <polyline points="9 18 15 12 9 6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                        </svg>
                      </span>
                      {{ dept.name }}
                    </span>
                    <span class="transfer-item-count">（{{ dept.userCount }}人）</span>
                  </div>
                  <!-- 递归渲染子部门 -->
                  <template v-if="dept.children && dept.children.length > 0 && expandedDeptIds.includes(dept.id)">
                    <template v-for="child in dept.children" :key="child.id">
                      <div 
                        class="transfer-item"
                        :class="{ 
                          'transfer-item-child': true,
                          selected: selectedAvailableIds.includes(child.id),
                          disabled: isDepartmentAssigned(child.id) || isParentSelected(dept.id)
                        }"
                        @click="handleChildDeptClick(child, dept)"
                      >
                        <input 
                          type="checkbox" 
                          :checked="selectedAvailableIds.includes(child.id)" 
                          :disabled="isDepartmentAssigned(child.id) || isParentSelected(dept.id)"
                          @click.stop="handleChildDeptCheckboxClick(child, dept)"
                          @change.stop
                        />
                        <span class="transfer-item-label" :class="{ disabled: isDepartmentAssigned(child.id) || isParentSelected(dept.id) }">
                          <span v-if="child.children && child.children.length > 0" class="tree-toggle" @click.stop="toggleDeptExpand(child.id)">
                            <svg v-if="expandedDeptIds.includes(child.id)" class="tree-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                              <polyline points="6 9 12 15 18 9" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                            </svg>
                            <svg v-else class="tree-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                              <polyline points="9 18 15 12 9 6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                            </svg>
                          </span>
                          {{ child.name }}
                        </span>
                        <span class="transfer-item-count">（{{ child.userCount }}人）</span>
                      </div>
                      <!-- 三级部门 -->
                      <template v-if="child.children && child.children.length > 0 && expandedDeptIds.includes(child.id)">
                        <div 
                          v-for="grandchild in child.children"
                          :key="grandchild.id"
                          class="transfer-item"
                          :class="{ 
                            'transfer-item-child': true,
                            selected: selectedAvailableIds.includes(grandchild.id),
                            disabled: isDepartmentAssigned(grandchild.id) || isParentSelected(child.id) || isParentSelected(dept.id)
                          }"
                          @click="handleChildDeptClick(grandchild, child)"
                          style="padding-left: 56px;"
                        >
                          <input 
                            type="checkbox" 
                            :checked="selectedAvailableIds.includes(grandchild.id)" 
                            :disabled="isDepartmentAssigned(grandchild.id) || isParentSelected(child.id) || isParentSelected(dept.id)"
                            @click.stop="handleChildDeptCheckboxClick(grandchild, child)"
                            @change.stop
                          />
                          <span class="transfer-item-label" :class="{ disabled: isDepartmentAssigned(grandchild.id) || isParentSelected(child.id) || isParentSelected(dept.id) }">
                            <span v-if="grandchild.children && grandchild.children.length > 0" class="tree-toggle" @click.stop="toggleDeptExpand(grandchild.id)">
                              <svg v-if="expandedDeptIds.includes(grandchild.id)" class="tree-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                                <polyline points="6 9 12 15 18 9" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                              </svg>
                              <svg v-else class="tree-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                                <polyline points="9 18 15 12 9 6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                              </svg>
                            </span>
                            {{ grandchild.name }}
                          </span>
                          <span class="transfer-item-count">（{{ grandchild.userCount }}人）</span>
                        </div>
                        <!-- 四级及以下部门 -->
                        <template v-if="grandchild.children && grandchild.children.length > 0 && expandedDeptIds.includes(grandchild.id)">
                          <div 
                            v-for="level4 in grandchild.children"
                            :key="level4.id"
                            class="transfer-item"
                            :class="{ 
                              'transfer-item-child': true,
                              selected: selectedAvailableIds.includes(level4.id),
                              disabled: isDepartmentAssigned(level4.id) || isParentSelected(grandchild.id) || isParentSelected(child.id) || isParentSelected(dept.id)
                            }"
                            @click="handleChildDeptClick(level4, grandchild)"
                            style="padding-left: 80px;"
                          >
                            <input 
                              type="checkbox" 
                              :checked="selectedAvailableIds.includes(level4.id)" 
                              :disabled="isDepartmentAssigned(level4.id) || isParentSelected(grandchild.id) || isParentSelected(child.id) || isParentSelected(dept.id)"
                              @click.stop="handleChildDeptCheckboxClick(level4, grandchild)"
                              @change.stop
                            />
                            <span class="transfer-item-label" :class="{ disabled: isDepartmentAssigned(level4.id) || isParentSelected(grandchild.id) || isParentSelected(child.id) || isParentSelected(dept.id) }">
                              <span v-if="level4.children && level4.children.length > 0" class="tree-toggle" @click.stop="toggleDeptExpand(level4.id)">
                                <svg v-if="expandedDeptIds.includes(level4.id)" class="tree-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                                  <polyline points="6 9 12 15 18 9" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                                </svg>
                                <svg v-else class="tree-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                                  <polyline points="9 18 15 12 9 6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                                </svg>
                              </span>
                              {{ level4.name }}
                            </span>
                            <span class="transfer-item-count">（{{ level4.userCount }}人）</span>
                          </div>
                          <!-- 五级及以下部门（继续递归） -->
                          <template v-if="level4.children && level4.children.length > 0 && expandedDeptIds.includes(level4.id)">
                            <div 
                              v-for="level5 in level4.children"
                              :key="level5.id"
                              class="transfer-item"
                              :class="{ 
                                'transfer-item-child': true,
                                selected: selectedAvailableIds.includes(level5.id),
                                disabled: isDepartmentAssigned(level5.id) || isParentSelected(level4.id) || isParentSelected(grandchild.id) || isParentSelected(child.id) || isParentSelected(dept.id)
                              }"
                              @click="handleChildDeptClick(level5, level4)"
                              style="padding-left: 104px;"
                            >
                              <input 
                                type="checkbox" 
                                :checked="selectedAvailableIds.includes(level5.id)" 
                                :disabled="isDepartmentAssigned(level5.id) || isParentSelected(level4.id) || isParentSelected(grandchild.id) || isParentSelected(child.id) || isParentSelected(dept.id)"
                                @click.stop="handleChildDeptCheckboxClick(level5, level4)"
                                @change.stop
                              />
                              <span class="transfer-item-label" :class="{ disabled: isDepartmentAssigned(level5.id) || isParentSelected(level4.id) || isParentSelected(grandchild.id) || isParentSelected(child.id) || isParentSelected(dept.id) }">
                                {{ level5.name }}
                              </span>
                              <span class="transfer-item-count">（{{ level5.userCount }}人）</span>
                            </div>
                          </template>
                        </template>
                      </template>
                    </template>
                  </template>
                </template>
              </div>
            </div>

            <!-- 中间：操作按钮 -->
            <div class="transfer-actions">
              <button 
                class="transfer-btn" 
                :disabled="selectedAvailableIds.length === 0"
                @click="addSelectedDepartments"
                title="添加选中部门"
              >
                <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M9 18l6-6-6-6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
              </button>
              <button 
                class="transfer-btn" 
                :disabled="selectedAssignedIds.length === 0"
                @click="removeSelectedDepartments"
                title="移除选中部门"
              >
                <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M15 18l-6-6 6-6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
              </button>
            </div>

            <!-- 右侧：已选部门 -->
            <div class="transfer-panel">
              <div class="transfer-panel-header">
                <input type="checkbox" :checked="selectAllAssigned" @change="toggleSelectAllAssigned" />
                <span>已选部门（{{ assignedDepartments.length }}）</span>
              </div>
              <div class="transfer-panel-search">
                <input v-model="assignedSearchKeyword" type="text" placeholder="搜索部门名称" />
              </div>
              <div class="transfer-panel-list">
                <div v-if="filteredAssignedDepartments.length === 0" class="transfer-empty">
                  暂无数据
                </div>
                <div 
                  v-for="dept in filteredAssignedDepartments" 
                  :key="dept.id"
                  class="transfer-item"
                  :class="{ selected: selectedAssignedIds.includes(dept.id) }"
                  @click="handleAssignedDeptClick(dept)"
                >
                  <input 
                    type="checkbox" 
                    :checked="selectedAssignedIds.includes(dept.id)" 
                    @click.stop="handleAssignedDeptCheckboxClick(dept)"
                    @change.stop
                  />
                  <span class="transfer-item-label">{{ dept.name }}</span>
                  <span class="transfer-item-count">（{{ dept.userCount }}人）</span>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button class="ghost" @click="closeAddDepartment">取消</button>
          <button class="primary" @click="confirmAddDepartment" :disabled="assignedDepartments.length === 0">确定</button>
        </div>
      </div>
    </div>

    <!-- 批量取消部门授权确认 -->
    <div v-if="showBatchRevokeDepartment" class="modal" @click.self="closeBatchRevokeDepartment">
      <div class="modal-content small" @click.stop>
        <div class="modal-header">
          <div class="modal-title">批量取消部门授权</div>
          <button class="close" @click="closeBatchRevokeDepartment">×</button>
        </div>
        <div class="modal-body">
          <div style="display: flex; align-items: center; gap: 12px; margin-bottom: 16px;">
            <svg style="width: 24px; height: 24px; color: #f59e0b; flex-shrink: 0;" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            <div style="flex: 1;">
              <div style="font-size: 15px; font-weight: 500; color: #1e293b; margin-bottom: 4px;">
                确认取消部门授权吗？
              </div>
              <div style="font-size: 13px; color: #64748b; line-height: 1.5;">
                您已选择 <span style="color: #ef4444; font-weight: 600;">{{ selectedDepartmentIds.length }}</span> 个部门，该操作将同时取消这些部门下所有用户的授权，且不可恢复，请谨慎操作。
              </div>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button class="ghost revoke-cancel-btn" @click="closeBatchRevokeDepartment">取消</button>
          <button class="danger revoke-confirm-btn" @click="confirmBatchRevokeDepartment">确定</button>
        </div>
      </div>
    </div>

    <!-- 部门取消授权确认 -->
    <div v-if="showRevokeDepartment" class="modal" @click.self="closeRevokeDepartment">
      <div class="modal-content small" @click.stop>
        <div class="modal-header">
          <div class="modal-title">取消部门授权</div>
          <button class="close" @click="closeRevokeDepartment">×</button>
        </div>
        <div class="modal-body">
          确认取消部门「{{ currentRevokeDepartment?.departmentName || currentRevokeDepartment?.department }}」的授权吗？该操作将同时取消该部门下所有用户的授权，且不可恢复。
        </div>
        <div class="modal-footer">
          <button class="ghost revoke-cancel-btn" @click="closeRevokeDepartment">取消</button>
          <button class="danger revoke-confirm-btn" @click="confirmRevokeDepartment">确定</button>
        </div>
      </div>
    </div>

    <!-- 批量取消授权确认 -->
    <div v-if="showBatchRevoke" class="modal" @click.self="closeBatchRevoke">
      <div class="modal-content small" @click.stop>
        <div class="modal-header">
          <div class="modal-title">批量取消授权</div>
          <button class="close" @click="closeBatchRevoke">×</button>
        </div>
        <div class="modal-body">
          <div style="display: flex; align-items: center; gap: 12px; margin-bottom: 16px;">
            <svg style="width: 24px; height: 24px; color: #f59e0b; flex-shrink: 0;" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            <div style="flex: 1;">
              <div style="font-size: 15px; font-weight: 500; color: #1e293b; margin-bottom: 4px;">
                确认取消授权吗？
              </div>
              <div style="font-size: 13px; color: #64748b; line-height: 1.5;">
                您已选择 <span style="color: #ef4444; font-weight: 600;">{{ selectedIds.length }}</span> 个用户，取消授权后将无法恢复，请谨慎操作。
              </div>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button class="ghost revoke-cancel-btn" @click="closeBatchRevoke">取消</button>
          <button class="danger revoke-confirm-btn" @click="confirmBatchRevoke">确定</button>
        </div>
      </div>
    </div>

    <!-- 取消授权确认 -->
    <div v-if="showRevoke" class="modal" @click.self="closeRevoke">
      <div class="modal-content small" @click.stop>
        <div class="modal-header">
          <div class="modal-title">取消授权</div>
          <button class="close" @click="closeRevoke">×</button>
        </div>
        <div class="modal-body">
          确认取消用户「{{ currentUser?.userName }}」的授权吗？
        </div>
        <div class="modal-footer">
          <button class="ghost revoke-cancel-btn" @click="closeRevoke">取消</button>
          <button class="danger revoke-confirm-btn" @click="confirmRevoke">确定</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, watch, onMounted, nextTick } from 'vue'
import { appRoleUserApi, appRoleDepartmentApi, departmentApi } from '@/utils/api'
import { message } from 'ant-design-vue'

const props = defineProps({
  appId: {
    type: [Number, String],
    required: true
  },
  roleId: {
    type: [Number, String],
    required: true
  },
  roleName: {
    type: String,
    required: true
  },
  appName: {
    type: String,
    required: true
  }
})

const emit = defineEmits(['close'])

// Tab切换
const activeTab = ref('users')

// 用户表格列定义
const userColumns = [
  {
    title: '应用系统',
    dataIndex: 'appName',
    key: 'appName',
    width: 150,
    ellipsis: true
  },
  {
    title: '角色',
    dataIndex: 'roleName',
    key: 'roleName',
    width: 200,
    ellipsis: true
  },
  {
    title: '工号',
    dataIndex: 'workNo',
    key: 'workNo',
    width: 120,
    ellipsis: true
  },
  {
    title: 'AD域账号',
    dataIndex: 'adAccount',
    key: 'adAccount',
    width: 150,
    ellipsis: true
  },
  {
    title: '用户',
    dataIndex: 'userName',
    key: 'userName',
    width: 120,
    ellipsis: true
  },
  {
    title: '手机',
    dataIndex: 'mobile',
    key: 'mobile',
    width: 130,
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
    width: 150,
    fixed: 'right'
  }
]

// 选择用户表格列定义
const selectUserColumns = [
  {
    title: '工号',
    dataIndex: 'workNo',
    key: 'workNo',
    width: 120,
    ellipsis: true
  },
  {
    title: 'AD域账号',
    dataIndex: 'adAccount',
    key: 'adAccount',
    width: 150,
    ellipsis: true
  },
  {
    title: '用户昵称',
    dataIndex: 'nickname',
    key: 'nickname',
    width: 120,
    ellipsis: true
  },
  {
    title: '邮箱',
    dataIndex: 'email',
    key: 'email',
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
    title: '操作',
    key: 'action',
    width: 100,
    fixed: 'right'
  }
]

// 部门表格列定义
const departmentColumns = [
  {
    title: '应用系统',
    dataIndex: 'appName',
    key: 'appName',
    width: 150,
    ellipsis: true
  },
  {
    title: '角色',
    dataIndex: 'roleName',
    key: 'roleName',
    width: 200,
    ellipsis: true
  },
  {
    title: '部门名称',
    dataIndex: 'departmentName',
    key: 'departmentName',
    width: 200,
    ellipsis: true
  },
  {
    title: '部门人数',
    dataIndex: 'userCount',
    key: 'userCount',
    width: 120
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    key: 'createTime',
    width: 150,
    ellipsis: true
  },
  {
    title: '操作',
    key: 'action',
    width: 150,
    fixed: 'right'
  }
]

// 用户选择处理
const onSelectChange = (selectedRowKeys) => {
  selectedIds.value = selectedRowKeys
}

const onSelectAllChange = (selected, selectedRows, changeRows) => {
  if (selected) {
    selectedIds.value = allUsers.value.map(u => u.id)
  } else {
    selectedIds.value = []
  }
}

// 部门选择处理
const onDepartmentSelectChange = (selectedRowKeys) => {
  selectedDepartmentIds.value = selectedRowKeys
}

const onDepartmentSelectAllChange = (selected, selectedRows, changeRows) => {
  if (selected) {
    selectedDepartmentIds.value = allDepartments.value.map(d => d.id)
  } else {
    selectedDepartmentIds.value = []
  }
}

// 选择用户表格选择处理
const onSelectUserChange = (selectedRowKeys) => {
  selectedUserIds.value = selectedRowKeys
}

// 分页显示范围
const startIndex = computed(() => {
  if (totalRecords.value === 0) return 0
  return (currentPage.value - 1) * pageSize.value + 1
})

const endIndex = computed(() => {
  const end = currentPage.value * pageSize.value
  return end > totalRecords.value ? totalRecords.value : end
})

const departmentStartIndex = computed(() => {
  if (totalDepartmentRecords.value === 0) return 0
  return (departmentCurrentPage.value - 1) * departmentPageSize.value + 1
})

const departmentEndIndex = computed(() => {
  const end = departmentCurrentPage.value * departmentPageSize.value
  return end > totalDepartmentRecords.value ? totalDepartmentRecords.value : end
})

// 已加入角色的用户
const allUsers = ref([])
const userLoading = ref(false)
const userTotalRecords = ref(0)

// 已分配的部门
const allDepartments = ref([])
const departmentLoading = ref(false)
const departmentTotalRecords = ref(0)

// 用户Tab相关
const filters = ref({
  adAccount: '',
  workNo: '',
  mobile: ''
})

// 选中的用户ID
const selectedIds = ref([])
const selectAll = computed(() => {
  return allUsers.value.length > 0 && allUsers.value.every(u => selectedIds.value.includes(u.id))
})

// 用户分页
const currentPage = ref(1)
const pageSize = ref(10)
const gotoPageNum = ref(1)

// 加载用户列表
const loadUsers = async () => {
  if (!props.appId || !props.roleId) return
  
  userLoading.value = true
  try {
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value,
      ...Object.fromEntries(
        Object.entries(filters.value).filter(([, value]) => value !== undefined && value !== null && value !== '')
      )
    }
    
    const response = await appRoleUserApi.getUsers(props.appId, props.roleId, params)
    if (response.code === 200 && response.data) {
      allUsers.value = response.data.list || []
      userTotalRecords.value = response.data.total || 0
      
      // 更新已添加用户工号列表（用于标记可选用户列表中的已添加用户）
      const workNos = allUsers.value.map(u => u.workNo)
      workNos.forEach(workNo => {
        addedUserWorkNos.value.add(workNo)
      })
    } else {
      message.error(response.message || '加载用户列表失败')
      allUsers.value = []
    }
  } catch (err) {
    if (import.meta.env.DEV) {
      console.error('加载用户列表失败:', err)
    }
    message.error(err.message || '加载用户列表失败')
    allUsers.value = []
  } finally {
    userLoading.value = false
  }
}

// 加载部门列表
const loadDepartments = async () => {
  if (!props.appId || !props.roleId) return
  
  departmentLoading.value = true
  try {
    const params = {
      page: departmentCurrentPage.value,
      pageSize: departmentPageSize.value,
      ...Object.fromEntries(
        Object.entries(departmentFilters.value).filter(([, value]) => value !== undefined && value !== null && value !== '')
      )
    }
    
    const response = await appRoleDepartmentApi.getDepartments(props.appId, props.roleId, params)
    if (response.code === 200 && response.data) {
      allDepartments.value = response.data.list || []
      departmentTotalRecords.value = response.data.total || 0
    } else {
      message.error(response.message || '加载部门列表失败')
      allDepartments.value = []
    }
  } catch (err) {
    if (import.meta.env.DEV) {
      console.error('加载部门列表失败:', err)
    }
    message.error(err.message || '加载部门列表失败')
    allDepartments.value = []
  } finally {
    departmentLoading.value = false
  }
}

// 部门Tab相关
const departmentFilters = ref({
  name: ''
})

// 选中的部门ID
const selectedDepartmentIds = ref([])
const selectAllDepartments = computed(() => {
  return allDepartments.value.length > 0 && allDepartments.value.every(d => selectedDepartmentIds.value.includes(d.id))
})

// 部门分页
const departmentCurrentPage = ref(1)
const departmentPageSize = ref(10)

// 筛选后的数据（后端已处理筛选，这里直接使用）
const filteredData = computed(() => allUsers.value)
const totalRecords = computed(() => userTotalRecords.value)
const totalPages = computed(() => Math.ceil(totalRecords.value / pageSize.value))
const paginatedData = computed(() => filteredData.value)

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

// 选择用户弹窗相关
const showAddUser = ref(false)
const selectedDepartmentForSelect = ref(null)
const expandedDeptTreeForSelect = ref([])

// 部门树（用于选择用户）
const departmentTreeForSelect = computed(() => {
  return allDepartmentOptions.value.map(dept => {
    if (dept.children && dept.children.length > 0) {
      return { ...dept, children: [...dept.children] }
    }
    return dept
  })
})

// 切换部门树展开/折叠
const toggleDeptTreeForSelect = (id) => {
  const current = Array.isArray(expandedDeptTreeForSelect.value) ? [...expandedDeptTreeForSelect.value] : []
  const index = current.indexOf(id)
  if (index > -1) {
    // 移除ID
    const newArray = current.filter(item => item !== id)
    expandedDeptTreeForSelect.value = newArray
  } else {
    // 添加ID
    const newArray = [...current, id]
    expandedDeptTreeForSelect.value = newArray
  }
}

// 处理展开/折叠点击事件
const handleToggleExpand = (id, event) => {
  if (event) {
    event.preventDefault()
    event.stopPropagation()
  }
  toggleDeptTreeForSelect(id)
}

// 选择部门（用于筛选用户）
const selectDepartmentForUser = (dept) => {
  selectedDepartmentForSelect.value = dept.id
  selectUserPage.value = 1
  loadSelectUsers()
}

const addUserFilters = ref({
  adAccount: '',
  nickname: '',
  workNo: ''
})

// 按部门分配相关 - 穿梭框
const showAddDepartment = ref(false)

// 所有部门数据（树形结构）- 从后端API获取
const allDepartmentOptions = ref([])

// 加载部门树数据
const loadDepartmentTree = async () => {
  try {
    const response = await departmentApi.getDepartmentTree()
    if (response.code === 200 && response.data) {
      allDepartmentOptions.value = response.data
    } else {
      message.error(response.message || '加载部门树失败')
      allDepartmentOptions.value = []
    }
  } catch (err) {
    if (import.meta.env.DEV) {
      console.error('加载部门树失败:', err)
    }
    message.error(err.message || '加载部门树失败')
    allDepartmentOptions.value = []
  }
}

// 展开的部门ID列表
const expandedDeptIds = ref([])

// 切换部门展开/折叠
const toggleDeptExpand = (id) => {
  const index = expandedDeptIds.value.indexOf(id)
  if (index > -1) {
    expandedDeptIds.value.splice(index, 1)
  } else {
    expandedDeptIds.value.push(id)
  }
}

// 检查部门是否已分配（在已选列表中）
const isDepartmentAssigned = (id) => {
  return assignedDepartments.value.some(d => d.id === id)
}

// 已分配的部门ID列表（从已分配部门中获取）
const assignedDepartmentIds = computed(() => {
  return allDepartments.value.map(d => {
    // 优先使用 departmentId，如果没有则通过名称查找
    if (d.departmentId) {
      return d.departmentId
    }
    const dept = allDepartmentOptions.value.find(opt => opt.name === d.departmentName || opt.name === d.department)
    return dept ? dept.id : null
  }).filter(id => id !== null)
})

// 可选部门（排除已分配的，包括子部门）
const availableDepartments = computed(() => {
  return allDepartmentOptions.value.map(dept => {
    // 如果父部门已分配，不显示
    if (assignedDepartmentIds.value.includes(dept.id)) {
      return null
    }
    // 如果有子部门，过滤掉已分配的子部门，但保留父部门
    if (dept.children && dept.children.length > 0) {
      const filteredChildren = dept.children.filter(child => !assignedDepartmentIds.value.includes(child.id))
      // 即使所有子部门都已分配，也显示父部门（但子部门列表为空）
      return { ...dept, children: filteredChildren }
    }
    // 没有子部门的部门，直接返回
    return dept
  }).filter(dept => dept !== null)
})

// 已选部门（临时选择，用于确认）
const assignedDepartments = ref([])

// 左侧选中项
const selectedAvailableIds = ref([])
const availableSearchKeyword = ref('')

// 右侧选中项
const selectedAssignedIds = ref([])
const assignedSearchKeyword = ref('')

// 过滤后的可选部门（包括子部门搜索）
const filteredAvailableDepartments = computed(() => {
  let result = availableDepartments.value
  // 确保子部门数组存在
  result = result.map(dept => {
    if (dept.children) {
      return { ...dept, children: dept.children || [] }
    }
    return dept
  })
  
  if (availableSearchKeyword.value) {
    result = result.map(dept => {
      const nameMatch = dept.name.includes(availableSearchKeyword.value)
      if (dept.children && dept.children.length > 0) {
        const filteredChildren = dept.children.filter(child => child.name.includes(availableSearchKeyword.value))
        // 如果父部门匹配或子部门有匹配，都显示
        if (nameMatch || filteredChildren.length > 0) {
          return { ...dept, children: nameMatch ? dept.children : filteredChildren }
        }
        return null
      }
      return nameMatch ? dept : null
    }).filter(dept => dept !== null)
  }
  return result
})

// 过滤后的已选部门
const filteredAssignedDepartments = computed(() => {
  let result = assignedDepartments.value
  if (assignedSearchKeyword.value) {
    result = result.filter(dept => dept.name.includes(assignedSearchKeyword.value))
  }
  return result
})

// 全选/取消全选 - 左侧（只选择可选的部门，排除已分配的）
const selectAllAvailable = computed(() => {
  const selectableDepts = filteredAvailableDepartments.value.filter(dept => 
    !isDepartmentAssigned(dept.id)
  )
  return selectableDepts.length > 0 && 
         selectableDepts.every(dept => selectedAvailableIds.value.includes(dept.id))
})

const toggleSelectAllAvailable = () => {
  const selectableDepts = filteredAvailableDepartments.value.filter(dept => 
    !isDepartmentAssigned(dept.id)
  )
  
  if (selectAllAvailable.value) {
    // 取消全选
    selectableDepts.forEach(dept => {
      const index = selectedAvailableIds.value.indexOf(dept.id)
      if (index > -1) {
        selectedAvailableIds.value.splice(index, 1)
      }
    })
  } else {
    // 全选
    selectableDepts.forEach(dept => {
      if (!selectedAvailableIds.value.includes(dept.id)) {
        selectedAvailableIds.value.push(dept.id)
      }
    })
  }
}

// 全选/取消全选 - 右侧
const selectAllAssigned = computed(() => {
  return filteredAssignedDepartments.value.length > 0 && 
         filteredAssignedDepartments.value.every(dept => selectedAssignedIds.value.includes(dept.id))
})

const toggleSelectAllAssigned = () => {
  if (selectAllAssigned.value) {
    filteredAssignedDepartments.value.forEach(dept => {
      const index = selectedAssignedIds.value.indexOf(dept.id)
      if (index > -1) {
        selectedAssignedIds.value.splice(index, 1)
      }
    })
  } else {
    filteredAssignedDepartments.value.forEach(dept => {
      if (!selectedAssignedIds.value.includes(dept.id)) {
        selectedAssignedIds.value.push(dept.id)
      }
    })
  }
}

// 检查父部门是否被选中（传入父部门id，检查该父部门是否被选中）
const isParentSelected = (parentId) => {
  return selectedAvailableIds.value.includes(parentId)
}

// 处理部门点击（父部门）
const handleDeptClick = (dept) => {
  if (isDepartmentAssigned(dept.id)) {
    return
  }
  toggleSelectAvailable(dept.id)
}

// 处理部门复选框点击（父部门）
const handleDeptCheckboxClick = (dept) => {
  if (isDepartmentAssigned(dept.id)) {
    return
  }
  toggleSelectAvailable(dept.id)
}

// 处理子部门点击
const handleChildDeptClick = (child, parent) => {
  if (isDepartmentAssigned(child.id) || isParentSelected(parent.id)) {
    return
  }
  toggleSelectAvailable(child.id)
}

// 处理子部门复选框点击
const handleChildDeptCheckboxClick = (child, parent) => {
  if (isDepartmentAssigned(child.id) || isParentSelected(parent.id)) {
    return
  }
  toggleSelectAvailable(child.id)
}

// 切换选中状态
const toggleSelectAvailable = (id) => {
  const index = selectedAvailableIds.value.indexOf(id)
  if (index > -1) {
    selectedAvailableIds.value.splice(index, 1)
  } else {
    selectedAvailableIds.value.push(id)
  }
}

// 处理已选部门点击
const handleAssignedDeptClick = (dept) => {
  toggleSelectAssigned(dept.id)
}

// 处理已选部门复选框点击
const handleAssignedDeptCheckboxClick = (dept) => {
  toggleSelectAssigned(dept.id)
}

const toggleSelectAssigned = (id) => {
  const index = selectedAssignedIds.value.indexOf(id)
  if (index > -1) {
    selectedAssignedIds.value.splice(index, 1)
  } else {
    selectedAssignedIds.value.push(id)
  }
}

// 添加选中部门到右侧（包括子部门）
const addSelectedDepartments = () => {
  const idsToAdd = [...selectedAvailableIds.value]
  idsToAdd.forEach(id => {
    // 查找父部门
    let dept = availableDepartments.value.find(d => d.id === id)
    if (!dept) {
      // 查找子部门
      for (const parent of availableDepartments.value) {
        if (parent.children) {
          dept = parent.children.find(c => c.id === id)
          if (dept) break
        }
      }
    }
    if (dept && !assignedDepartments.value.find(d => d.id === id)) {
      assignedDepartments.value.push({ ...dept })
    }
  })
  selectedAvailableIds.value = []
}

// 从右侧移除选中部门
const removeSelectedDepartments = () => {
  selectedAssignedIds.value.forEach(id => {
    const index = assignedDepartments.value.findIndex(d => d.id === id)
    if (index > -1) {
      assignedDepartments.value.splice(index, 1)
    }
  })
  selectedAssignedIds.value = []
}

// 选择用户弹窗 - 可选用户列表
const allSelectUsers = ref([])
const selectUserLoading = ref(false)
const selectUserTotalRecords = ref(0)

// 加载可选用户列表（后端已过滤已分配用户）
const loadSelectUsers = async () => {
  if (!props.appId || !props.roleId) return
  
  selectUserLoading.value = true
  try {
    const params = {
      page: selectUserPage.value,
      pageSize: selectUserPageSize.value,
      departmentId: selectedDepartmentForSelect.value,
      ...Object.fromEntries(
        Object.entries(addUserFilters.value).filter(([, value]) => value !== undefined && value !== null && value !== '')
      )
    }
    
    const response = await appRoleUserApi.getAvailableUsers(props.appId, props.roleId, params)
    if (response.code === 200 && response.data) {
      allSelectUsers.value = response.data.list || []
      selectUserTotalRecords.value = response.data.total || 0
    } else {
      message.error(response.message || '加载可选用户列表失败')
      allSelectUsers.value = []
    }
  } catch (err) {
    if (import.meta.env.DEV) {
      console.error('加载可选用户列表失败:', err)
    }
    message.error(err.message || '加载可选用户列表失败')
    allSelectUsers.value = []
  } finally {
    selectUserLoading.value = false
  }
}

const selectedUserIds = ref([])
const selectUserPage = ref(1)
const selectUserPageSize = ref(10)

// 已添加的用户工号列表（用于标记已添加的用户）
const addedUserWorkNos = ref(new Set())

// 过滤可选用户列表（后端已处理筛选，这里直接使用）
const filteredSelectUsers = computed(() => allSelectUsers.value)
const selectUserList = computed(() => filteredSelectUsers.value)

const selectUserTotal = computed(() => selectUserTotalRecords.value)
const selectUserTotalPages = computed(() => Math.ceil(selectUserTotal.value / selectUserPageSize.value))

const selectUserVisiblePages = computed(() => {
  const pages = []
  const total = selectUserTotalPages.value
  const current = selectUserPage.value
  
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

// 取消授权
const showRevoke = ref(false)
const currentUser = ref(null)

// 批量取消授权
const showBatchRevoke = ref(false)

const handleSearch = () => {
  currentPage.value = 1
  loadUsers()
}

const handleReset = () => {
  filters.value = {
    adAccount: '',
    workNo: '',
    mobile: ''
  }
  currentPage.value = 1
  loadUsers()
}

const toggleSelect = (id) => {
  const index = selectedIds.value.indexOf(id)
  if (index > -1) {
    selectedIds.value.splice(index, 1)
  } else {
    selectedIds.value.push(id)
  }
}

const toggleSelectAll = (e) => {
  if (e.target.checked) {
    selectedIds.value = paginatedData.value.map(u => u.id)
  } else {
    selectedIds.value = []
  }
}

const goToPage = (page) => {
  if (page === '...' || page < 1 || page > totalPages.value) return
  currentPage.value = page
  gotoPageNum.value = page
  loadUsers()
}

watch(gotoPageNum, (val) => {
  if (val >= 1 && val <= totalPages.value) {
    currentPage.value = val
  }
})

// 监听 props 变化，当 appId 或 roleId 变化时重新加载数据
watch([() => props.appId, () => props.roleId], ([newAppId, newRoleId], [oldAppId, oldRoleId]) => {
  if (newAppId && newRoleId && (newAppId !== oldAppId || newRoleId !== oldRoleId)) {
    // 重置状态
    currentPage.value = 1
    departmentCurrentPage.value = 1
    filters.value = {
      adAccount: '',
      workNo: '',
      mobile: ''
    }
    departmentFilters.value = {
      name: ''
    }
    addedUserWorkNos.value.clear()
    
    // 加载数据
    loadUsers()
    loadDepartments()
  }
}, { immediate: false })

// 组件挂载时加载数据
onMounted(() => {
  // 加载部门树数据（用于按部门分配功能）
  loadDepartmentTree()
  
  if (props.appId && props.roleId) {
    loadUsers()
    loadDepartments()
  }
})

const openAddUser = async () => {
  showAddUser.value = true
  selectedUserIds.value = []
  selectedDepartmentForSelect.value = null
  // 重置展开状态，但不立即设置默认展开
  expandedDeptTreeForSelect.value = []
  addUserFilters.value = {
    adAccount: '',
    nickname: '',
    workNo: ''
  }
  selectUserPage.value = 1
  
  // 加载已添加用户列表，用于标记可选用户列表中的已添加用户
  await loadUsers()
  
  // 如果部门树数据为空，重新加载
  if (allDepartmentOptions.value.length === 0) {
    await loadDepartmentTree()
  }
  
  // 不自动展开，让用户手动控制
  // 如果需要默认展开，可以取消下面的注释
  // nextTick(() => {
  //   expandedDeptTreeForSelect.value = allDepartmentOptions.value
  //     .filter(dept => dept.children && dept.children.length > 0)
  //     .map(dept => dept.id)
  // })
  
  // 加载可选用户列表
  await loadSelectUsers()
}

const closeAddUser = () => {
  showAddUser.value = false
  selectedUserIds.value = []
  selectedDepartmentForSelect.value = null
  expandedDeptTreeForSelect.value = []
  resetAddUserFilters()
}

const searchUsers = () => {
  selectUserPage.value = 1
  loadSelectUsers()
}

// 根据ID查找部门（包括子部门）
const findDepartmentById = (id) => {
  for (const dept of allDepartmentOptions.value) {
    if (dept.id === id) {
      return dept
    }
    if (dept.children) {
      const child = dept.children.find(c => c.id === id)
      if (child) {
        return child
      }
    }
  }
  return null
}

const resetAddUserFilters = () => {
  addUserFilters.value = {
    adAccount: '',
    nickname: '',
    workNo: ''
  }
  selectedDepartmentForSelect.value = null
  selectUserPage.value = 1
  loadSelectUsers()
}

const toggleSelectUser = (id) => {
  const index = selectedUserIds.value.indexOf(id)
  if (index > -1) {
    selectedUserIds.value.splice(index, 1)
  } else {
    selectedUserIds.value.push(id)
  }
}

// 检查用户是否已添加
const isUserAdded = (workNo) => {
  return addedUserWorkNos.value.has(workNo)
}

const addSingleUser = async (user) => {
  if (!props.appId || !props.roleId) {
    message.error('应用ID或角色ID不存在')
    return
  }
  
  // 如果用户已添加，直接返回
  if (isUserAdded(user.workNo)) {
    return
  }
  
  try {
    const response = await appRoleUserApi.assignUsers(props.appId, props.roleId, [user.workNo])
    
    if (response.code === 200) {
      // 标记用户为已添加
      addedUserWorkNos.value.add(user.workNo)
      message.success('添加成功')
      // 刷新已添加用户列表
      await loadUsers()
      // 重新加载可选用户列表（已添加的用户会被后端过滤掉）
      if (showAddUser.value) {
        await loadSelectUsers()
      }
    } else {
      message.error(response.message || '添加失败')
    }
  } catch (err) {
    if (import.meta.env.DEV) {
      console.error('添加单个用户失败:', err)
    }
    message.error(err.message || '添加用户失败')
  }
}

const confirmAddUsers = async () => {
  if (!props.appId || !props.roleId) return
  
  const selectedUsers = filteredSelectUsers.value.filter(u => selectedUserIds.value.includes(u.id))
  if (selectedUsers.length === 0) {
    message.error('请选择要添加的用户')
    return
  }
  
  try {
    const workNos = selectedUsers.map(u => u.workNo)
    const response = await appRoleUserApi.assignUsers(props.appId, props.roleId, workNos)
    if (response.code === 200) {
      // 标记所有选中的用户为已添加
      workNos.forEach(workNo => {
        addedUserWorkNos.value.add(workNo)
      })
      message.success('添加成功')
      // 刷新已添加用户列表
      await loadUsers()
      // 如果弹窗仍然打开，重新加载可选用户列表（已添加的用户会被后端过滤掉）
      if (showAddUser.value) {
        selectedUserIds.value = []
        await loadSelectUsers()
      } else {
        closeAddUser()
      }
    } else {
      message.error(response.message || '添加失败')
    }
  } catch (err) {
    console.error('添加用户失败:', err)
    message.error(err.message || '添加用户失败')
  }
}

const handleBatchRevoke = () => {
  if (selectedIds.value.length === 0) {
    message.error('请选择要取消授权的用户')
    return
  }
  showBatchRevoke.value = true
}

const closeBatchRevoke = () => {
  showBatchRevoke.value = false
}

const confirmBatchRevoke = async () => {
  if (!props.appId || !props.roleId) return
  
  if (selectedIds.value.length === 0) {
    message.error('请选择要取消授权的用户')
    return
  }
  
  try {
    const response = await appRoleUserApi.batchRevokeUsers(props.appId, props.roleId, selectedIds.value)
    if (response.code === 200) {
      message.success('取消授权成功')
      selectedIds.value = []
      closeBatchRevoke()
      await loadUsers()
    } else {
      message.error(response.message || '取消授权失败')
    }
  } catch (err) {
    if (import.meta.env.DEV) {
      console.error('批量取消授权失败:', err)
    }
    error(err.message || '批量取消授权失败')
  }
}

const openRevoke = (user) => {
  currentUser.value = user
  showRevoke.value = true
}

const closeRevoke = () => {
  currentUser.value = null
  showRevoke.value = false
}

const confirmRevoke = async () => {
  if (!currentUser.value || !props.appId || !props.roleId) return
  
  try {
    const response = await appRoleUserApi.revokeUser(props.appId, props.roleId, currentUser.value.id)
    if (response.code === 200) {
      message.success('取消授权成功')
      closeRevoke()
      await loadUsers()
    } else {
      message.error(response.message || '取消授权失败')
    }
  } catch (err) {
    if (import.meta.env.DEV) {
      console.error('取消授权失败:', err)
    }
    message.error(err.message || '取消授权失败')
  }
}

const handleClose = () => {
  emit('close')
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  if (isNaN(date.getTime())) return '-'
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  const seconds = String(date.getSeconds()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}

// 部门Tab相关函数
const filteredDepartmentData = computed(() => allDepartments.value)
const totalDepartmentRecords = computed(() => departmentTotalRecords.value)
const totalDepartmentPages = computed(() => Math.ceil(departmentTotalRecords.value / departmentPageSize.value))
const paginatedDepartmentData = computed(() => filteredDepartmentData.value)

const visibleDepartmentPages = computed(() => {
  const pages = []
  const total = totalDepartmentPages.value
  const current = departmentCurrentPage.value
  
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

const goToDepartmentPage = (page) => {
  if (page === '...' || page < 1 || page > totalDepartmentPages.value) return
  departmentCurrentPage.value = page
  loadDepartments()
}

const handleDepartmentSearch = () => {
  departmentCurrentPage.value = 1
  loadDepartments()
}

const handleDepartmentReset = () => {
  departmentFilters.value = {
    name: ''
  }
  departmentCurrentPage.value = 1
  loadDepartments()
}

const toggleSelectDepartment = (id) => {
  const index = selectedDepartmentIds.value.indexOf(id)
  if (index > -1) {
    selectedDepartmentIds.value.splice(index, 1)
  } else {
    selectedDepartmentIds.value.push(id)
  }
}

const toggleSelectAllDepartments = () => {
  if (selectAllDepartments.value) {
    selectedDepartmentIds.value = []
  } else {
    selectedDepartmentIds.value = paginatedDepartmentData.value.map(d => d.id)
  }
}

const openRevokeDepartment = (dept) => {
  currentRevokeDepartment.value = dept
  showRevokeDepartment.value = true
}

const closeRevokeDepartment = () => {
  showRevokeDepartment.value = false
  currentRevokeDepartment.value = null
}

const confirmRevokeDepartment = async () => {
  if (!currentRevokeDepartment.value || !props.appId || !props.roleId) return
  
  try {
    const response = await appRoleDepartmentApi.revokeDepartment(
      props.appId, 
      props.roleId, 
      currentRevokeDepartment.value.id
    )
    if (response.code === 200) {
      message.success('取消授权成功')
      closeRevokeDepartment()
      await loadDepartments()
    } else {
      message.error(response.message || '取消授权失败')
    }
  } catch (err) {
    if (import.meta.env.DEV) {
      console.error('取消部门授权失败:', err)
    }
    error(err.message || '取消部门授权失败')
  }
}

const handleBatchRevokeDepartment = () => {
  if (selectedDepartmentIds.value.length === 0) {
    message.error('请选择要取消授权的部门')
    return
  }
  showBatchRevokeDepartment.value = true
}

const closeBatchRevokeDepartment = () => {
  showBatchRevokeDepartment.value = false
}

const confirmBatchRevokeDepartment = async () => {
  if (!props.appId || !props.roleId) return
  
  if (selectedDepartmentIds.value.length === 0) {
    message.error('请选择要取消授权的部门')
    return
  }
  
  try {
    const response = await appRoleDepartmentApi.batchRevokeDepartments(
      props.appId, 
      props.roleId, 
      selectedDepartmentIds.value
    )
    if (response.code === 200) {
      message.success('批量取消授权成功')
      selectedDepartmentIds.value = []
      closeBatchRevokeDepartment()
      await loadDepartments()
    } else {
      message.error(response.message || '批量取消授权失败')
    }
  } catch (err) {
    if (import.meta.env.DEV) {
      console.error('批量取消部门授权失败:', err)
    }
    message.error(err.message || '批量取消部门授权失败')
  }
}

// 部门取消授权相关
const showRevokeDepartment = ref(false)
const currentRevokeDepartment = ref(null)

// 批量取消部门授权
const showBatchRevokeDepartment = ref(false)

// 按部门分配相关函数
const openAddDepartment = () => {
  showAddDepartment.value = true
  // 初始化已选部门（从已分配部门中加载）
  assignedDepartments.value = allDepartments.value.map(d => {
    // 先查找父部门
    let dept = allDepartmentOptions.value.find(opt => opt.name === d.departmentName || opt.name === d.department)
    if (!dept) {
      // 如果没找到，可能是子部门，查找所有子部门
      for (const parent of allDepartmentOptions.value) {
        if (parent.children) {
          dept = parent.children.find(child => child.name === d.departmentName || child.name === d.department)
          if (dept) {
            // 返回子部门的完整信息
            return { ...dept }
          }
        }
      }
    }
    return dept ? { ...dept } : null
  }).filter(d => d !== null)
  selectedAvailableIds.value = []
  selectedAssignedIds.value = []
  availableSearchKeyword.value = ''
  assignedSearchKeyword.value = ''
  // 默认展开所有有子部门的部门
  expandedDeptIds.value = []
  // 延迟展开，确保DOM渲染完成
  setTimeout(() => {
    expandedDeptIds.value = allDepartmentOptions.value
      .filter(dept => dept.children && dept.children.length > 0)
      .map(dept => dept.id)
  }, 100)
}

const closeAddDepartment = () => {
  showAddDepartment.value = false
  assignedDepartments.value = []
  selectedAvailableIds.value = []
  selectedAssignedIds.value = []
  availableSearchKeyword.value = ''
  assignedSearchKeyword.value = ''
}

const confirmAddDepartment = async () => {
  if (!props.appId || !props.roleId) return
  
  // 当前弹窗中勾选的部门（部门ID）
  const newDeptIds = assignedDepartments.value.map(d => d.id)
  
  if (newDeptIds.length === 0) {
    message.error('请至少选择一个部门')
    return
  }
  
  // 已分配的部门：来自已分配部门列表（allDepartments）
  // allDepartments 中：id 为关联表ID，departmentId 为部门ID
  const existingDeptMap = new Map() // departmentId -> relationId
  allDepartments.value.forEach(d => {
    if (d.departmentId) {
      existingDeptMap.set(d.departmentId, d.id)
    }
  })
  
  const existingDeptIds = Array.from(existingDeptMap.keys())
  
  // 需要新增的部门（部门ID）= 新选择的 - 已存在的
  const toAddDeptIds = newDeptIds.filter(id => !existingDeptIds.includes(id))
  // 需要取消授权的部门（关联表ID）= 原来有但现在未选中的
  const toRemoveRelationIds = existingDeptIds
    .filter(id => !newDeptIds.includes(id))
    .map(id => existingDeptMap.get(id))
  
  try {
    // 先取消不再勾选的部门授权
    if (toRemoveRelationIds.length > 0) {
      const revokeResp = await appRoleDepartmentApi.batchRevokeDepartments(
        props.appId,
        props.roleId,
        toRemoveRelationIds
      )
      if (revokeResp.code !== 200) {
        message.error(revokeResp.message || '取消部门授权失败')
        return
      }
    }
    
    // 再新增本次新勾选的部门
    if (toAddDeptIds.length > 0) {
      const assignResp = await appRoleDepartmentApi.assignDepartments(
        props.appId,
        props.roleId,
        toAddDeptIds
      )
      if (assignResp.code !== 200) {
        message.error(assignResp.message || '分配部门失败')
        return
      }
    }
    
    message.success('保存成功')
    closeAddDepartment()
    await loadDepartments()
    // 切换到部门tab显示最新的已分配部门
    activeTab.value = 'departments'
  } catch (err) {
    if (import.meta.env.DEV) {
      console.error('分配部门失败:', err)
    }
    message.error(err.message || '分配部门失败')
  }
}

// Mock所有用户数据（用于按部门筛选）- 使用水浒传人物数据
const allNormalUsers = ref([
  { workNo: 'SH045', name: '魏定国', department: '研发中心', adAccount: 'wei.dingguo', mobile: '13800138045', status: '正常' },
  { workNo: 'SH046', name: '萧让', department: '研发中心', adAccount: 'xiao.rang', mobile: '13800138046', status: '正常' },
  { workNo: 'SH047', name: '裴宣', department: '产品中心', adAccount: 'pei.xuan', mobile: '13800138047', status: '正常' },
  { workNo: 'SH048', name: '欧鹏', department: '运营中心', adAccount: 'ou.peng', mobile: '13800138048', status: '正常' },
  { workNo: 'SH049', name: '邓飞', department: '市场部', adAccount: 'deng.fei', mobile: '13800138049', status: '正常' },
  { workNo: 'SH050', name: '燕顺', department: '研发中心', adAccount: 'yan.shun', mobile: '13800138050', status: '正常' },
  { workNo: 'SH051', name: '杨林', department: '产品中心', adAccount: 'yang.lin', mobile: '13800138051', status: '正常' },
  { workNo: 'SH052', name: '凌振', department: '销售中心', adAccount: 'ling.zhen', mobile: '13800138052', status: '正常' },
  { workNo: 'SH053', name: '蒋敬', department: '财务部', adAccount: 'jiang.jing', mobile: '13800138053', status: '正常' },
  { workNo: 'SH054', name: '吕方', department: '人事部', adAccount: 'lv.fang', mobile: '13800138054', status: '正常' },
  { workNo: 'SH055', name: '郭盛', department: '技术中心', adAccount: 'guo.sheng', mobile: '13800138055', status: '正常' }
])
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
  width: 100%;
}

.filters-row {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
  width: 100%;
}

.filter-bar .filter-item {
  display: flex !important;
  flex-direction: row !important;
  align-items: center !important;
  gap: 8px;
  min-width: 150px;
  flex-wrap: nowrap !important;
  min-width: 0;
  overflow: hidden;
}

.filter-bar .filter-item label {
  font-size: 13px;
  color: #64748b;
  font-weight: 500;
  white-space: nowrap;
  flex-shrink: 0;
  margin: 0;
  padding: 0;
}

.filter-bar .filter-item input {
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
  min-width: 120px;
  box-sizing: border-box;
}

.filter-bar .filter-item input:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.filter-bar .filter-item input::placeholder {
  color: #94a3b8;
}

.filter-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.action-row {
  display: flex;
  gap: 12px;
  align-items: center;
  justify-content: flex-start;
  width: 100%;
}

.icon {
  width: 16px;
  height: 16px;
  margin-right: 6px;
  vertical-align: middle;
}

.batch-revoke {
  background: transparent;
  border: 1px solid #ef4444;
  color: #ef4444;
  padding: 8px 16px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 13px;
  height: 36px;
}

.batch-revoke:hover:not(:disabled) {
  background: #fee2e2;
  border-color: #ef4444;
  color: #dc2626;
}

.batch-revoke:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  background: #f8fafc;
  border-color: #e5e7eb;
  color: #94a3b8;
}

.close-btn {
  background: transparent;
  border: 1px solid #f59e0b;
  color: #f59e0b;
  padding: 8px 16px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 13px;
  height: 36px;
}

.close-btn:hover {
  background: #fef3c7;
  border-color: #f59e0b;
  color: #d97706;
}

.action-row .primary {
  padding: 8px 14px;
  font-size: 12px;
  height: 36px;
}

.table-grid-roleusers {
  grid-template-columns: 50px 150px 200px 120px 150px 120px 130px 100px 150px;
}

.table-grid-selectusers {
  grid-template-columns: 50px 120px 150px 120px 1fr 100px 100px;
}

.pager-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.page-size-select {
  background: #ffffff;
  border: 1px solid #e5e7eb;
  color: #1e293b;
  padding: 6px 10px;
  border-radius: 6px;
  outline: none;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;
}

.page-size-select:hover {
  border-color: #cbd5e1;
}

.goto-page {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #64748b;
  font-size: 13px;
}

.goto-page input {
  background: #ffffff;
  border: 1px solid #e5e7eb;
  color: #1e293b;
  padding: 6px 10px;
  border-radius: 6px;
  outline: none;
  width: 60px;
  font-size: 13px;
  text-align: center;
  transition: all 0.2s;
}

.goto-page input:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.modal-content.large {
  width: 95vw;
  max-width: 1400px;
  max-height: 90vh;
  display: flex;
  flex-direction: column;
  background: #ffffff;
  border: 1px solid #e5e7eb;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
  border-radius: 10px;
}

.modal-content.large .modal-header {
  background: #ffffff;
  border-bottom: 1px solid #f1f5f9;
  padding: 18px 24px;
}

.modal-content.large .modal-title {
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
}

.modal-content.large .modal-body {
  padding: 24px;
  overflow-y: auto;
  flex: 1;
  background: #ffffff;
}

.modal-content.large .modal-footer {
  background: #ffffff;
  border-top: 1px solid #f1f5f9;
  padding: 16px 24px;
}

.add-user-filters {
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  padding: 20px;
  margin-bottom: 20px;
}

.filter-row {
  display: flex;
  gap: 20px;
  align-items: flex-end;
  margin-bottom: 0;
}

.filter-group {
  flex: 1;
  display: flex;
  gap: 16px;
  min-width: 0;
}

.add-user-filters .filter-item {
  flex: 1;
  display: flex !important;
  flex-direction: row !important;
  align-items: center !important;
  gap: 8px;
  flex-wrap: nowrap !important;
  min-width: 0;
  overflow: hidden;
}

.add-user-filters .filter-item label {
  font-size: 13px;
  color: #64748b;
  font-weight: 500;
  white-space: nowrap;
  flex-shrink: 0;
  margin: 0;
  padding: 0;
}

.add-user-filters .filter-item input,
.add-user-filters .filter-item select {
  background: #ffffff;
  border: 1px solid #e5e7eb;
  color: #1e293b;
  padding: 10px 12px;
  border-radius: 8px;
  outline: none;
  font-size: 13px;
  transition: all 0.2s;
  height: 40px;
  box-sizing: border-box;
  flex: 1;
  min-width: 0;
  width: 0;
  overflow: hidden;
}

.add-user-filters .filter-item input:focus,
.add-user-filters .filter-item select:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.add-user-filters .filter-item input::placeholder {
  color: #94a3b8;
}

.filter-actions {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
  flex-shrink: 0;
  align-items: center;
}

.filter-actions .primary,
.filter-actions .reset {
  padding: 8px 16px;
  font-size: 13px;
  height: 40px;
}

.add-user-content {
  display: flex;
  gap: 20px;
  min-height: 450px;
  max-height: 60vh;
}

.app-tree {
  width: 220px;
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  padding: 16px;
  flex-shrink: 0;
  overflow-y: auto;
}

.tree-title {
  font-size: 14px;
  color: #1e293b;
  margin-bottom: 16px;
  font-weight: 600;
  padding-bottom: 12px;
  border-bottom: 1px solid #f1f5f9;
}

.tree-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 12px;
  min-width: max-content;
  color: #1e293b;
  font-size: 13px;
  cursor: pointer;
  border-radius: 8px;
  transition: all 0.2s;
  margin-bottom: 4px;
}

.tree-item:hover {
  background: #f8fafc;
  color: #667eea;
}

.tree-item.active {
  background: #eef2ff;
  color: #667eea;
  font-weight: 500;
}

.tree-item-child {
  padding-left: 32px;
  position: relative;
}

.tree-item-child::before {
  content: '';
  position: absolute;
  left: 20px;
  top: 50%;
  width: 12px;
  height: 1px;
  background: #cbd5e1;
}

.tree-item-child::after {
  content: '';
  position: absolute;
  left: 20px;
  top: 0;
  width: 1px;
  height: 50%;
  background: #cbd5e1;
}

.tree-item-child:last-child::after {
  height: 25%;
}

.tree-toggle-placeholder {
  width: 16px;
  height: 16px;
  flex-shrink: 0;
}

.tree-toggle {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 2px;
  transition: transform 0.2s;
}

.tree-toggle:hover {
  transform: scale(1.1);
}

.tree-icon {
  width: 16px;
  height: 16px;
  color: #64748b;
  transition: color 0.2s;
}

.tree-item:hover .tree-icon {
  color: #667eea;
}

/* 展开/折叠图标样式 - 参考菜单列表 */
.expand-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 20px;
  height: 20px;
  cursor: pointer;
  transition: all 0.2s;
  margin-right: 6px;
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
  margin-right: 6px;
}

/* 自定义滚动条样式 - 确保滚动条可见且不遮挡内容 */
.dept-tree-scroll::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

.dept-tree-scroll::-webkit-scrollbar-track {
  background: #f1f5f9;
  border-radius: 4px;
}

.dept-tree-scroll::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 4px;
}

.dept-tree-scroll::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}

/* 确保部门树项目可以横向滚动 */
.dept-tree-scroll .tree-item {
  min-width: max-content;
}

.user-list {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
  overflow: hidden;
  width: 100%;
}

.user-table-card {
  flex: 1;
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  margin-bottom: 16px;
  display: flex;
  flex-direction: column;
  min-height: 0;
  width: 100%;
}

.user-table-card .table-head {
  background: #f8fafc;
  border-bottom: 1px solid #f1f5f9;
}

.user-table-card .table-row {
  border-bottom: 1px solid #f1f5f9;
  transition: background 0.2s;
}

.user-table-card .table-row:hover {
  background: #f8fafc;
}

.user-table-card .table-row:last-child {
  border-bottom: none;
}

.user-table-card .email-cell {
  word-break: break-all;
  white-space: normal;
  overflow: visible;
  min-width: 0;
  line-height: 1.5;
}

.search-input {
  display: flex;
  align-items: center;
  gap: 8px;
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 0 12px;
  height: 40px;
  box-sizing: border-box;
  transition: all 0.2s;
}

.search-input:focus-within {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.search-icon {
  width: 16px;
  height: 16px;
  color: #94a3b8;
  flex-shrink: 0;
}

.search-input input {
  background: transparent;
  border: none;
  outline: none;
  color: #1e293b;
  flex: 1;
  font-size: 13px;
  padding: 0;
  height: 100%;
}

.search-input input::placeholder {
  color: #94a3b8;
}

.empty-table-row {
  grid-column: 1 / -1;
  padding: 0;
}

.action-btn.add-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  color: #ffffff;
  padding: 6px 12px;
  border-radius: 6px;
  font-size: 13px;
  transition: all 0.2s;
  box-shadow: 0 2px 4px rgba(102, 126, 234, 0.2);
  cursor: pointer;
}

.action-btn.add-btn:hover {
  box-shadow: 0 4px 8px rgba(102, 126, 234, 0.3);
  transform: translateY(-1px);
}

.action-btn.add-btn:active {
  transform: translateY(0);
  box-shadow: 0 1px 2px rgba(102, 126, 234, 0.2);
}

/* 按部门分配弹窗样式 - 穿梭框 */
.modal-content.transfer-modal {
  width: 900px;
  max-width: 95vw;
}

.transfer-body {
  padding: 20px;
}

.transfer-container {
  display: flex;
  gap: 20px;
  height: 500px;
}

.transfer-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  overflow: hidden;
}

.transfer-panel-header {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  background: #f8fafc;
  border-bottom: 1px solid #e5e7eb;
  font-size: 14px;
  font-weight: 600;
  color: #1e293b;
}

.transfer-panel-header input[type="checkbox"] {
  width: 16px;
  height: 16px;
  cursor: pointer;
}

.transfer-panel-search {
  padding: 12px 16px;
  border-bottom: 1px solid #e5e7eb;
}

.transfer-panel-search input {
  width: 100%;
  height: 36px;
  padding: 8px 12px;
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  outline: none;
  font-size: 13px;
  transition: all 0.2s;
  box-sizing: border-box;
}

.transfer-panel-search input:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.transfer-panel-list {
  flex: 1;
  overflow-y: auto;
  padding: 8px;
}

.transfer-empty {
  text-align: center;
  padding: 40px 20px;
  color: #94a3b8;
  font-size: 13px;
}

.transfer-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 12px;
  margin-bottom: 4px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
  user-select: none;
}

.transfer-item:hover:not(.disabled) {
  background: #f8fafc;
}

.transfer-item.selected:not(.disabled) {
  background: #eef2ff;
}

.transfer-item.disabled {
  opacity: 0.5;
  cursor: not-allowed;
  background: #f1f5f9;
}

.transfer-item.disabled:hover {
  background: #f1f5f9;
}

.transfer-item-child {
  padding-left: 32px;
  position: relative;
}

.transfer-item-child::before {
  content: '';
  position: absolute;
  left: 20px;
  top: 50%;
  width: 12px;
  height: 1px;
  background: #cbd5e1;
}

.transfer-item-child::after {
  content: '';
  position: absolute;
  left: 20px;
  top: 0;
  width: 1px;
  height: 50%;
  background: #cbd5e1;
}

.transfer-item-child:last-child::after {
  height: 25%;
}

.transfer-item input[type="checkbox"] {
  width: 16px;
  height: 16px;
  cursor: pointer;
  flex-shrink: 0;
  pointer-events: auto;
}

.transfer-item input[type="checkbox"]:disabled {
  cursor: not-allowed;
  pointer-events: none;
}

.transfer-item-label {
  flex: 1;
  font-size: 13px;
  color: #1e293b;
  display: flex;
  align-items: center;
  gap: 6px;
}

.transfer-item-label.disabled {
  color: #94a3b8;
}

.transfer-item-count {
  font-size: 12px;
  color: #64748b;
}

.tree-toggle {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 2px;
  transition: transform 0.2s;
  flex-shrink: 0;
}

.tree-toggle:hover {
  transform: scale(1.1);
}

.tree-icon {
  width: 14px;
  height: 14px;
  color: #64748b;
  transition: color 0.2s;
}

.transfer-item:hover .tree-icon {
  color: #667eea;
}

.transfer-actions {
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 12px;
  padding: 20px 0;
}

.transfer-btn {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 8px;
  color: #ffffff;
  cursor: pointer;
  transition: all 0.2s;
  box-shadow: 0 2px 4px rgba(102, 126, 234, 0.2);
}

.transfer-btn:hover:not(:disabled) {
  box-shadow: 0 4px 8px rgba(102, 126, 234, 0.3);
  transform: translateY(-1px);
}

.transfer-btn:active:not(:disabled) {
  transform: translateY(0);
}

.transfer-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
  background: #cbd5e1;
}

.transfer-btn svg {
  width: 20px;
  height: 20px;
}

/* Tab切换样式 */
.role-tabs {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
  border-bottom: 2px solid #e5e7eb;
}

.role-tab-item {
  padding: 12px 24px;
  font-size: 14px;
  font-weight: 500;
  color: #64748b;
  cursor: pointer;
  border-bottom: 2px solid transparent;
  margin-bottom: -2px;
  transition: all 0.2s;
  position: relative;
}

.role-tab-item:hover {
  color: #667eea;
}

.role-tab-item.active {
  color: #667eea;
  border-bottom-color: #667eea;
}

.tab-content {
  animation: fadeIn 0.2s;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

/* 部门表格样式 */
.table-grid-departments {
  display: grid;
  grid-template-columns: 50px 150px 200px 200px 120px 150px 150px;
  gap: 0;
}

/* 已添加用户行样式 */
:deep(.user-added-row) {
  background-color: #f5f5f5 !important;
  opacity: 0.6;
}

:deep(.user-added-row td) {
  color: #999 !important;
}

:deep(.user-added-row .ant-checkbox-wrapper) {
  cursor: not-allowed;
}

:deep(.user-added-row .ant-checkbox-input) {
  cursor: not-allowed;
}
</style>

