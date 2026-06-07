<template>
  <div class="blog-layout">
    <div class="blog-main">
      <div class="card" v-loading="loading">
        <template v-if="article">
          <h1 style="margin:0 0 8px;font-size:26px;">{{ article.title }}</h1>
          <div style="color:#909399;font-size:13px;margin-bottom:16px;">
            <span>📅 {{ formatDate(article.createTime) }}</span>
            <span v-if="article.categoryName" style="margin-left:12px;">📁 {{ article.categoryName }}</span>
            <span style="margin-left:12px;">👁 {{ article.viewCount || 0 }}</span>
          </div>
          <div v-if="article.tagList && article.tagList.length" style="margin-bottom:20px;">
            <span v-for="t in article.tagList" :key="t.id" class="tag-pill"># {{ t.name }}</span>
          </div>
          <div
            v-if="article.summary"
            style="background:#f5f7fa;padding:14px 18px;border-left:4px solid #409eff;margin-bottom:20px;border-radius:4px;color:#606266;"
          >
            <strong>摘要：</strong>{{ article.summary }}
          </div>
          <div class="article-detail" v-html="renderedContent"></div>
        </template>
      </div>

      <!-- 评论区 -->
      <div class="card">
        <div class="card-title">💬 评论 ({{ total }})</div>
        <el-form :model="commentForm" style="margin-bottom:20px;" @submit.prevent="submitComment">
          <el-row :gutter="12">
            <el-col :span="12">
              <el-form-item>
                <el-input v-model="commentForm.nickname" placeholder="您的昵称" maxlength="20" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item>
                <el-input v-model="commentForm.email" placeholder="邮箱(可选)" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-input
            v-model="commentForm.content"
            type="textarea"
            :rows="3"
            placeholder="写下您的评论..."
            maxlength="500"
            show-word-limit
          />
          <div style="margin-top:12px;text-align:right;">
            <el-button type="primary" @click="submitComment">发表评论</el-button>
          </div>
        </el-form>
        <div v-if="commentList.length === 0" class="empty-box">还没有评论,快来抢沙发~</div>
        <div v-for="c in commentList" :key="c.id" class="comment-item">
          <span class="comment-user">{{ c.nickname }}</span>
          <span class="comment-time">{{ formatDate(c.createTime) }}</span>
          <div class="comment-content">{{ c.content }}</div>
        </div>
        <div class="pagination-wrap" v-if="total > 5">
          <el-pagination
            v-model:current-page="commentPage"
            :page-size="5"
            :total="total"
            layout="prev, pager, next"
            @current-change="fetchComments"
          />
        </div>
      </div>
    </div>

    <aside class="blog-side">
      <div class="card">
        <div class="card-title">📄 导航</div>
        <el-button type="primary" link @click="$router.push('/')">← 返回首页</el-button>
      </div>
    </aside>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import MarkdownIt from 'markdown-it'
import { getArticleDetail, getCommentList, submitComment as apiSubmit } from '@/api'
import { ElMessage } from 'element-plus'

const route = useRoute()
const articleId = computed(() => route.params.id)
const loading = ref(false)
const article = ref(null)
const commentList = ref([])
const commentPage = ref(1)
const total = ref(0)

const md = new MarkdownIt({
  html: false,
  linkify: true,
  typographer: true,
  breaks: true
})

const renderedContent = computed(() => {
  if (!article.value || !article.value.content) return ''
  return md.render(article.value.content)
})

const commentForm = ref({ nickname: '', email: '', content: '', articleId: articleId.value })

const formatDate = d => {
  if (!d) return ''
  return String(d).replace('T', ' ').substring(0, 16)
}

const fetchArticle = () => {
  loading.value = true
  getArticleDetail(articleId.value).then(res => {
    article.value = res
  }).finally(() => {
    loading.value = false
  })
}

const fetchComments = () => {
  getCommentList({ pageNum: commentPage.value, pageSize: 5, articleId: articleId.value }).then(res => {
    commentList.value = res?.list || []
    total.value = res?.total || 0
  })
}

const submitComment = () => {
  if (!commentForm.value.nickname || !commentForm.value.content) {
    ElMessage.warning('请填写昵称和内容')
    return
  }
  apiSubmit({
    articleId: articleId.value,
    nickname: commentForm.value.nickname,
    email: commentForm.value.email,
    content: commentForm.value.content
  }).then(() => {
    ElMessage.success('提交成功,等待审核')
    commentForm.value = { nickname: '', email: '', content: '' }
    commentPage.value = 1
    fetchComments()
  })
}

onMounted(() => {
  fetchArticle()
  fetchComments()
})
</script>
