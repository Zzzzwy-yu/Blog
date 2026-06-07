<template>
  <div style="min-height:100vh;background:linear-gradient(135deg,#667eea 0%,#764ba2 100%);display:flex;align-items:center;justify-content:center;">
    <div style="background:#fff;border-radius:12px;padding:40px;width:420px;box-shadow:0 12px 40px rgba(0,0,0,0.2);">
      <h1 style="margin:0 0 8px;text-align:center;color:#303133;">博客管理后台</h1>
      <p style="text-align:center;color:#909399;margin-bottom:28px;font-size:13px;">请使用管理员账号登录</p>
      <el-form :model="form" :rules="rules" ref="formRef" @submit.prevent="login">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名" size="large">
            <template #prefix><el-icon><User /></el-icon></template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码" size="large" show-password @keyup.enter="login">
            <template #prefix><el-icon><Lock /></el-icon></template>
          </el-input>
        </el-form-item>
        <el-button type="primary" size="large" style="width:100%;" :loading="loading" @click="login">登 录</el-button>
      </el-form>
      <div style="text-align:center;margin-top:20px;color:#909399;font-size:12px;">
        默认账号: admin / 密码: 123456
      </div>
      <div style="text-align:center;margin-top:10px;">
        <el-link type="primary" @click="$router.push('/')">← 返回博客首页</el-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { adminLogin } from '@/api'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref()
const loading = ref(false)
const form = reactive({ username: 'admin', password: '' })
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const login = () => {
  formRef.value.validate(valid => {
    if (!valid) return
    loading.value = true
    adminLogin(form).then(res => {
      userStore.setToken(res.token)
      userStore.setUserInfo({ username: form.username, nickname: res.nickname })
      ElMessage.success('登录成功')
      router.push('/admin/dashboard')
    }).finally(() => {
      loading.value = false
    })
  })
}
</script>
