<template>
  <div class="api-documentation">
      <div class="doc-header">
      <h1>对外接口调用说明文档</h1>
      <a-button type="primary" @click="downloadPDF" :loading="downloading">
        <template #icon>
          <DownloadOutlined />
        </template>
        下载PDF
      </a-button>
    </div>

    <div class="doc-content" id="doc-content">
      <h2>概述</h2>
      <p>本系统提供对外接口，允许外部系统通过 <code>clientId</code> 和 <code>clientSecret</code> 获取访问令牌（Token），然后使用 Token 进行以下操作：</p>
      <ul>
        <li>查询用户在指定应用下的菜单权限</li>
        <li>批量推送部门数据</li>
        <li>批量推送用户数据</li>
      </ul>
      <p><strong>注意：</strong>系统提供两种Token获取方式：</p>
      <ul>
        <li><strong>应用Token</strong>：使用 <code>applications</code> 表的 <code>clientId</code> 和 <code>clientSecret</code>，用于查询菜单权限</li>
        <li><strong>消费者Token</strong>：使用 <code>consumer_info</code> 表的 <code>clientId</code> 和 <code>clientSecret</code>，用于数据推送接口</li>
      </ul>

      <h2>流程一：查询菜单权限</h2>
      <p>本流程用于查询用户在指定应用下的菜单权限，需要使用应用Token进行认证。</p>

      <h3>1. 获取应用Token</h3>
      <p><strong>接口地址：</strong> <code>POST /one-permission/api/external/token</code></p>
      <p><strong>说明：</strong>使用 <code>applications</code> 表的 <code>clientId</code> 和 <code>clientSecret</code> 获取Token，用于查询菜单权限接口。</p>
      
      <p><strong>请求头：</strong></p>
      <pre><code>Content-Type: application/json</code></pre>

      <p><strong>请求体：</strong></p>
      <pre><code>{
  "clientId": "your_client_id",
  "clientSecret": "your_client_secret"
}</code></pre>

      <p><strong>响应示例：</strong></p>
      <pre><code>{
  "code": 200,
  "message": "获取token成功",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 7200,
    "appId": 1,
    "appName": "CRM系统"
  }
}</code></pre>

      <p><strong>错误响应：</strong></p>
      <pre><code>{
  "code": 500,
  "message": "clientId不存在或已禁用"
}</code></pre>

      <h3>2. 查询菜单权限</h3>
      <p><strong>接口地址：</strong> <code>POST /one-permission/api/external/menu-permissions</code></p>
      <p><strong>说明：</strong>根据应用ID和工号查询用户在指定应用下的菜单权限列表（树形结构）。</p>
      <p><strong>重要提示：</strong>查询的 <code>appId</code> 必须与Token关联的应用ID一致，否则返回"无权调用此应用权限"。</p>

      <p><strong>请求头：</strong></p>
      <pre><code>Content-Type: application/json
