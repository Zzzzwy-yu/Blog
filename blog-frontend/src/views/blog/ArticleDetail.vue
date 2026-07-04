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
            <span
              style="margin-left:12px;cursor:pointer;"
              @click="handleLike"
              :style="{ color: isLiked ? '#ef4444' : '#909399' }"
            >
              ❤ {{ article.likeCount || 0 }}
            </span>
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
          <div v-if="article?.contentType === 'typst'" class="article-detail typst-content">
            <div v-if="typstLoading" style="text-align:center;padding:40px;">
              <el-loading type="spinner" />
              <p style="margin-top:16px;color:#909399;">正在渲染 Typst 文档...</p>
            </div>
            <div v-else v-html="typstSvg"></div>
          </div>
          <div v-else class="article-detail" v-html="renderedContent"></div>
        </template>
      </div>

      <div class="card">
        <div class="card-title">💬 评论 ({{ total }})</div>
        <template v-if="userStore.token">
          <el-form :model="commentForm" style="margin-bottom:20px;" @submit.prevent="submitComment">
            <el-input
              v-model="commentForm.content"
              type="textarea"
              :rows="3"
              :placeholder="replyTarget ? '回复 ' + replyTarget.nickname + ' 的评论...' : '写下您的评论...'"
              maxlength="500"
              show-word-limit
            />
            <div style="margin-top:12px;text-align:right;">
              <el-button v-if="replyTarget" size="small" @click="cancelReply">取消回复</el-button>
              <el-button type="primary" @click="submitComment">发表评论</el-button>
            </div>
          </el-form>
        </template>
        <div v-else class="empty-box" style="text-align:center;padding:20px;">
          <p>请先登录后再评论</p>
          <el-button type="primary" link @click="$router.push('/user/login')">去登录</el-button>
        </div>
        <div v-if="commentList.length === 0 && userStore.token" class="empty-box">还没有评论,快来抢沙发~</div>

        <div v-for="c in commentList" :key="c.id" class="comment-item">
          <span class="comment-user">{{ c.nickname }}</span>
          <span class="comment-time">{{ formatDate(c.createTime) }}</span>
          <div class="comment-content">{{ c.content }}</div>
          <div class="comment-actions">
            <span
              class="comment-action"
              @click="handleCommentLike(c)"
              :style="{ color: c.liked ? '#ef4444' : '#909399' }"
            >
              ❤ {{ c.likeCount || 0 }}
            </span>
            <span v-if="userStore.token" class="comment-action" @click="replyTo(c)">
              💬 回复
            </span>
          </div>
          <div v-if="c.children && c.children.length > 0" class="comment-replies">
            <div v-for="child in c.children" :key="child.id" class="comment-reply">
              <span class="comment-user">{{ child.nickname }}</span>
              <span class="comment-time">{{ formatDate(child.createTime) }}</span>
              <div class="comment-content">{{ child.content }}</div>
              <div class="comment-actions">
                <span
                  class="comment-action"
                  @click="handleCommentLike(child)"
                  :style="{ color: child.liked ? '#ef4444' : '#909399' }"
                >
                  ❤ {{ child.likeCount || 0 }}
                </span>
                <span v-if="userStore.token" class="comment-action" @click="replyTo(child)">
                  💬 回复
                </span>
              </div>
            </div>
          </div>
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
import { ref, onMounted, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import MarkdownIt from 'markdown-it'
import { $typst } from '@myriaddreamin/typst.ts'
import { useUserStore } from '@/store/user'
import { getArticleDetail, getCommentList, submitComment as apiSubmit, toggleLike, getLikeStatus, toggleCommentLike } from '@/api'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const articleId = computed(() => route.params.id)
const loading = ref(false)
const article = ref(null)
const commentList = ref([])
const commentPage = ref(1)
const total = ref(0)
const typstSvg = ref('')
const typstLoading = ref(false)
const isLiked = ref(false)
const replyTarget = ref(null)

const md = new MarkdownIt({
  html: false,
  linkify: true,
  typographer: true,
  breaks: true
})

const renderedContent = computed(() => {
  if (!article.value || !article.value.content) return ''
  if (article.value.contentType === 'typst') return ''
  return md.render(article.value.content)
})

const renderTypst = async () => {
  if (!article.value || !article.value.content || article.value.contentType !== 'typst') return
  typstLoading.value = true
  try {
    const result = await $typst.svg({
      mainContent: article.value.content
    })
    if (typeof result === 'string') {
      typstSvg.value = result
    } else if (Array.isArray(result)) {
      typstSvg.value = result.join('')
    } else {
      typstSvg.value = '<div style="color:red;">Typst 渲染结果格式未知</div>'
    }
  } catch (e) {
    console.error('Typst render error:', e)
    typstSvg.value = '<div style="color:red;">Typst 渲染失败: ' + e.message + '</div>'
  } finally {
    typstLoading.value = false
  }
}

watch(() => [article.value?.content, article.value?.contentType], () => {
  renderTypst()
}, { immediate: true })

const commentForm = ref({ content: '', articleId: articleId.value, parentId: null })

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

const fetchLikeStatus = () => {
  if (!userStore.token) return
  getLikeStatus(articleId.value).then(res => {
    isLiked.value = res.liked
    if (article.value) {
      article.value.likeCount = res.likeCount
    }
  })
}

const handleLike = () => {
  if (!userStore.token) {
    ElMessage.warning('请先登录')
    router.push('/user/login')
    return
  }
  toggleLike(articleId.value).then(res => {
    isLiked.value = res.liked
    if (article.value) {
      article.value.likeCount = res.likeCount
    }
  })
}

const fetchComments = () => {
  getCommentList({ pageNum: commentPage.value, pageSize: 5, articleId: articleId.value }).then(res => {
    commentList.value = res?.list || []
    total.value = res?.total || 0
  })
}

const replyTo = (comment) => {
  replyTarget.value = comment
  commentForm.value.parentId = comment.id
}

const cancelReply = () => {
  replyTarget.value = null
  commentForm.value.parentId = null
}

const submitComment = () => {
  if (!commentForm.value.content) {
    ElMessage.warning('请填写评论内容')
    return
  }
  apiSubmit({
    articleId: articleId.value,
    content: commentForm.value.content,
    parentId: commentForm.value.parentId
  }).then(() => {
    ElMessage.success('评论发表成功')
    commentForm.value = { content: '', articleId: articleId.value, parentId: null }
    replyTarget.value = null
    commentPage.value = 1
    fetchComments()
  })
}

const handleCommentLike = (comment) => {
  if (!userStore.token) {
    ElMessage.warning('请先登录')
    router.push('/user/login')
    return
  }
  toggleCommentLike(comment.id).then(res => {
    comment.liked = res.liked
    comment.likeCount = res.likeCount
  })
}

onMounted(() => {
  fetchArticle()
  fetchComments()
  fetchLikeStatus()
})
</script>

<style scoped>
.article-detail :deep(img) {
  max-width: 100%;
  height: auto;
  border-radius: 8px;
  margin: 8px 0;
  box-sizing: border-box;
}
.article-detail :deep(table) {
  max-width: 100%;
  overflow-x: auto;
  display: block;
}
.comment-item {
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}
.comment-item:last-child {
  border-bottom: none;
}
.comment-user {
  font-weight: 600;
  color: #303133;
  font-size: 14px;
}
.comment-time {
  color: #909399;
  font-size: 12px;
  margin-left: 12px;
}
.comment-content {
  margin-top: 8px;
  color: #606266;
  line-height: 1.6;
  font-size: 14px;
}
.comment-actions {
  margin-top: 8px;
}
.comment-action {
  font-size: 13px;
  margin-right: 16px;
  cursor: pointer;
}
.comment-action:hover {
  opacity: 0.7;
}
.comment-replies {
  margin-top: 12px;
  padding-left: 24px;
  border-left: 2px solid #e4e7ed;
}
.comment-reply {
  padding: 10px 0;
}
.comment-reply:not(:last-child) {
  border-bottom: 1px dashed #f0f0f0;
}
</style>
