<template>
  <div class="blog-layout">
    <div class="blog-main">
      <div class="card">
        <div class="card-title">👋 关于作者</div>
        <div v-if="profile">
          <div style="display:flex;align-items:center;gap:24px;margin-bottom:20px;">
            <div class="profile-avatar" style="width:120px;height:120px;font-size:48px;">
              {{ profile.nickname ? profile.nickname.charAt(0).toUpperCase() : 'B' }}
            </div>
            <div>
              <h2 style="margin:0 0 8px;">{{ profile.nickname || '博主' }}</h2>
              <p style="color:#909399;margin:0 0 8px;">📧 {{ profile.email || '暂未设置' }}</p>
              <p style="color:#606266;line-height:1.8;">{{ profile.bio || '欢迎来到我的技术博客' }}</p>
            </div>
          </div>

          <div style="display:grid;grid-template-columns:repeat(3,1fr);gap:16px;margin:24px 0;">
            <div style="background:#ecf5ff;padding:20px;border-radius:8px;text-align:center;">
              <div style="font-size:32px;font-weight:700;color:#409eff;">{{ profile.articleCount || 0 }}</div>
              <div style="color:#606266;font-size:14px;">文章数量</div>
            </div>
            <div style="background:#f0f9eb;padding:20px;border-radius:8px;text-align:center;">
              <div style="font-size:32px;font-weight:700;color:#67c23a;">{{ profile.commentCount || 0 }}</div>
              <div style="color:#606266;font-size:14px;">评论数量</div>
            </div>
            <div style="background:#fdf6ec;padding:20px;border-radius:8px;text-align:center;">
              <div style="font-size:32px;font-weight:700;color:#e6a23c;">{{ profile.totalView || 0 }}</div>
              <div style="color:#606266;font-size:14px;">总浏览量</div>
            </div>
          </div>

          <h3 style="margin-top:30px;">✍ 想说的话</h3>
          <p style="color:#606266;line-height:1.8;">
            记录学习与工作，期待与你一起进步！
          </p>
          <p style="color:#606266;line-height:1.8;">
            欢迎通过留言板与我交流💕
          </p>
        </div>
      </div>

      <div class="card" v-if="userStore.token" v-loading="loading">
        <h3 style="margin:0 0 20px;font-size:18px;">✏️ 编辑个人信息</h3>
        <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
          <el-form-item label="用户名">
            <el-input v-model="form.username" disabled />
          </el-form-item>
          <el-form-item label="昵称" prop="nickname">
            <el-input v-model="form.nickname" />
          </el-form-item>
          <el-form-item label="邮箱" prop="email">
            <el-input v-model="form.email" />
          </el-form-item>
          <el-form-item label="手机号">
            <el-input v-model="form.phone" />
          </el-form-item>
          <el-form-item label="个人简介" prop="bio">
            <el-input v-model="form.bio" type="textarea" :rows="3" maxlength="500" show-word-limit />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="saving" @click="save">保存</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>

    <aside class="blog-side">
      <div class="card">
        <div class="card-title">📌 快速导航</div>
        <el-button type="primary" link @click="$router.push('/')">→ 返回首页</el-button><br />
        <el-button type="success" link @click="$router.push('/message')">→ 去留言板</el-button>
      </div>
    </aside>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '@/store/user'
import { getProfile, getUserInfo, updateUserInfo } from '@/api'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()
const profile = ref({})
const loading = ref(false)
const saving = ref(false)
const formRef = ref()
const form = reactive({ username: '', nickname: '', email: '', phone: '', bio: '' })
const rules = {
  nickname: [
    { min: 2, max: 50, message: '昵称长度需在2-50个字符之间', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ],
  bio: [
    { max: 500, message: '个人简介不能超过500个字符', trigger: 'blur' }
  ]
}

const fetchProfile = () => {
  getProfile().then(res => {
    profile.value = res || {}
  })
}

const fetchUserInfo = () => {
  loading.value = true
  getUserInfo().then(res => {
    if (res) {
      form.username = res.username || ''
      form.nickname = res.nickname || ''
      form.email = res.email || ''
      form.phone = res.phone || ''
      form.bio = res.bio || ''
    }
  }).finally(() => {
    loading.value = false
  })
}

const save = () => {
  formRef.value.validate(valid => {
    if (!valid) return
    saving.value = true
    updateUserInfo({
      nickname: form.nickname,
      email: form.email,
      phone: form.phone,
      bio: form.bio
    }).then(() => {
      ElMessage.success('更新成功')
      fetchProfile()
    }).finally(() => {
      saving.value = false
    })
  })
}

onMounted(() => {
  fetchProfile()
  if (userStore.token) {
    fetchUserInfo()
  }
})
</script>