Authorization: Bearer {accessToken}</code></pre>

      <p><strong>请求体：</strong></p>
      <pre><code>{
  "appId": 1,
  "workNo": "W001"
}</code></pre>

      <p><strong>请求参数说明：</strong></p>
      <ul>
        <li><code>appId</code>：应用ID（必填，必须与Token关联的应用ID一致）</li>
        <li><code>workNo</code>：工号（必填）</li>
      </ul>

      <p><strong>响应示例：</strong></p>
      <pre><code>{
  "code": 200,
  "message": "查询成功",
  "data": [
    {
      "id": 1,
      "name": "系统管理",
      "menuKey": "system",
      "parentId": null,
      "menuType": "目录",
      "icon": "setting",
      "sort": 1,
      "isExternal": false,
      "route": "/system",
      "component": "System/index",
      "permission": "system:view",
      "displayStatus": "显示",
      "status": "正常",
      "embeddedUrl": null,
      "children": [
        {
          "id": 2,
          "name": "用户管理",
          "menuKey": "user",
          "parentId": 1,
          "menuType": "菜单",
          "icon": "user",
          "sort": 1,
          "isExternal": false,
          "route": "/system/user",
          "component": "System/User/index",
          "permission": "system:user:view",
          "displayStatus": "显示",
          "status": "正常",
          "embeddedUrl": null,
          "children": null
        }
      ]
    }
  ]
}</code></pre>

      <p><strong>错误响应：</strong></p>
      <pre><code>{
  "code": 401,
  "message": "Token无效或已过期"
}</code></pre>
      <pre><code>{
  "code": 500,
  "message": "无权调用此应用权限"
}</code></pre>

      <h3>3. 验证Token（可选）</h3>
      <p><strong>接口地址：</strong> <code>GET /one-permission/api/external/validate-token</code></p>
      <p><strong>说明：</strong>验证应用Token是否有效，可在调用其他接口前先验证Token。</p>

      <p><strong>请求头：</strong></p>
      <pre><code>Authorization: Bearer {accessToken}</code></pre>

      <p><strong>响应示例：</strong></p>
      <pre><code>{
  "code": 200,
  "message": "Token有效",
  "data": true
}</code></pre>

      <h2>流程二：推送数据（部门/用户）</h2>
      <p>本流程用于批量推送部门数据和用户数据，需要使用消费者Token进行认证。</p>

      <h3>1. 获取消费者Token</h3>
      <p><strong>接口地址：</strong> <code>POST /one-permission/api/external/consumer/token</code></p>
      <p><strong>说明：</strong>使用 <code>consumer_info</code> 表的 <code>clientId</code> 和 <code>clientSecret</code> 获取Token，用于数据推送接口。</p>
      
      <p><strong>请求头：</strong></p>
      <pre><code>Content-Type: application/json</code></pre>

      <p><strong>请求体：</strong></p>
      <pre><code>{
  "clientId": "your_consumer_client_id",
  "clientSecret": "your_consumer_client_secret"
}</code></pre>

      <p><strong>响应示例：</strong></p>
      <pre><code>{
  "code": 200,
  "message": "获取token成功",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 7200,
    "consumerName": "测试消费者"
  }
}</code></pre>

      <p><strong>错误响应：</strong></p>
      <pre><code>{
  "code": 500,
  "message": "clientId不存在或已禁用"
}</code></pre>

      <h3>2. 批量推送部门数据</h3>
      <p><strong>接口地址：</strong> <code>POST /one-permission/api/external/departments</code></p>
      <p><strong>说明：</strong>一次性批量推送所有部门数据，支持创建和更新。系统会自动处理部门层级关系，确保父部门先于子部门创建。</p>
      <p><strong>安全限制：</strong></p>
      <ul>
        <li>部门层级最多支持10级</li>
        <li>系统会自动检测并防止循环引用</li>
        <li>部门不能引用自身作为父部门</li>
      </ul>

      <p><strong>请求头：</strong></p>
      <pre><code>Content-Type: application/json
