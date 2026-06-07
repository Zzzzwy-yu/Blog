<template>
  <div>
    <el-row :gutter="16" style="margin-bottom:16px;">
      <el-col :span="6">
        <el-card shadow="hover">
          <div style="display:flex;justify-content:space-between;align-items:center;">
            <div>
              <div style="color:#909399;font-size:13px;">文章总数</div>
              <div style="font-size:32px;font-weight:700;color:#409eff;margin-top:6px;">{{ data.articleCount }}</div>
            </div>
            <el-icon style="font-size:48px;color:#409eff;opacity:0.5;"><Document /></el-icon>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div style="display:flex;justify-content:space-between;align-items:center;">
            <div>
              <div style="color:#909399;font-size:13px;">分类数</div>
              <div style="font-size:32px;font-weight:700;color:#67c23a;margin-top:6px;">{{ data.categoryCount }}</div>
            </div>
            <el-icon style="font-size:48px;color:#67c23a;opacity:0.5;"><Folder /></el-icon>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div style="display:flex;justify-content:space-between;align-items:center;">
            <div>
              <div style="color:#909399;font-size:13px;">标签数</div>
              <div style="font-size:32px;font-weight:700;color:#e6a23c;margin-top:6px;">{{ data.tagCount }}</div>
            </div>
            <el-icon style="font-size:48px;color:#e6a23c;opacity:0.5;"><PriceTag /></el-icon>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div style="display:flex;justify-content:space-between;align-items:center;">
            <div>
              <div style="color:#909399;font-size:13px;">总评论</div>
              <div style="font-size:32px;font-weight:700;color:#f56c6c;margin-top:6px;">{{ data.commentCount }}</div>
            </div>
            <el-icon style="font-size:48px;color:#f56c6c;opacity:0.5;"><ChatDotRound /></el-icon>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16">
      <el-col :span="12">
        <el-card>
          <template #header><strong>📊 统计数据</strong></template>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="待审核评论">{{ data.pendingCommentCount }}</el-descriptions-item>
            <el-descriptions-item label="今日发布文章">{{ data.todayArticleCount }}</el-descriptions-item>
            <el-descriptions-item label="总浏览量">{{ data.totalViewCount }}</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header><strong>⚡ 快捷操作</strong></template>
          <div style="display:flex;flex-wrap:wrap;gap:12px;">
            <el-button type="primary" @click="$router.push('/admin/article/edit')"><el-icon><Edit /></el-icon> 写新文章</el-button>
            <el-button type="success" @click="$router.push('/admin/article')"><el-icon><Document /></el-icon> 文章管理</el-button>
            <el-button type="warning" @click="$router.push('/admin/comment')"><el-icon><ChatDotRound /></el-icon> 评论审核</el-button>
            <el-button type="info" @click="$router.push('/admin/category')"><el-icon><Folder /></el-icon> 分类管理</el-button>
            <el-button @click="$router.push('/admin/tag')"><el-icon><PriceTag /></el-icon> 标签管理</el-button>
            <el-button type="danger" @click="$router.push('/admin/password')"><el-icon><Lock /></el-icon> 修改密码</el-button>
          </div>
          <div style="margin-top:20px;padding:16px;background:#f5f7fa;border-radius:6px;color:#606266;font-size:13px;line-height:1.8;">
            💡 欢迎使用博客管理系统。你可以在此发布文章、管理分类和标签、审核访客留言。<br />
            前台访问地址: <el-link @click="$router.push('/')">/ 首页</el-link>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getDashboard } from '@/api'

const data = ref({})

onMounted(() => {
  getDashboard().then(res => {
    data.value = res || {}
  })
})
</script>
