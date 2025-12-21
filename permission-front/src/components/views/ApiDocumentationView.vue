<template>
  <div class="api-documentation">
    <div class="doc-header">
      <h1>获取用户应用菜单权限接口调用说明文档</h1>
      <a-button type="primary" @click="downloadPDF" :loading="downloading">
        <template #icon>
          <DownloadOutlined />
        </template>
        下载PDF
      </a-button>
    </div>

    <div class="doc-content" id="doc-content">
      <h2>概述</h2>
      <p>本系统提供对外接口，允许外部系统通过 <code>clientId</code> 和 <code>clientSecret</code> 获取访问令牌（Token），然后使用 Token 查询用户在指定应用下的菜单权限。</p>

      <h2>接口列表</h2>

      <h3>1. 获取访问令牌</h3>
      <p><strong>接口地址：</strong> <code>POST /one-permission/api/external/token</code></p>
      
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
    "consumerName": "测试消费者"
  }
}</code></pre>

      <p><strong>错误响应：</strong></p>
      <pre><code>{
  "code": 500,
  "message": "clientId不存在或已禁用"
}</code></pre>

      <h3>2. 查询菜单权限</h3>
      <p><strong>接口地址：</strong> <code>POST /one-permission/api/external/menu-permissions</code></p>

      <p><strong>请求头：</strong></p>
      <pre><code>Content-Type: application/json
Authorization: Bearer {accessToken}</code></pre>

      <p><strong>请求体：</strong></p>
      <pre><code>{
  "appId": 1,
  "workNo": "W001"
}</code></pre>

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

      <h3>3. 验证Token</h3>
      <p><strong>接口地址：</strong> <code>GET /one-permission/api/external/validate-token</code></p>

      <p><strong>请求头：</strong></p>
      <pre><code>Authorization: Bearer {accessToken}</code></pre>

      <p><strong>响应示例：</strong></p>
      <pre><code>{
  "code": 200,
  "message": "Token有效",
  "data": true
}</code></pre>

      <h2>使用流程</h2>
      <ol>
        <li><strong>获取Token</strong>
          <ul>
            <li>使用 <code>clientId</code> 和 <code>clientSecret</code> 调用 <code>/api/external/token</code> 接口</li>
            <li>获取 <code>accessToken</code></li>
          </ul>
        </li>
        <li><strong>查询权限</strong>
          <ul>
            <li>在请求头中携带 <code>Authorization: Bearer {accessToken}</code></li>
            <li>调用 <code>/api/external/menu-permissions</code> 接口</li>
            <li>传入 <code>appId</code> 和 <code>workNo</code>（工号）</li>
            <li>获取该用户在指定应用下的菜单权限列表（树形结构）</li>
          </ul>
        </li>
        <li><strong>Token管理</strong>
          <ul>
            <li>Token有效期为2小时（7200秒）</li>
            <li>可以通过 <code>/api/external/validate-token</code> 接口验证Token是否有效</li>
            <li>Token过期后需要重新获取</li>
          </ul>
        </li>
      </ol>

      <h2>权限说明</h2>
      <ul>
        <li>系统会根据用户在应用下的角色分配情况，返回该用户有权限访问的菜单</li>
        <li>只返回状态为"正常"且显示状态为"显示"的菜单</li>
        <li>返回的菜单按 <code>sort</code> 字段排序</li>
        <li>返回的菜单结构为树形结构，包含父子关系</li>
      </ul>

      <h2>错误码说明</h2>
      <ul>
        <li><code>200</code>: 成功</li>
        <li><code>401</code>: Token无效或已过期</li>
        <li><code>500</code>: 业务错误（如：clientId不存在、应用不存在等）</li>
      </ul>

      <h2>注意事项</h2>
      <ol>
        <li><code>clientId</code> 和 <code>clientSecret</code> 需要在系统中预先配置（通过消费者管理页面）</li>
        <li>Token需要妥善保管，避免泄露</li>
        <li>建议在Token即将过期时提前刷新</li>
        <li>所有接口都使用JSON格式进行数据交换</li>
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
      filename: '获取用户应用菜单权限接口调用说明文档.pdf',
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