Authorization: Bearer {consumerToken}</code></pre>

      <p><strong>请求体：</strong></p>
      <pre><code>[
  {
    "id": 1,
    "name": "技术部",
    "parentId": null,
    "code": "TECH",
    "level": 1,
    "sort": 1,
    "leaderWorkNo": "W001",
    "status": "正常",
    "remark": "技术部门"
  },
  {
    "id": 2,
    "name": "前端组",
    "parentId": 1,
    "code": "FRONTEND",
    "level": 2,
    "sort": 1,
    "status": "正常"
  }
]</code></pre>

      <p><strong>请求参数说明：</strong></p>
      <ul>
        <li><code>id</code>：部门ID（可选，如果提供则使用该ID创建或更新部门，否则由数据库自动生成）</li>
        <li><code>name</code>：部门名称（必填）</li>
        <li><code>parentId</code>：父部门ID，NULL表示顶级部门（可选）</li>
        <li><code>code</code>：部门编码（可选）</li>
        <li><code>level</code>：部门层级，1为顶级（可选）</li>
        <li><code>sort</code>：显示排序（可选）</li>
        <li><code>leaderWorkNo</code>：部门负责人工号（可选）</li>
        <li><code>status</code>：状态，正常/禁用（可选，默认"正常"）</li>
        <li><code>remark</code>：备注（可选）</li>
      </ul>

      <p><strong>部门ID说明：</strong></p>
      <ul>
        <li>如果提供了 <code>id</code> 字段，系统会优先根据ID查找部门：
          <ul>
            <li>如果ID存在，则更新该部门（即使名称不匹配也会更新）</li>
            <li>如果ID不存在，则使用该ID创建新部门</li>
          </ul>
        </li>
        <li>如果未提供 <code>id</code> 字段，系统会根据部门名称查找：
          <ul>
            <li>如果名称存在，则更新该部门</li>
            <li>如果名称不存在，则创建新部门（ID由数据库自动生成）</li>
          </ul>
        </li>
        <li>如果同时提供了 <code>id</code> 和 <code>name</code>，但ID对应的部门名称与提供的名称不一致，系统会更新部门名称</li>
      </ul>

      <p><strong>响应示例：</strong></p>
      <pre><code>{
  "code": 200,
  "message": "推送部门数据完成",
  "data": {
    "successCount": 2,
    "failCount": 0,
    "totalCount": 2,
    "successIds": [1, 2],
    "errors": []
  }
}</code></pre>

      <p><strong>错误响应示例：</strong></p>
      <pre><code>{
  "code": 200,
  "message": "推送部门数据完成",
  "data": {
    "successCount": 1,
    "failCount": 1,
    "totalCount": 2,
    "successIds": [1],
    "errors": [
      {
        "departmentName": "无效部门",
        "errorMessage": "部门层级不能超过 10 级"
      }
    ]
  }
}</code></pre>

      <h3>3. 批量推送用户数据</h3>
      <p><strong>接口地址：</strong> <code>POST /one-permission/api/external/normal-users</code></p>
      <p><strong>说明：</strong>批量推送普通用户数据，支持创建和更新。系统根据工号查询用户，如果存在则更新，不存在则创建。</p>
      <p><strong>重要提示：</strong></p>
      <ul>
        <li>工号不能为空，是必填字段</li>
        <li>系统根据工号查询用户，存在则更新，不存在则创建</li>
        <li>如果提供了部门ID，则必须确保该部门存在，否则会返回错误</li>
      </ul>

      <p><strong>请求头：</strong></p>
      <pre><code>Content-Type: application/json
