<template>
  <el-container style="height:100vh;">
    <el-aside width="220px" style="background:#304156;color:#fff;">
      <div style="padding:20px 16px;border-bottom:1px solid #1f2d3d;text-align:center;">
        <div style="font-size:18px;font-weight:700;">博客管理系统</div>
        <div style="font-size:12px;color:#9ea8b3;margin-top:4px;">Admin Panel</div>
      </div>
      <el-menu
        :default-active="activeMenu"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
        router
      >
        <el-menu-item index="/admin/dashboard">
          <el-icon><DataAnalysis /></el-icon>
          <span>数据看板</span>
        </el-menu-item>
        <el-menu-item index="/admin/article">
          <el-icon><Document /></el-icon>
          <span>文章管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/category">
          <el-icon><Folder /></el-icon>
          <span>分类管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/tag">
          <el-icon><PriceTag /></el-icon>
          <span>标签管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/comment">
          <el-icon><ChatDotRound /></el-icon>
          <span>留言评论</span>
        </el-menu-item>
        <el-menu-item index="/admin/password">
          <el-icon><Lock /></el-icon>
          <span>修改密码</span>
        </el-menu-item>
        <el-menu-item index="/" @click="goFront">
          <el-icon><Monitor /></el-icon>
          <span>前台首页</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header style="background:#fff;border-bottom:1px solid #eee;display:flex;align-items:center;justify-content:space-between;">
        <div style="font-size:16px;font-weight:600;">{{ $route.meta.title || '管理后台' }}</div>
        <div style="display:flex;align-items:center;gap:12px;">
          <span>👤 {{ userInfo?.nickname || userInfo?.username || 'admin' }}</span>
          <el-button size="small" type="danger" @click="logout">退出登录</el-button>
        </div>
      </el-header>
      <el-main style="background:#f5f7fa;">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { getAdminInfo, adminLogout } from '@/api'
import { ElMessage, ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const userInfo = ref({})
const activeMenu = computed(() => route.path)

const goFront = () => {
  router.push('/')
}

const logout = () => {
  ElMessageBox.confirm('确认退出登录吗?', '提示', {
    type: 'warning'
  }).then(() => {
    adminLogout().then(() => {
      userStore.logout()
      ElMessage.success('已退出登录')
      router.push('/admin/login')
    })
  }).catch(() => {})
}

onMounted(() => {
  getAdminInfo().then(res => {
    userInfo.value = res || {}
  }).catch(() => {})
})
</script>

<style scoped>
.el-menu { border-right: none !important; }
</style>
