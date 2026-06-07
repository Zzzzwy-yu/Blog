<template>
  <div>
    <el-card>
      <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:16px;">
        <div style="display:flex;gap:10px;">
          <el-input v-model="keyword" placeholder="搜索标题" style="width:220px;" clearable @keyup.enter="fetchList">
            <template #prefix><el-icon><Search /></el-icon></template>
          </el-input>
          <el-select v-model="statusFilter" placeholder="全部状态" style="width:140px;" @change="fetchList">
            <el-option label="全部" :value="null" />
            <el-option label="已上架" :value="1" />
            <el-option label="已下架" :value="0" />
          </el-select>
        </div>
        <el-button type="primary" @click="$router.push('/admin/article/edit')"><el-icon><Plus /></el-icon> 新建文章</el-button>
      </div>
      <el-table :data="list" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="title" label="标题" min-width="220">
          <template #default="{row}">
            <el-link type="primary" @click="$router.push('/admin/article/edit?id=' + row.id)">{{ row.title }}</el-link>
          </template>
        </el-table-column>
        <el-table-column prop="categoryName" label="分类" width="120" />
        <el-table-column prop="author" label="作者" width="100" />
        <el-table-column label="浏览/评论" width="120">
          <template #default="{row}">
            👁 {{ row.viewCount || 0 }} / 💬 {{ row.commentCount || 0 }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{row}">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '上架' : '下架' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{row}">
            <el-button size="small" @click="$router.push('/admin/article/edit?id=' + row.id)">编辑</el-button>
            <el-button size="small" :type="row.status === 1 ? 'warning' : 'success'" @click="toggleStatus(row)">
              {{ row.status === 1 ? '下架' : '上架' }}
            </el-button>
            <el-button size="small" type="danger" @click="remove(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="pageNum"
          :page-size="pageSize"
          :total="total"
          layout="total, prev, pager, next, jumper"
          @current-change="fetchList"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getAdminArticleList, switchArticleStatus, deleteArticle } from '@/api'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const loading = ref(false)
const list = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const keyword = ref('')
const statusFilter = ref(null)

const fetchList = () => {
  loading.value = true
  getAdminArticleList({
    pageNum: pageNum.value,
    pageSize: pageSize.value,
    keyword: keyword.value,
    status: statusFilter.value
  }).then(res => {
    list.value = res?.list || []
    total.value = res?.total || 0
  }).finally(() => {
    loading.value = false
  })
}

const toggleStatus = row => {
  const next = row.status === 1 ? 0 : 1
  switchArticleStatus(row.id, next).then(() => {
    ElMessage.success('操作成功')
    fetchList()
  })
}

const remove = row => {
  ElMessageBox.confirm('确认删除文章《' + row.title + '》?', '提示', { type: 'warning' })
    .then(() => {
      deleteArticle(row.id).then(() => {
        ElMessage.success('已删除')
        fetchList()
      })
    }).catch(() => {})
}

onMounted(fetchList)
</script>

<style scoped>
.pagination-wrap { display:flex; justify-content:flex-end; margin-top:16px; }
</style>