Authorization: Bearer {consumerToken}</code></pre>

      <p><strong>请求体：</strong></p>
      <pre><code>[
  {
    "workNo": "W001",
    "name": "张三",
    "departmentId": 1,
    "adAccount": "zhangsan",
    "mobile": "13800138000",
    "email": "zhangsan@example.com",
    "nickname": "小张",
    "level": "P5",
    "status": "正常"
  },
  {
    "workNo": "W002",
    "name": "李四",
    "departmentId": 2,
    "mobile": "13900139000",
    "status": "正常"
  }
]</code></pre>

      <p><strong>请求参数说明：</strong></p>
      <ul>
        <li><code>workNo</code>：工号（<strong>必填</strong>，不能为空，系统根据工号查询用户，存在则更新，不存在则创建）</li>
        <li><code>name</code>：姓名（必填）</li>
        <li><code>departmentId</code>：主部门ID（可选，如果提供则必须存在，否则会返回"部门ID不存在"错误）</li>
        <li><code>adAccount</code>：AD域账号（可选）</li>
        <li><code>mobile</code>：手机号码（可选）</li>
        <li><code>email</code>：邮箱（可选）</li>
        <li><code>nickname</code>：用户昵称（可选）</li>
        <li><code>level</code>：职级（可选）</li>
        <li><code>status</code>：状态，正常/禁用（可选，默认"正常"）</li>
      </ul>

      <p><strong>用户更新/创建逻辑：</strong></p>
      <ul>
        <li>系统根据工号（<code>workNo</code>）查询用户是否存在</li>
        <li>如果用户存在：更新用户信息（保留原有的ID和创建时间）</li>
        <li>如果用户不存在：创建新用户（ID由数据库自动生成）</li>
        <li>工号会自动去除前后空格</li>
      </ul>

      <p><strong>响应示例：</strong></p>
      <pre><code>{
  "code": 200,
  "message": "推送用户数据完成",
  "data": {
    "successCount": 2,
    "failCount": 0,
    "totalCount": 2,
    "successIds": [1, 2],
    "errors": []
  }
}</code></pre>

      <p><strong>错误响应示例：</strong></p>
      <pre><code>{
  "code": 200,
  "message": "推送用户数据完成",
  "data": {
    "successCount": 1,
    "failCount": 1,
    "totalCount": 2,
    "successIds": [1],
    "errors": [
      {
        "workNo": "W003",
        "errorMessage": "工号不能为空"
      }
    ]
  }
}</code></pre>
      <pre><code>{
  "code": 200,
  "message": "推送用户数据完成",
  "data": {
    "successCount": 1,
    "failCount": 1,
    "totalCount": 2,
    "successIds": [1],
    "errors": [
      {
        "workNo": "W003",
        "errorMessage": "部门ID 999 不存在"
      }
    ]
  }
}</code></pre>

      <h2>Token管理</h2>
      <ul>
        <li>Token有效期为2小时（7200秒）</li>
        <li>可以通过 <code>/api/external/validate-token</code> 接口验证应用Token是否有效</li>
        <li>Token过期后需要重新获取</li>
        <li>应用Token和消费者Token是独立的，不能混用</li>
        <li>应用Token仅用于查询菜单权限接口</li>
        <li>消费者Token仅用于数据推送接口</li>
      </ul>

      <h2>权限说明</h2>
      <ul>
        <li>系统会根据用户在应用下的角色分配情况，返回该用户有权限访问的菜单</li>
        <li>只返回状态为"正常"且显示状态为"显示"的菜单</li>
        <li>返回的菜单按 <code>sort</code> 字段排序</li>
        <li>返回的菜单结构为树形结构，包含父子关系</li>
      </ul>

      <h2>错误码说明</h2>
      <ul>
        <li><code>200</code>: 成功（注意：批量推送接口即使部分失败也会返回200，需要通过响应体中的 <code>failCount</code> 判断）</li>
        <li><code>401</code>: Token无效或已过期、未携带Token</li>
        <li><code>500</code>: 业务错误（如：clientId不存在、应用不存在、无权调用此应用权限等）</li>
      </ul>

      <h2>注意事项</h2>
      <ol>
        <li><strong>Token类型区分</strong>
          <ul>
            <li>应用Token：使用 <code>applications</code> 表的 <code>clientId</code> 和 <code>clientSecret</code>，仅用于查询菜单权限</li>
            <li>消费者Token：使用 <code>consumer_info</code> 表的 <code>clientId</code> 和 <code>clientSecret</code>，仅用于数据推送接口</li>
            <li>两种Token不能混用，必须使用对应的Token调用对应的接口</li>
          </ul>
        </li>
        <li><strong>凭证配置</strong>
          <ul>
            <li>应用凭证：在"应用管理"页面配置，系统会自动生成 <code>clientId</code> 和 <code>clientSecret</code></li>
            <li>消费者凭证：在"消费者管理"页面配置，需要手动设置 <code>clientId</code> 和 <code>clientSecret</code></li>
          </ul>
        </li>
        <li><strong>数据推送注意事项</strong>
          <ul>
            <li><strong>部门推送：</strong>
              <ul>
                <li>建议一次性推送所有部门数据，系统会自动处理层级关系</li>
                <li>支持通过 <code>id</code> 字段指定部门ID，如果提供则使用该ID，否则由数据库自动生成</li>
                <li>系统会优先根据ID查找部门，如果ID不存在则根据名称查找</li>
                <li>部门层级限制：最多支持10级，超过会返回错误</li>
                <li>循环引用保护：系统会自动检测并防止部门循环引用</li>
                <li>所有部门插入/更新完成后，系统会统一更新 <code>ancestors</code> 字段（从顶级部门开始，逐层向下）</li>
              </ul>
            </li>
            <li><strong>用户推送：</strong>
              <ul>
                <li>支持批量推送，根据工号自动判断创建或更新</li>
                <li><strong>工号不能为空</strong>，是必填字段</li>
                <li>系统根据工号查询用户，存在则更新，不存在则创建</li>
                <li>如果提供了部门ID，则必须确保该部门存在，否则会返回错误</li>
                <li>工号会自动去除前后空格</li>
              </ul>
            </li>
            <li><strong>批量推送结果：</strong>
              <ul>
                <li>即使部分数据失败，也会返回成功的数据ID和详细的错误信息</li>
                <li>可以通过响应体中的 <code>successCount</code> 和 <code>failCount</code> 判断成功和失败的数量</li>
                <li>失败的记录会在 <code>errors</code> 数组中返回详细的错误信息</li>
              </ul>
            </li>
          </ul>
        </li>
        <li><strong>安全建议</strong>
          <ul>
            <li>Token需要妥善保管，避免泄露</li>
            <li>建议在Token即将过期时提前刷新</li>
            <li>所有接口都使用JSON格式进行数据交换</li>
            <li>建议使用HTTPS协议传输敏感数据</li>
          </ul>
        </li>
      </ol>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { DownloadOutlined } from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'
