<template>
  <div class="blog-layout">
    <div class="blog-main">
      <div class="card">
        <div class="card-title">💬 留言板</div>
        <p style="color:#909399;font-size:14px;">欢迎在留言板留下您的足迹~</p>
        <template v-if="userStore.token">
          <el-form :model="form" style="margin:20px 0;" @submit.prevent="submitMsg">
            <el-input
              v-model="form.content"
              type="textarea"
              :rows="3"
              placeholder="想说什么就说什么吧..."
              maxlength="500"
              show-word-limit
            />
            <div style="margin-top:12px;text-align:right;">
              <el-button type="primary" @click="submitMsg">发表留言</el-button>
            </div>
          </el-form>
        </template>
        <div v-else class="empty-box" style="text-align:center;padding:20px;">
          <p>请先登录后再留言</p>
          <el-button type="primary" link @click="$router.push('/user/login')">去登录</el-button>
        </div>
        <div v-if="messageList.length === 0 && userStore.token" class="empty-box">暂无留言</div>
        <div v-for="m in messageList" :key="m.id" class="comment-item">
          <span class="comment-avatar">{{ getInitial(m.nickname) }}</span>
          <div class="comment-body">
            <span class="comment-user">{{ m.nickname }}</span>
            <span class="comment-time">{{ formatDate(m.createTime) }}</span>
            <div class="comment-content">{{ m.content }}</div>
            <div class="comment-actions">
              <span
                class="comment-action"
                @click="handleLike(m)"
                :style="{ color: m.liked ? '#ef4444' : '#909399' }"
              >
                ❤ {{ m.likeCount || 0 }}
              </span>
            </div>
          </div>
        </div>
        <div class="pagination-wrap" v-if="total > 5">
          <el-pagination
            v-model:current-page="pageNum"
            :page-size="5"
            :total="total"
            layout="prev, pager, next"
            @current-change="fetchMessages"
          />
        </div>
      </div>
    </div>

    <aside class="blog-side">
      <div class="card profile-card">
        <div class="profile-avatar">{{ initial }}</div>
        <div class="profile-name">{{ profile.nickname || '博主' }}</div>
        <div class="profile-bio">{{ profile.bio || '欢迎留言~' }}</div>
      </div>
    </aside>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { getMessageList, submitComment, getProfile, toggleCommentLike } from '@/api'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

const form = ref({ content: '' })
const messageList = ref([])
const pageNum = ref(1)
const total = ref(0)
const profile = ref({})

const initial = computed(() => {
  const name = profile.value.nickname || 'B'
  return name.charAt(0).toUpperCase()
})

const getInitial = name => {
  if (!name) return 'U'
  return name.charAt(0).toUpperCase()
}

const formatDate = d => {
  if (!d) return ''
  return String(d).replace('T', ' ').substring(0, 16)
}

const fetchMessages = () => {
  getMessageList({ pageNum: pageNum.value, pageSize: 5 }).then(res => {
    messageList.value = res?.list || []
    total.value = res?.total || 0
  })
}

const fetchProfile = () => {
  getProfile().then(res => {
    profile.value = res || {}
  })
}

const submitMsg = () => {
  if (!form.value.content) {
    ElMessage.warning('请填写留言内容')
    return
  }
  submitComment({ content: form.value.content }).then(() => {
    ElMessage.success('留言成功,等待审核')
    form.value = { content: '' }
    pageNum.value = 1
    fetchMessages()
  })
}

const handleLike = (message) => {
  if (!userStore.token) {
    ElMessage.warning('请先登录')
    router.push('/user/login')
    return
  }
  toggleCommentLike(message.id).then(res => {
    message.liked = res.liked
    message.likeCount = res.likeCount
  })
}

onMounted(() => {
  fetchProfile()
  fetchMessages()
})
</script>

<style scoped>
.comment-item {
  display: flex;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}
.comment-item:last-child {
  border-bottom: none;
}
.comment-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  font-size: 16px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.comment-body {
  margin-left: 12px;
  flex: 1;
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
</style>
