<template>
  <div class="blog-layout">
    <!-- 主内容 -->
    <div class="blog-main">
      <!-- 搜索栏 -->
      <div class="card search-bar">
        <el-input
          v-model="keyword"
          placeholder="搜索文章标题/摘要..."
          clearable
          @keyup.enter="handleSearch"
          @clear="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="resetFilter">重置</el-button>
      </div>

      <!-- 筛选提示 -->
      <div v-if="currentCategoryId || currentTagId" class="card">
        <div style="color:#909399;font-size:13px;margin-bottom:8px;">当前筛选:</div>
        <el-tag
          v-if="currentCategoryId"
          closable
          type="primary"
          style="margin-right:8px;"
          @close="clearCategory"
        >
          分类: {{ currentCategoryName }}
        </el-tag>
        <el-tag
          v-if="currentTagId"
          closable
          type="success"
          @close="clearTag"
        >
          标签: {{ currentTagName }}
        </el-tag>
      </div>

      <!-- 文章列表 -->
      <div class="card" v-loading="loading">
        <div v-if="articleList.length === 0" class="empty-box">暂无文章</div>
        <div
          v-for="article in articleList"
          :key="article.id"
          class="article-item"
          @click="goDetail(article.id)"
        >
          <h3 class="article-title">{{ article.title }}</h3>
          <div class="article-meta">
            <span>📅 {{ formatDate(article.createTime) }}</span>
            <span v-if="article.categoryName" style="margin-left:12px;">
              📁 {{ article.categoryName }}
            </span>
            <span style="margin-left:12px;">👁 {{ article.viewCount || 0 }}</span>
            <span style="margin-left:12px;">💬 {{ article.commentCount || 0 }}</span>
          </div>
          <p class="article-summary">{{ article.summary }}</p>
          <div v-if="article.tagList && article.tagList.length" class="article-tags">
            <span
              v-for="tag in article.tagList"
              :key="tag.id"
              class="tag-pill"
              @click.stop="filterByTag(tag)"
            >
              # {{ tag.name }}
            </span>
          </div>
        </div>
        <div class="pagination-wrap" v-if="total > pageSize">
          <el-pagination
            v-model:current-page="pageNum"
            :page-size="pageSize"
            :total="total"
            layout="prev, pager, next, total"
            @current-change="fetchList"
          />
        </div>
      </div>
    </div>

    <!-- 侧栏 -->
    <aside class="blog-side">
      <!-- 个人卡片 -->
      <div class="card profile-card">
        <div class="profile-avatar">{{ initial }}</div>
        <div class="profile-name">{{ profile.nickname || '博主' }}</div>
        <div class="profile-bio">{{ profile.bio || '欢迎访问我的技术博客' }}</div>
        <div class="profile-stats">
          <div class="profile-stat">
            <div class="profile-stat-num">{{ profile.articleCount || 0 }}</div>
            <div class="profile-stat-label">文章</div>
          </div>
          <div class="profile-stat">
            <div class="profile-stat-num">{{ profile.commentCount || 0 }}</div>
            <div class="profile-stat-label">评论</div>
          </div>
          <div class="profile-stat">
            <div class="profile-stat-num">{{ profile.totalView || 0 }}</div>
            <div class="profile-stat-label">浏览</div>
          </div>
        </div>
      </div>

      <!-- 分类 -->
      <div class="card">
        <div class="card-title">📂 文章分类</div>
        <div v-if="categoryList.length === 0" style="color:#909399;font-size:13px;">暂无分类</div>
        <div
          v-for="c in categoryList"
          :key="c.id"
          class="cat-item"
          @click="filterByCategory(c)"
        >
          <span>{{ c.name }}</span>
        </div>
      </div>

      <!-- 标签 -->
      <div class="card">
        <div class="card-title">🏷 文章标签</div>
        <div v-if="tagList.length === 0" style="color:#909399;font-size:13px;">暂无标签</div>
        <div v-else style="display:flex;flex-wrap:wrap;gap:8px;">
          <span
            v-for="t in tagList"
            :key="t.id"
            class="tag-pill"
            style="margin-right:0;"
            @click="filterByTag(t)"
          >
            # {{ t.name }}
          </span>
        </div>
      </div>
    </aside>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import {
  getArticleList, getCategoryList, getTagList, getProfile
} from '@/api'

const router = useRouter()

const loading = ref(false)
const keyword = ref('')
const pageNum = ref(1)
const pageSize = ref(5)
const total = ref(0)
const articleList = ref([])
const categoryList = ref([])
const tagList = ref([])
const profile = ref({})

const currentCategoryId = ref(null)
const currentTagId = ref(null)
const currentCategoryName = ref('')
const currentTagName = ref('')

const initial = computed(() => {
  const name = profile.value.nickname || 'B'
  return name.charAt(0).toUpperCase()
})

const formatDate = d => {
  if (!d) return ''
  return String(d).replace('T', ' ').substring(0, 16)
}

const fetchProfile = () => {
  getProfile().then(res => {
    profile.value = res || {}
  })
}

const fetchCategories = () => {
  getCategoryList().then(res => {
    categoryList.value = res || []
  })
}

const fetchTags = () => {
  getTagList().then(res => {
    tagList.value = res || []
  })
}

const fetchList = () => {
  loading.value = true
  const params = {
    pageNum: pageNum.value,
    pageSize: pageSize.value,
    keyword: keyword.value,
    categoryId: currentCategoryId.value,
    tagId: currentTagId.value
  }
  getArticleList(params).then(res => {
    articleList.value = res?.list || []
    total.value = res?.total || 0
  }).finally(() => {
    loading.value = false
  })
}

const handleSearch = () => {
  pageNum.value = 1
  fetchList()
}

const resetFilter = () => {
  keyword.value = ''
  currentCategoryId.value = null
  currentTagId.value = null
  pageNum.value = 1
  fetchList()
}

const filterByCategory = cat => {
  currentCategoryId.value = cat.id
  currentCategoryName.value = cat.name
  currentTagId.value = null
  pageNum.value = 1
  fetchList()
}

const filterByTag = tag => {
  currentTagId.value = tag.id
  currentTagName.value = tag.name
  currentCategoryId.value = null
  pageNum.value = 1
  fetchList()
}

const clearCategory = () => {
  currentCategoryId.value = null
  fetchList()
}

const clearTag = () => {
  currentTagId.value = null
  fetchList()
}

const goDetail = id => {
  router.push('/article/' + id)
}

onMounted(() => {
  fetchProfile()
  fetchCategories()
  fetchTags()
  fetchList()
})
</script>