import html2pdf from 'html2pdf.js'

const downloading = ref(false)

const downloadPDF = async () => {
  downloading.value = true
  try {
    const element = document.getElementById('doc-content')
    const opt = {
      margin: [10, 10, 10, 10],
      filename: '对外接口调用说明文档.pdf',
      image: { type: 'jpeg', quality: 0.98 },
      html2canvas: { scale: 2, useCORS: true },
      jsPDF: { unit: 'mm', format: 'a4', orientation: 'portrait' }
    }
    
    await html2pdf().set(opt).from(element).save()
    message.success('PDF下载成功')
  } catch (error) {
    console.error('PDF下载失败:', error)
    message.error('PDF下载失败，请重试')
  } finally {
    downloading.value = false
  }
}
</script>

<style scoped>
.api-documentation {
  padding: 24px;
  background: var(--bg-secondary, #ffffff);
  min-height: 100%;
}

.doc-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 2px solid var(--border-color, #e8e8e8);
}

.doc-header h1 {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  color: var(--text-primary, #333);
}

.doc-content {
  background: var(--bg-primary, #ffffff);
  padding: 32px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  line-height: 1.8;
  color: var(--text-primary, #333);
}

.doc-content h2 {
  margin-top: 32px;
  margin-bottom: 16px;
  font-size: 20px;
  font-weight: 600;
  color: var(--text-primary, #333);
  border-bottom: 2px solid var(--ant-color-primary, #1890ff);
  padding-bottom: 8px;
}

.doc-content h2:first-child {
  margin-top: 0;
}

.doc-content h3 {
  margin-top: 24px;
  margin-bottom: 12px;
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary, #333);
}

.doc-content p {
  margin-bottom: 12px;
  color: var(--text-secondary, #666);
}

.doc-content code {
  background: var(--code-bg, #f5f5f5);
  padding: 2px 6px;
  border-radius: 4px;
  font-family: 'Courier New', monospace;
  font-size: 14px;
  color: var(--code-color, #e83e8c);
}

.doc-content pre {
  background: var(--code-bg, #f5f5f5);
  padding: 16px;
  border-radius: 6px;
  overflow-x: auto;
  margin-bottom: 16px;
  border-left: 4px solid var(--ant-color-primary, #1890ff);
}

.doc-content pre code {
  background: none;
  padding: 0;
  color: var(--text-primary, #333);
  font-size: 13px;
  line-height: 1.6;
}

.doc-content ul,
.doc-content ol {
  margin-bottom: 16px;
  padding-left: 24px;
}

.doc-content li {
  margin-bottom: 8px;
  color: var(--text-secondary, #666);
}

.doc-content strong {
  color: var(--text-primary, #333);
  font-weight: 600;
}
</style>

