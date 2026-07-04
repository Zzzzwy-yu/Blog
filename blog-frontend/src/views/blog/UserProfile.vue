<template>
  <div class="blog-layout">
    <div class="blog-main">
      <div class="card" v-loading="loading">
        <h2 style="margin:0 0 24px;font-size:22px;">个人信息</h2>
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
            <el-button @click="$router.push('/')">返回首页</el-button>
          </el-form-item>
        </el-form>
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
import { ref, reactive, onMounted } from 'vue'
import { getUserInfo, updateUserInfo } from '@/api'
import { ElMessage } from 'element-plus'

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
    }).finally(() => {
      saving.value = false
    })
  })
}

onMounted(() => {
  fetchUserInfo()
})
</script>