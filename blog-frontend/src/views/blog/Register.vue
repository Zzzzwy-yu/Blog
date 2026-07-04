<template>
  <div style="min-height:100vh;background:linear-gradient(135deg,#667eea 0%,#764ba2 100%);display:flex;align-items:center;justify-content:center;">
    <div style="background:#fff;border-radius:12px;padding:40px;width:420px;box-shadow:0 12px 40px rgba(0,0,0,0.2);">
      <h1 style="margin:0 0 8px;text-align:center;color:#303133;">用户注册</h1>
      <p style="text-align:center;color:#909399;margin-bottom:28px;font-size:13px;">创建您的账号</p>
      <el-form :model="form" :rules="rules" ref="formRef" @submit.prevent="register">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名" size="large">
            <template #prefix><el-icon><User /></el-icon></template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码" size="large" show-password>
            <template #prefix><el-icon><Lock /></el-icon></template>
          </el-input>
        </el-form-item>
        <el-form-item prop="confirmPassword">
          <el-input v-model="form.confirmPassword" type="password" placeholder="确认密码" size="large" show-password>
            <template #prefix><el-icon><Lock /></el-icon></template>
          </el-input>
        </el-form-item>
        <el-form-item prop="nickname">
          <el-input v-model="form.nickname" placeholder="昵称" size="large">
            <template #prefix><el-icon><UserFilled /></el-icon></template>
          </el-input>
        </el-form-item>
        <el-form-item prop="email">
          <el-input v-model="form.email" placeholder="邮箱(可选)" size="large">
            <template #prefix><el-icon><Message /></el-icon></template>
          </el-input>
        </el-form-item>
        <el-button type="primary" size="large" style="width:100%;" :loading="loading" @click="register">注 册</el-button>
      </el-form>
      <div style="text-align:center;margin-top:20px;">
        <el-link type="primary" @click="$router.push('/user/login')">已有账号？去登录</el-link>
      </div>
      <div style="text-align:center;margin-top:10px;">
        <el-link type="info" @click="$router.push('/')">← 返回博客首页</el-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { userRegister } from '@/api'
import { ElMessage } from 'element-plus'

const router = useRouter()
const formRef = ref()
const loading = ref(false)
const form = reactive({ username: '', password: '', confirmPassword: '', nickname: '', email: '' })

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== form.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 50, message: '用户名长度需在3-50个字符之间', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 100, message: '密码长度需在6-100个字符之间', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 2, max: 50, message: '昵称长度需在2-50个字符之间', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ]
}

const register = () => {
  formRef.value.validate(valid => {
    if (!valid) return
    loading.value = true
    userRegister({
      username: form.username,
      password: form.password,
      nickname: form.nickname,
      email: form.email
    }).then(() => {
      ElMessage.success('注册成功，请登录')
      router.push('/user/login')
    }).finally(() => {
      loading.value = false
    })
  })
}
</script>