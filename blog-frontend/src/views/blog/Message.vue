<template>
  <div class="blog-layout">
    <div class="blog-main">
      <div class="card">
        <div class="card-title">💬 留言板</div>
        <p style="color:#909399;font-size:14px;">欢迎在留言板留下您的足迹~</p>
        <el-form :model="form" style="margin:20px 0;" @submit.prevent="submitMsg">
          <el-row :gutter="12">
            <el-col :span="12">
              <el-form-item>
                <el-input v-model="form.nickname" placeholder="您的昵称" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item>
                <el-input v-model="form.email" placeholder="邮箱(可选)" />
              </el-form-item>
            </el-col>
          </el-row>
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
        <div v-if="messageList.length === 0" class="empty-box">暂无留言</div>
        <div v-for="m in messageList" :key="m.id" class="comment-item">
          <span class="comment-user">{{ m.nickname }}</span>
          <span class="comment-time">{{ formatDate(m.createTime) }}</span>
          <div class="comment-content">{{ m.content }}</div>
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
        <div class="profile-avatar">B</div>
        <div class="profile-name">{{ profile.nickname || '博主' }}</div>
        <div class="profile-bio">{{ profile.bio || '欢迎留言~' }}</div>
      </div>
    </aside>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getMessageList, submitComment, getProfile } from '@/api'
import { ElMessage } from 'element-plus'

const form = ref({ nickname: '', email: '', content: '' })
const messageList = ref([])
const pageNum = ref(1)
const total = ref(0)
const profile = ref({})

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
  if (!form.value.nickname || !form.value.content) {
    ElMessage.warning('请填写昵称和内容')
    return
  }
  submitComment(form.value).then(() => {
    ElMessage.success('留言成功,等待审核')
    form.value = { nickname: '', email: '', content: '' }
    pageNum.value = 1
    fetchMessages()
  })
}

onMounted(() => {
  fetchProfile()
  fetchMessages()
})
</script>
