<template>
  <el-card>
    <template #header>
      <div style="display:flex;justify-content:space-between;align-items:center;">
        <strong>留言/评论管理</strong>
      </div>
    </template>
    <el-table :data="list" v-loading="loading" stripe>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="nickname" label="昵称" width="120" />
      <el-table-column prop="email" label="邮箱" width="180" />
      <el-table-column prop="articleTitle" label="所属文章" min-width="180">
        <template #default="{row}">
          <el-tag v-if="row.articleId" type="info" size="small">评论</el-tag>
          <el-tag v-else type="warning" size="small">留言</el-tag>
          <span style="margin-left:6px;">{{ row.articleTitle || '(留言板)' }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="content" label="内容" min-width="260" show-overflow-tooltip />
      <el-table-column prop="ip" label="IP" width="130" />
      <el-table-column prop="createTime" label="时间" width="170" />
      <el-table-column label="操作" width="100" fixed="right">
        <template #default="{row}">
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
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getAdminCommentList, deleteComment } from '@/api'
import { ElMessage, ElMessageBox } from 'element-plus'

const list = ref([])
const loading = ref(false)
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const fetchList = () => {
  loading.value = true
  getAdminCommentList({
    pageNum: pageNum.value,
    pageSize: pageSize.value
  }).then(res => {
    list.value = res?.list || []
    total.value = res?.total || 0
  }).finally(() => {
    loading.value = false
  })
}

const remove = row => {
  ElMessageBox.confirm('确认删除该评论?', '提示', { type: 'warning' })
    .then(() => {
      deleteComment(row.id).then(() => {
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
