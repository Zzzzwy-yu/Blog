<template>
  <div class="admin-content">
    <div class="page-header">
      <h2>用户管理</h2>
      <p>管理注册用户信息</p>
    </div>

    <el-card>
      <el-table :data="userList" border style="width:100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="头像" width="80">
          <template #default="{ row }">
            <span class="user-avatar">{{ getInitial(row.nickname || row.username) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="username" label="用户名" min-width="120" />
        <el-table-column prop="nickname" label="昵称" min-width="120" />
        <el-table-column prop="email" label="邮箱" min-width="180" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="bio" label="简介" min-width="200" show-overflow-tooltip />
        <el-table-column label="角色" width="80">
          <template #default="{ row }">
            <el-tag :type="row.role === 2 ? 'danger' : 'success'">
              {{ row.role === 2 ? '管理员' : '普通用户' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              :active-value="1"
              :inactive-value="0"
              :disabled="row.role === 2"
              @change="handleStatusChange(row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="注册时间" width="180" />
        <el-table-column prop="updateTime" label="更新时间" width="180" />
      </el-table>

      <div style="margin-top:16px;text-align:right;">
        <el-pagination
          v-model:current-page="pageNum"
          :page-size="pageSize"
          :total="total"
          layout="prev, pager, next, total"
          @current-change="fetchList"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getAdminUserList, updateUserStatus } from '@/api'
import { ElMessage, ElMessageBox } from 'element-plus'

const userList = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const getInitial = name => {
  if (!name) return 'U'
  return name.charAt(0).toUpperCase()
}

const fetchList = () => {
  getAdminUserList({ pageNum: pageNum.value, pageSize: pageSize.value }).then(res => {
    userList.value = res?.list || []
    total.value = res?.total || 0
  })
}

const handleStatusChange = row => {
  const action = row.status === 1 ? '启用' : '禁用'
  ElMessageBox.confirm(`确认${action}用户 ${row.username} 吗?`, '提示', {
    type: 'warning'
  }).then(() => {
    updateUserStatus(row.id, row.status).then(() => {
      ElMessage.success(`${action}成功`)
      fetchList()
    }).catch(() => {
      row.status = row.status === 1 ? 0 : 1
    })
  }).catch(() => {
    row.status = row.status === 1 ? 0 : 1
  })
}

onMounted(() => {
  fetchList()
})
</script>

<style scoped>
.admin-content {
  padding: 20px;
}
.page-header {
  margin-bottom: 20px;
}
.page-header h2 {
  margin: 0;
  font-size: 20px;
  color: #303133;
}
.page-header p {
  margin: 6px 0 0;
  color: #909399;
  font-size: 14px;
}
.user-avatar {
  display: inline-flex;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  font-size: 16px;
  font-weight: 600;
  align-items: center;
  justify-content: center;
}
</style>
